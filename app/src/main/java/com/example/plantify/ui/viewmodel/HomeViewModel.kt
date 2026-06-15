package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.local.entity.TaskScheduleEntity
import com.example.plantify.data.remote.WeatherService
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PlantRepository,
    private val weatherService: WeatherService = WeatherService()
) : ViewModel() {

    private val _myPlants = MutableStateFlow<List<MyPlantEntity>>(emptyList())
    val myPlants: StateFlow<List<MyPlantEntity>> = _myPlants.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskScheduleEntity>>(emptyList())
    val tasks: StateFlow<List<TaskScheduleEntity>> = _tasks.asStateFlow()

    private val _currentWeather = MutableStateFlow("Fetching weather...")
    val currentWeather: StateFlow<String> = _currentWeather.asStateFlow()

    private val _weatherCondition = MutableStateFlow("28°C — Sunny")
    val weatherCondition: StateFlow<String> = _weatherCondition.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.myPlants.collect {
                _myPlants.value = it
                // Logic to fetch weather for the first plant's location
                // In a real app, you might want a default location or user location
            }
        }
        viewModelScope.launch {
            repository.allSchedules.collect {
                _tasks.value = it
            }
        }
        viewModelScope.launch {
            repository.syncWithSupabase()
        }
        // Fetch weather for a default location or based on plants
        viewModelScope.launch {
            val weather = weatherService.getCurrentWeather("32.73.20.1001") // Sample code
            if (weather != null) {
                _currentWeather.value = weather
            } else {
                _currentWeather.value = "Sunny, 28°C" // Fallback dummy but tracked
            }
        }
    }
}