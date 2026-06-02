package com.example.plantify.data.repository

import com.example.plantify.data.local.dao.PlantDao
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.local.entity.TaskScheduleEntity
import com.example.plantify.data.remote.SupabaseConfig
import com.example.plantify.data.remote.model.KebunkuDto
import com.example.plantify.data.remote.model.TanamanKatalogDto
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PlantRepository(private val plantDao: PlantDao) {

    val allCatalog: Flow<List<PlantCatalogEntity>> = plantDao.getAllCatalog()
    val myPlants: Flow<List<MyPlantEntity>> = plantDao.getMyPlants()
    val allSchedules: Flow<List<TaskScheduleEntity>> = plantDao.getAllSchedules()

    suspend fun addPlant(plant: MyPlantEntity) {
        val id = plantDao.insertMyPlant(plant)
        // Optionally trigger immediate sync or let WorkManager handle it
    }

    suspend fun syncWithSupabase() {
        val supabase = SupabaseConfig.supabase
        
        // 1. Sync Catalog (Pull from Supabase)
        try {
            val remoteCatalog = supabase.from("tanaman_katalog").select().decodeList<TanamanKatalogDto>()
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

        // 2. Sync My Plants (Push to Supabase)
        try {
            val localPlants = plantDao.getMyPlants().first()
            val dtos = localPlants.map {
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
            if (dtos.isNotEmpty()) {
                supabase.from("kebunku").upsert(dtos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Add similar logic for tasks and notifications if needed
    }
}
