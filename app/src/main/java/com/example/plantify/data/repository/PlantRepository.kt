package com.example.plantify.data.repository

import com.example.plantify.data.local.dao.PlantDao
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.local.entity.TaskScheduleEntity
import com.example.plantify.data.remote.SupabaseConfig
import com.example.plantify.data.remote.model.KebunkuDto
import com.example.plantify.data.remote.model.TanamanKatalogDto
import com.example.plantify.data.remote.model.JadwalTugasDto
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import com.example.plantify.data.remote.AiService

class PlantRepository(
    private val plantDao: PlantDao,
    private val aiService: AiService
) {

    val allCatalog: Flow<List<PlantCatalogEntity>> = plantDao.getAllCatalog()
    val myPlants: Flow<List<MyPlantEntity>> = plantDao.getMyPlants()
    val allSchedules: Flow<List<TaskScheduleEntity>> = plantDao.getAllSchedules()

    suspend fun getCatalogById(id: Int): PlantCatalogEntity? = plantDao.getCatalogById(id)

    suspend fun addPlantType(plant: PlantCatalogEntity) {
        plantDao.insertSingleCatalog(plant)
        syncWithSupabase()
    }

    suspend fun addPlantWithSchedules(plant: MyPlantEntity, schedules: List<TaskScheduleEntity>) {
        val id = plantDao.insertMyPlant(plant).toInt()
        schedules.forEach {
            plantDao.insertSchedule(it.copy(id_kebun = id))
        }
        // Sinkronisasi setelah data masuk
        syncWithSupabase()
    }

    suspend fun addPlant(plant: MyPlantEntity): Long {
        val id = plantDao.insertMyPlant(plant)
        syncWithSupabase()
        return id
    }

    suspend fun updateSchedule(schedule: TaskScheduleEntity) {
        plantDao.updateSchedule(schedule)
        syncWithSupabase()
    }

    suspend fun syncWithSupabase() {
        val supabase = SupabaseConfig.supabase

        // 1. Ambil data katalog dari Supabase
        try {
            val response = supabase.postgrest.from("tanaman_katalog").select()
            val remoteCatalog = response.decodeAs<List<TanamanKatalogDto>>()
            val localCatalog = remoteCatalog.map {
                PlantCatalogEntity(
                    id_tanaman = it.id_tanaman ?: 0,
                    nama_tanaman = it.nama_tanaman,
                    difficulty = it.difficulty,
                    durasi_panen = it.durasi_panen,
                    interval_siram = it.interval_siram,
                    interval_pupuk = it.interval_pupuk,
                    emoji_icon = it.emoji_icon
                )
            }
            plantDao.insertCatalog(localCatalog)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Kirim data lokal ke Supabase
        try {
            val localPlants = plantDao.getMyPlants().first()
            val plantDtos = localPlants.map {
                KebunkuDto(
                    id_kebun = if (it.id_kebun == 0) null else it.id_kebun,
                    id_tanaman = it.id_tanaman,
                    tanggal_mulai_tanam = it.tanggal_mulai_tanam,
                    nama_pot = it.nama_pot,
                    progress_persen = it.progress_persen,
                    next_watering = it.next_watering,
                    status_tanaman = it.status_tanaman
                )
            }
            if (plantDtos.isNotEmpty()) {
                supabase.postgrest.from("kebunku").upsert(plantDtos)
            }

            // Sinkronisasi jadwal tugas
            val localSchedules = plantDao.getAllSchedules().first()
            val scheduleDtos = localSchedules.map {
                JadwalTugasDto(
                    id_tugas = if (it.id_tugas == 0) null else it.id_tugas,
                    id_kebun = it.id_kebun,
                    jenis_tugas = it.jenis_tugas,
                    waktu_eksekusi = it.waktu_eksekusi,
                    status_tugas = it.status_tugas
                )
            }
            if (scheduleDtos.isNotEmpty()) {
                supabase.postgrest.from("jadwal_tugas").upsert(scheduleDtos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun generateScheduleWithAI(plantName: String, condition: String, idKebun: Int): List<TaskScheduleEntity> {
        return try {
            val jsonResponse = aiService.generateCareSchedule(plantName, condition)
            if (jsonResponse != null) {
                val jsonObj = org.json.JSONObject(jsonResponse)
                val tasksArray = jsonObj.getJSONArray("tasks")
                val schedules = mutableListOf<TaskScheduleEntity>()

                for (i in 0 until tasksArray.length()) {
                    val task = tasksArray.getJSONObject(i)
                    val type = task.getString("type")
                    val time = task.getString("time")

                    schedules.add(
                        TaskScheduleEntity(
                            id_kebun = idKebun,
                            jenis_tugas = type,
                            waktu_eksekusi = time,
                            status_tugas = "Pending"
                        )
                    )
                }
                schedules
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun saveSchedules(schedules: List<TaskScheduleEntity>) {
        plantDao.insertSchedules(schedules)
    }
    suspend fun generatePlantInfo(plantName: String): String? {
        return aiService.generatePlantInfo(plantName)
    }
}