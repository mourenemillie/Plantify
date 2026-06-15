package com.example.plantify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "kebunku")
data class MyPlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id_kebun: Int = 0,
    val id_tanaman: Int,
    val tanggal_mulai_tanam: String,
    val nama_pot: String?,
    val progress_persen: Float,
    val next_watering: String?,
    val status_tanaman: String?
)
