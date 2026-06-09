package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.Plant
import com.example.plantify.data.PlantRepository
import com.example.plantify.data.PlantTask
import com.example.plantify.data.ScheduleRepository
import com.example.plantify.data.TaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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
            }
        }

        viewModelScope.launch {
            ScheduleRepository.scheduleGroups.collect { groups ->
                val todayGroup = groups.find { it.date.startsWith("Today") }
                if (todayGroup == null) {
                    _tasks.value = emptyList()
                    return@collect
                }

                _tasks.value = todayGroup.items
                    .filter { !it.isDone }
                    .map { item ->
                        val type = when (item.title) {
                            "Fertilizing" -> TaskType.FERTILIZING
                            else -> TaskType.WATERING
                        }
                        PlantTask(
                            id = item.title + item.plantName,
                            title = item.title,
                            subtitle = item.plantName,
                            time = item.time,
                            type = type
                        )
                    }
            }
        }
    }
}