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

class PlantRepository(private val plantDao: PlantDao) {

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
        // Sync after adding
        syncWithSupabase()
    }

    suspend fun updateSchedule(schedule: TaskScheduleEntity) {
        plantDao.updateSchedule(schedule)
        syncWithSupabase()
    }

    suspend fun syncWithSupabase() {
        val supabase = SupabaseConfig.supabase
        
        // 1. Sync Catalog (Pull from Supabase)
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

        // 2. Sync My Plants (Bidirectional / Upsert)
        try {
            // Push local to remote
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

            // Sync Schedules
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
}
