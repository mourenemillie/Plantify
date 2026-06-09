package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.Plant
import com.example.plantify.data.PlantRepository
import com.example.plantify.data.PlantTask
import com.example.plantify.data.TaskType
import com.example.plantify.R
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

    // Cuaca: diisi oleh LocationViewModel via PlantifyApp
    private val _weatherCondition = MutableStateFlow("Cerah, 28°C")
    val weatherCondition: StateFlow<String> = _weatherCondition.asStateFlow()

    init {
        loadMockData()
    }

    /** Dipanggil dari PlantifyApp saat LocationViewModel mendapat data cuaca baru */
    fun updateWeather(condition: String) {
        _weatherCondition.value = condition
    }

    private fun loadMockData() {
        viewModelScope.launch {
            PlantRepository.plants.collect { entries ->
                _plants.value = entries.map { entry ->
                    val currentDay = PlantRepository.calcCurrentDay(entry)
                    val progress = PlantRepository.calcProgress(entry)
                    val nextWatering = when {
                        currentDay % 2 == 0 -> "Today"
                        else -> "Tomorrow"
                    }
                    val imgRes = when (entry.name.lowercase()) {
                        "tomato" -> R.drawable.ic_plant_tomato
                        "red chili" -> R.drawable.ic_plant_red_chili
                        "spinach" -> R.drawable.ic_plant_spinach
                        "mustard greens" -> R.drawable.ic_plant_mustard_greens
                        "lettuce" -> R.drawable.ic_plant_lettuce
                        "green onion" -> R.drawable.ic_plant_green_onion
                        "bell pepper" -> R.drawable.ic_plant_bell_pepper
                        "cucumber" -> R.drawable.ic_plant_cucumber
                        else -> R.drawable.ic_plant_tomato
                    }
                    Plant(
                        id = entry.id,
                        name = entry.name,
                        daysGrown = currentDay,
                        progress = progress,
                        nextWatering = nextWatering,
                        imageRes = imgRes
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