package com.example.plantify.data.local.dao

import androidx.room.*
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.local.entity.TaskScheduleEntity
import com.example.plantify.data.local.entity.NotificationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    // Catalog
    @Query("SELECT * FROM tanaman_katalog")
    fun getAllCatalog(): Flow<List<PlantCatalogEntity>>

    @Query("SELECT * FROM tanaman_katalog WHERE id_tanaman = :id")
    suspend fun getCatalogById(id: Int): PlantCatalogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalog(catalog: List<PlantCatalogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleCatalog(plant: PlantCatalogEntity)

    // My Plants
    @Query("SELECT * FROM kebunku")
    fun getMyPlants(): Flow<List<MyPlantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyPlant(plant: MyPlantEntity): Long

    @Delete
    suspend fun deleteMyPlant(plant: MyPlantEntity)

    // Schedules
    @Query("SELECT * FROM jadwal_tugas WHERE id_kebun = :idKebun")
    fun getSchedulesForPlant(idKebun: Int): Flow<List<TaskScheduleEntity>>

    @Query("SELECT * FROM jadwal_tugas")
    fun getAllSchedules(): Flow<List<TaskScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: TaskScheduleEntity)

    @Update
    suspend fun updateSchedule(schedule: TaskScheduleEntity)

    // Notifications
    @Query("SELECT * FROM notifikasi_log ORDER BY waktu_tampil DESC")
    fun getNotifications(): Flow<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationLogEntity)
}
