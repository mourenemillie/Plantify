package com.example.plantify.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifikasi_log",
    foreignKeys = [
        ForeignKey(
            entity = TaskScheduleEntity::class,
            parentColumns = ["id_tugas"],
            childColumns = ["id_tugas"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_tugas")]
)
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id_notif: Int = 0,
    val id_tugas: Int,
    val judul_notif: String,
    val pesan_notif: String,
    val waktu_tampil: String,
    val is_read: Boolean = false
)
