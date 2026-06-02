package com.example.plantify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 1,
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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
