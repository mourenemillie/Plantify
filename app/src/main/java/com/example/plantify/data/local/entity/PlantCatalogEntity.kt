package com.example.plantify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tanaman_katalog")
data class PlantCatalogEntity(
    @PrimaryKey(autoGenerate = true)
    val id_tanaman: Int = 0,
    val nama_tanaman: String,
    val difficulty: String?,
    val durasi_panen: Int?,
    val interval_siram: Int?,
    val interval_pupuk: Int?,
    val emoji_icon: String?
)
