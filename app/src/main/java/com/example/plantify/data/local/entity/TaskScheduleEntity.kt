package com.example.plantify.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "jadwal_tugas",
    foreignKeys = [
        ForeignKey(
            entity = MyPlantEntity::class,
            parentColumns = ["id_kebun"],
            childColumns = ["id_kebun"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_kebun")]
)
data class TaskScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id_tugas: Int = 0,
    val id_kebun: Int,
    val jenis_tugas: String,
    val waktu_eksekusi: String,
    val status_tugas: String
)
