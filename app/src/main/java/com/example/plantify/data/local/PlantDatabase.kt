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
    version = 3,
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
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                INSTANCE?.plantDao()?.let { dao ->
                                    // Seed semua 8 tanaman ke katalog
                                    val fullCatalog = listOf(
                                        PlantCatalogEntity(id_tanaman = 1, nama_tanaman = "Tomato", difficulty = "Easy", durasi_panen = 70, interval_siram = 2, interval_pupuk = 7, emoji_icon = "🍅"),
                                        PlantCatalogEntity(id_tanaman = 2, nama_tanaman = "Red Chili", difficulty = "Medium", durasi_panen = 85, interval_siram = 3, interval_pupuk = 10, emoji_icon = "🌶️"),
                                        PlantCatalogEntity(id_tanaman = 3, nama_tanaman = "Spinach", difficulty = "Easy", durasi_panen = 45, interval_siram = 1, interval_pupuk = 7, emoji_icon = "🥬"),
                                        PlantCatalogEntity(id_tanaman = 4, nama_tanaman = "Mustard Greens", difficulty = "Easy", durasi_panen = 35, interval_siram = 1, interval_pupuk = 7, emoji_icon = "🥬"),
                                        PlantCatalogEntity(id_tanaman = 5, nama_tanaman = "Lettuce", difficulty = "Easy", durasi_panen = 50, interval_siram = 1, interval_pupuk = 7, emoji_icon = "🥗"),
                                        PlantCatalogEntity(id_tanaman = 6, nama_tanaman = "Green Onion", difficulty = "Easy", durasi_panen = 70, interval_siram = 2, interval_pupuk = 14, emoji_icon = "🌿"),
                                        PlantCatalogEntity(id_tanaman = 7, nama_tanaman = "Bell Pepper", difficulty = "Medium", durasi_panen = 80, interval_siram = 3, interval_pupuk = 10, emoji_icon = "🫑"),
                                        PlantCatalogEntity(id_tanaman = 8, nama_tanaman = "Cucumber", difficulty = "Easy", durasi_panen = 55, interval_siram = 2, interval_pupuk = 7, emoji_icon = "🥒")
                                    )
                                    dao.insertCatalog(fullCatalog)

                                    // Seed dummy plant (Tomato)
                                    val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                    val dateStr = format.format(java.util.Date())
                                    val dummyPlant = MyPlantEntity(
                                        id_tanaman = 1,
                                        tanggal_mulai_tanam = dateStr,
                                        nama_pot = "Tomato",
                                        progress_persen = 10f,
                                        next_watering = "08:00",
                                        status_tanaman = "Sehat"
                                    )
                                    val newId = dao.insertMyPlant(dummyPlant)

                                    // Seed schedules untuk dummy plant
                                    val tasks = listOf(
                                        TaskScheduleEntity(id_kebun = newId.toInt(), jenis_tugas = "Penyiraman", waktu_eksekusi = "07:00", status_tugas = "Pending"),
                                        TaskScheduleEntity(id_kebun = newId.toInt(), jenis_tugas = "Pemupukan", waktu_eksekusi = "16:00", status_tugas = "Pending")
                                    )
                                    dao.insertSchedules(tasks)
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}