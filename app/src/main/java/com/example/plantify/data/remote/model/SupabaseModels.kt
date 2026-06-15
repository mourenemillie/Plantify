package com.example.plantify.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class TanamanKatalogDto(
    val id_tanaman: Int? = null,
    val nama_tanaman: String,
    val difficulty: String?,
    val durasi_panen: Int?,
    val interval_siram: Int?,
    val interval_pupuk: Int?,
    val emoji_icon: String?
)

@Serializable
data class KebunkuDto(
    val id_kebun: Int? = null,
    val id_tanaman: Int,
    val tanggal_mulai_tanam: String,
    val nama_pot: String?,
    val progress_persen: Float,
    val next_watering: String?,
    val status_tanaman: String?
)

@Serializable
data class JadwalTugasDto(
    val id_tugas: Int? = null,
    val id_kebun: Int,
    val jenis_tugas: String,
    val waktu_eksekusi: String,
    val status_tugas: String
)

@Serializable
data class NotifikasiLogDto(
    val id_notif: Int? = null,
    val id_tugas: Int,
    val judul_notif: String,
    val pesan_notif: String,
    val waktu_tampil: String,
    val is_read: Boolean = false
)
