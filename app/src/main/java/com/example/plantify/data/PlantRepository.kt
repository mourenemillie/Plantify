package com.example.plantify.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.concurrent.TimeUnit

// Data tanaman yang sudah ditanam
data class PlantEntry(
    val id: String,
    val name: String,
    val emoji: String,
    val totalDays: Int,
    val plantingYear: Int,
    val plantingMonth: Int,  // 1–12
    val plantingDay: Int,
    val location: String,
    val wateringFrequency: String,
    val stages: List<String>
)

object PlantRepository {

    private val _plants = MutableStateFlow<List<PlantEntry>>(emptyList())
    val plants: StateFlow<List<PlantEntry>> = _plants.asStateFlow()

    init {
        _plants.value = listOf(
            PlantEntry(
                id = "cherry_tomato",
                name = "Cherry Tomato",
                emoji = "🍅",
                totalDays = 70,
                plantingYear = 2026, plantingMonth = 4, plantingDay = 24,
                location = "Backyard",
                wateringFrequency = "Every 2 days",
                stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit")
            ),
            PlantEntry(
                id = "red_chili",
                name = "Red Chili",
                emoji = "🌶️",
                totalDays = 85,
                plantingYear = 2026, plantingMonth = 4, plantingDay = 17,
                location = "Garden bed",
                wateringFrequency = "Every 3 days",
                stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit")
            ),
            PlantEntry(
                id = "spinach",
                name = "Spinach",
                emoji = "🥬",
                totalDays = 45,
                plantingYear = 2026, plantingMonth = 5, plantingDay = 1,
                location = "Balcony pot",
                wateringFrequency = "Every day",
                stages = listOf("Seed", "Sprout", "Veg", "Harvest")
            )
        )
    }

    fun addPlant(entry: PlantEntry) {
        _plants.value = _plants.value + entry
    }
    fun calcCurrentDay(entry: PlantEntry): Int {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val planting = Calendar.getInstance().apply {
            set(Calendar.YEAR, entry.plantingYear)
            set(Calendar.MONTH, entry.plantingMonth - 1)
            set(Calendar.DAY_OF_MONTH, entry.plantingDay)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val diff = TimeUnit.MILLISECONDS.toDays(today.timeInMillis - planting.timeInMillis).toInt()
        return diff.coerceIn(0, entry.totalDays)
    }

    fun calcProgress(entry: PlantEntry): Float {
        val currentDay = calcCurrentDay(entry)
        return (currentDay.toFloat() / entry.totalDays).coerceIn(0f, 1f)
    }

    fun calcStageIndex(entry: PlantEntry): Int {
        val progress = calcProgress(entry)
        val stageCount = entry.stages.size
        return if (progress >= 1f) stageCount - 1
        else (progress * stageCount).toInt().coerceIn(0, stageCount - 1)
    }
}