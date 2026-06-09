package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.Plant
import com.example.plantify.data.PlantRepository
import com.example.plantify.data.PlantTask
import com.example.plantify.data.TaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel : ViewModel() {

    private val _plants = MutableStateFlow<List<Plant>>(emptyList())
    val plants: StateFlow<List<Plant>> = _plants.asStateFlow()

    private val _tasks = MutableStateFlow<List<PlantTask>>(emptyList())
    val tasks: StateFlow<List<PlantTask>> = _tasks.asStateFlow()

    init {
        viewModelScope.launch {
            PlantRepository.plants.collect { entries ->
                _plants.value = entries.map { entry ->
                    val currentDay = PlantRepository.calcCurrentDay(entry)
                    val progress = PlantRepository.calcProgress(entry)
                    val nextWatering = when {
                        currentDay % 2 == 0 -> "Today"
                        else -> "Tomorrow"
                    }
                    Plant(
                        id = entry.id,
                        name = entry.name,
                        daysGrown = currentDay,
                        progress = progress,
                        nextWatering = nextWatering
                    )
                }
                _tasks.value = entries
                    .filter { entry ->
                        val currentDay = PlantRepository.calcCurrentDay(entry)
                        currentDay % 2 == 0
                    }
                    .map { entry ->
                        PlantTask(
                            id = UUID.randomUUID().toString(),
                            title = "Watering",
                            subtitle = entry.name,
                            time = "08:00 AM",
                            type = TaskType.WATERING
                        )
                    }
            }
        }
    }
}