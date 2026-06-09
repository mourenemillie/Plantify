package com.example.plantify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.launch
import com.example.plantify.data.local.dao.PlantDao
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.local.entity.TaskScheduleEntity
import com.example.plantify.data.local.entity.NotificationLogEntity

@Database(
    entities = [
        PlantCatalogEntity::class,
        MyPlantEntity::class,
        TaskScheduleEntity::class,
        NotificationLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        fun getDatabase(context: Context): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plant_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Insert initial dummy data
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                            INSTANCE?.plantDao()?.let { dao ->
                                // Insert a dummy plant
                                val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                val dateStr = format.format(java.util.Date())
                                
                                val dummyPlant = MyPlantEntity(
                                    id_tanaman = 1, // Assume Tomato is ID 1 in catalog
                                    tanggal_mulai_tanam = dateStr,
                                    nama_pot = "Tomato (Balcony)",
                                    progress_persen = 10f,
                                    next_watering = "16:00",
                                    status_tanaman = "Sehat"
                                )
                                val newId = dao.insertMyPlant(dummyPlant)
                                
                                // Insert dummy schedules for it
                                val tasks = listOf(
                                    TaskScheduleEntity(
                                        id_kebun = newId.toInt(),
                                        jenis_tugas = "Penyiraman",
                                        waktu_eksekusi = "07:00",
                                        status_tugas = "Pending"
                                    ),
                                    TaskScheduleEntity(
                                        id_kebun = newId.toInt(),
                                        jenis_tugas = "Pemupukan",
                                        waktu_eksekusi = "16:00",
                                        status_tugas = "Pending"
                                    )
                                )
                                dao.insertSchedules(tasks)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
