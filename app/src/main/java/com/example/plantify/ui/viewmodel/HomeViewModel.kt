package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantify.data.Plant
import com.example.plantify.data.PlantTask
import com.example.plantify.data.TaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class HomeViewModel : ViewModel() {

    private val _plants = MutableStateFlow<List<Plant>>(emptyList())
    val plants: StateFlow<List<Plant>> = _plants.asStateFlow()

    private val _tasks = MutableStateFlow<List<PlantTask>>(emptyList())
    val tasks: StateFlow<List<PlantTask>> = _tasks.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _plants.value = listOf(
            Plant(
                id = UUID.randomUUID().toString(),
                name = "Cherry Tomato",
                daysGrown = 15,
                progress = 0.85f,
                nextWatering = "Today",
                imageUrl = "https://images.unsplash.com/photo-1592841200221-a6898f307baa?w=200&h=200&fit=crop"
            ),
            Plant(
                id = UUID.randomUUID().toString(),
                name = "Red Chili",
                daysGrown = 22,
                progress = 0.92f,
                nextWatering = "Tomorrow",
                imageUrl = "https://images.unsplash.com/photo-1588252303782-cb80119abd6e?w=200&h=200&fit=crop"
            ),
            Plant(
                id = UUID.randomUUID().toString(),
                name = "Spinach",
                daysGrown = 8,
                progress = 0.45f,
                nextWatering = "Today",
                imageUrl = "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=200&h=200&fit=crop"
            )
        )

        _tasks.value = listOf(
            PlantTask(
                id = UUID.randomUUID().toString(),
                title = "Watering",
                subtitle = "Cherry Tomato",
                time = "08:00 AM",
                type = TaskType.WATERING
            ),
            PlantTask(
                id = UUID.randomUUID().toString(),
                title = "Watering",
                subtitle = "Spinach",
                time = "08:00 AM",
                type = TaskType.WATERING
            ),
            PlantTask(
                id = UUID.randomUUID().toString(),
                title = "Fertilizing",
                subtitle = "Red Chili",
                time = "09:00 AM",
                type = TaskType.FERTILIZING
            )
        )
    }
}
