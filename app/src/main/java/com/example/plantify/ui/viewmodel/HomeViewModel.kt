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

    // State cuaca berdasarkan wilayah permanen tanaman
    private val _currentWeather = MutableStateFlow("Fetching weather...")
    val currentWeather: StateFlow<String> = _currentWeather.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // Mengambil daftar tanaman dari database lokal
        viewModelScope.launch {
            repository.myPlants.collect {
                _myPlants.value = it
            }
        }
        
        // Mengambil seluruh jadwal tugas perawatan
        viewModelScope.launch {
            repository.allSchedules.collect {
                _tasks.value = it
            }
        }
        
        // Sinkronisasi data ke Supabase cloud
        viewModelScope.launch {
            repository.syncWithSupabase()
        }
        
        // Mengambil data cuaca wilayah dari API berdasarkan kode area tanaman
        viewModelScope.launch {
            val weather = weatherService.getCurrentWeather("32.73.20.1001") 
            if (weather != null) {
                _currentWeather.value = weather
            } else {
                _currentWeather.value = "Sunny, 28°C" 
            }
        }
    }
}