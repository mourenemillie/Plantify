package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.PlantDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import java.util.Calendar
import java.util.concurrent.TimeUnit

class PlantDetailViewModel : ViewModel() {

    private val _plantDetail = MutableStateFlow<PlantDetail?>(null)
    val plantDetail: StateFlow<PlantDetail?> = _plantDetail.asStateFlow()

    fun loadPlant(plantId: String) {
        viewModelScope.launch {
            _plantDetail.value = getPlantById(plantId)
        }
    }

    private fun getPlantById(plantId: String): PlantDetail {
        val allPlants = listOf(
            buildPlantDetail(
                id = "cherry_tomato",
                name = "Cherry Tomato",
                emoji = "🍅",
                category = "Vegetables",
                difficulty = "Easy",
                difficultyColor = Color(0xFFE8F5E9),
                description = "Cherry tomatoes are small, bite-sized fruits that grow in clusters. They're sweet, easy to grow, and perfect for beginners. Great for containers or garden beds.",
                plantingYear = 2026, plantingMonth = 4, plantingDay = 24,
                totalDays = 70,
                stages = listOf("Seed", "Sprout", "Vegetative", "Flowering", "Fruiting"),
                wateringFrequency = "Every 2 days",
                sunlight = "Full sun (6-8 hrs)",
                temperature = "20–27°C",
                fertilizing = "Every 2 weeks",
                tips = listOf(
                    "Water consistently to prevent blossom end rot",
                    "Use a stake or cage for support as it grows",
                    "Pinch off suckers to improve fruit production",
                    "Harvest when fully red for best flavor"
                )
            ),
            buildPlantDetail(
                id = "red_chili",
                name = "Red Chili",
                emoji = "🌶️",
                category = "Vegetables",
                difficulty = "Medium",
                difficultyColor = Color(0xFFFFF3E0),
                description = "Red chili peppers are spicy fruits used in cooking worldwide. They need warm temperatures and consistent care, but reward you with a generous harvest.",
                plantingYear = 2026, plantingMonth = 4, plantingDay = 17,
                totalDays = 85,
                stages = listOf("Seed", "Sprout", "Vegetative", "Flowering", "Fruiting"),
                wateringFrequency = "Every 3 days",
                sunlight = "Full sun (6-8 hrs)",
                temperature = "22–30°C",
                fertilizing = "Every 3 weeks",
                tips = listOf(
                    "Avoid overwatering — chili prefers slightly dry soil",
                    "Add calcium to prevent blossom drop",
                    "Harvest green or wait for full red color",
                    "Wear gloves when handling hot varieties"
                )
            ),
            buildPlantDetail(
                id = "spinach",
                name = "Spinach",
                emoji = "🥬",
                category = "Leafy Greens",
                difficulty = "Easy",
                difficultyColor = Color(0xFFE8F5E9),
                description = "Spinach is a fast-growing leafy green packed with nutrients. It prefers cooler temperatures and is one of the easiest vegetables to grow at home.",
                plantingYear = 2026, plantingMonth = 5, plantingDay = 1,
                totalDays = 45,
                stages = listOf("Seed", "Sprout", "Vegetative", "Harvest"),
                wateringFrequency = "Every day",
                sunlight = "Partial shade (4-6 hrs)",
                temperature = "10–20°C",
                fertilizing = "Every 4 weeks",
                tips = listOf(
                    "Keep soil moist but not waterlogged",
                    "Harvest outer leaves first to extend yield",
                    "Avoid planting in summer heat — it bolts quickly",
                    "Great companion plant with strawberries"
                )
            )
        )

        return allPlants.find { it.id == plantId } ?: allPlants.first()
    }

    private fun buildPlantDetail(
        id: String,
        name: String,
        emoji: String,
        category: String,
        difficulty: String,
        difficultyColor: androidx.compose.ui.graphics.Color,
        description: String,
        plantingYear: Int,
        plantingMonth: Int,
        plantingDay: Int,
        totalDays: Int,
        stages: List<String>,
        wateringFrequency: String,
        sunlight: String,
        temperature: String,
        fertilizing: String,
        tips: List<String>
    ): PlantDetail {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val plantingCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, plantingYear)
            set(Calendar.MONTH, plantingMonth - 1)
            set(Calendar.DAY_OF_MONTH, plantingDay)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val rawDay = TimeUnit.MILLISECONDS.toDays(today.timeInMillis - plantingCal.timeInMillis).toInt()
        val currentDay = rawDay.coerceIn(0, totalDays)
        val progress = (currentDay.toFloat() / totalDays).coerceIn(0f, 1f)
        val stageCount = stages.size
        val currentStageIndex = if (progress >= 1f) stageCount - 1
        else (progress * stageCount).toInt().coerceIn(0, stageCount - 1)

        return PlantDetail(
            id = id, name = name, emoji = emoji, category = category,
            difficulty = difficulty, difficultyColor = difficultyColor,
            description = description, totalDays = totalDays, currentDay = currentDay,
            progress = progress, stages = stages, currentStageIndex = currentStageIndex,
            wateringFrequency = wateringFrequency, sunlight = sunlight,
            temperature = temperature, fertilizing = fertilizing, tips = tips
        )
    }
}