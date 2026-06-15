package com.example.plantify.data.models

data class Plant(
    val id: String,
    val speciesId: String,
    val name: String,
    val nickname: String? = null,
    val datePlanted: String, // Changed to String for simpler dummy data
    val currentDay: Int,
    val totalDays: Int,
    val progress: Float,
    val nextWatering: String,
    val stages: List<String>,
    val currentStageIndex: Int,
    val estimateHarvestDate: String,
    val emoji: String = "🌱"
)
