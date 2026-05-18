package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantify.data.GrowthProgressItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GrowthProgressViewModel : ViewModel() {

    private val _growthItems = MutableStateFlow<List<GrowthProgressItem>>(emptyList())
    val growthItems: StateFlow<List<GrowthProgressItem>> = _growthItems.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _growthItems.value = listOf(
            GrowthProgressItem(
                plantEmoji = "🍅",
                plantName = "Cherry Tomato",
                currentDay = 15,
                totalDays = 70,
                stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit"),
                currentStageIndex = 2,
                estimateDate = "June 15, 2026",
                progress = 0.21f
            ),
            GrowthProgressItem(
                plantEmoji = "🌶️",
                plantName = "Red Chili",
                currentDay = 22,
                totalDays = 85,
                stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit"),
                currentStageIndex = 2,
                estimateDate = "July 2, 2026",
                progress = 0.26f
            ),
            GrowthProgressItem(
                plantEmoji = "🥬",
                plantName = "Spinach",
                currentDay = 8,
                totalDays = 45,
                stages = listOf("Seed", "Sprout", "Veg", "Harvest"),
                currentStageIndex = 1,
                estimateDate = "June 4, 2026",
                progress = 0.18f
            )
        )
    }
}
