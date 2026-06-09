package com.example.plantify.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.PlantDetail
import com.example.plantify.data.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlantDetailViewModel : ViewModel() {

    private val _plantDetail = MutableStateFlow<PlantDetail?>(null)
    val plantDetail: StateFlow<PlantDetail?> = _plantDetail.asStateFlow()

    fun loadPlant(plantId: String) {
        viewModelScope.launch {
            PlantRepository.plants.collect { entries ->
                val entry = entries.find { it.id == plantId } ?: entries.firstOrNull() ?: return@collect

                val currentDay = PlantRepository.calcCurrentDay(entry)
                val progress = PlantRepository.calcProgress(entry)
                val currentStageIndex = PlantRepository.calcStageIndex(entry)

                val difficulty = when (entry.name) {
                    "Red Chili", "Bell Pepper" -> "Medium"
                    else -> "Easy"
                }
                val difficultyColor = if (difficulty == "Medium") Color(0xFFFFF3E0) else Color(0xFFE8F5E9)

                val description = when (entry.name) {
                    "Cherry Tomato", "Tomato" -> "Cherry tomatoes are small, bite-sized fruits that grow in clusters. They're sweet, easy to grow, and perfect for beginners."
                    "Red Chili" -> "Red chili peppers are spicy fruits used in cooking worldwide. They need warm temperatures and consistent care."
                    "Spinach" -> "Spinach is a fast-growing leafy green packed with nutrients. It prefers cooler temperatures and is one of the easiest vegetables to grow."
                    "Lettuce" -> "Lettuce is a crisp leafy vegetable that grows quickly in cool weather. Great for salads and easy to grow in containers."
                    "Cucumber" -> "Cucumbers are refreshing vegetables that thrive in warm weather. They grow fast and produce abundantly with proper watering."
                    "Bell Pepper" -> "Bell peppers are colorful, sweet vegetables that need warmth and plenty of sunlight to produce their best harvest."
                    else -> "${entry.name} is a wonderful plant to grow at home. With the right care, it will reward you with a healthy harvest."
                }

                val tips = when (entry.name) {
                    "Cherry Tomato", "Tomato" -> listOf(
                        "Water consistently to prevent blossom end rot",
                        "Use a stake or cage for support as it grows",
                        "Pinch off suckers to improve fruit production",
                        "Harvest when fully red for best flavor"
                    )
                    "Red Chili" -> listOf(
                        "Avoid overwatering — chili prefers slightly dry soil",
                        "Add calcium to prevent blossom drop",
                        "Harvest green or wait for full red color",
                        "Wear gloves when handling hot varieties"
                    )
                    "Spinach" -> listOf(
                        "Keep soil moist but not waterlogged",
                        "Harvest outer leaves first to extend yield",
                        "Avoid planting in summer heat — it bolts quickly",
                        "Great companion plant with strawberries"
                    )
                    else -> listOf(
                        "Water regularly based on the schedule",
                        "Make sure it gets enough sunlight",
                        "Check for pests weekly",
                        "Harvest at the right time for best taste"
                    )
                }

                val sunlight = when (entry.name) {
                    "Spinach", "Lettuce", "Mustard Greens" -> "Partial shade (4-6 hrs)"
                    else -> "Full sun (6-8 hrs)"
                }
                val temp = when (entry.name) {
                    "Spinach", "Lettuce", "Mustard Greens" -> "10–20°C"
                    "Red Chili", "Bell Pepper" -> "22–30°C"
                    else -> "20–27°C"
                }
                val fertilizing = when (entry.name) {
                    "Spinach", "Lettuce", "Mustard Greens" -> "Every 4 weeks"
                    "Red Chili", "Bell Pepper" -> "Every 3 weeks"
                    else -> "Every 2 weeks"
                }

                _plantDetail.value = PlantDetail(
                    id = entry.id,
                    name = entry.name,
                    emoji = entry.emoji,
                    category = when (entry.name) {
                        "Spinach", "Lettuce", "Mustard Greens", "Green Onion" -> "Leafy Greens"
                        else -> "Vegetables"
                    },
                    difficulty = difficulty,
                    difficultyColor = difficultyColor,
                    description = description,
                    totalDays = entry.totalDays,
                    currentDay = currentDay,
                    progress = progress,
                    stages = entry.stages,
                    currentStageIndex = currentStageIndex,
                    wateringFrequency = entry.wateringFrequency,
                    sunlight = sunlight,
                    temperature = temp,
                    fertilizing = fertilizing,
                    tips = tips
                )
            }
        }
    }
}