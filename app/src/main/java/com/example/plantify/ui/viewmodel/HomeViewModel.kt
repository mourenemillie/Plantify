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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// UI-facing wrapper: pairs a user's planted entry (kebunku row) with its species
// name from the catalog so the Home screen can pick the right icon.
data class HomePlantUi(
    val plant: MyPlantEntity,
    val speciesName: String
)

class HomeViewModel(
    private val repository: PlantRepository,
    private val weatherService: WeatherService = WeatherService()
) : ViewModel() {

    private val _myPlants = MutableStateFlow<List<HomePlantUi>>(emptyList())
    val myPlants: StateFlow<List<HomePlantUi>> = _myPlants.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskScheduleEntity>>(emptyList())
    val tasks: StateFlow<List<TaskScheduleEntity>> = _tasks.asStateFlow()

    // State cuaca berdasarkan wilayah permanen tanaman
    private val _currentWeather = MutableStateFlow("Fetching weather...")
    val currentWeather: StateFlow<String> = _currentWeather.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // Join My Plants with the catalog so each row carries its species name.
        viewModelScope.launch {
            combine(repository.myPlants, repository.allCatalog) { plants, catalog ->
                val byId = catalog.associateBy { it.id_tanaman }
                plants.map { p ->
                    HomePlantUi(
                        plant = p,
                        speciesName = byId[p.id_tanaman]?.nama_tanaman ?: (p.nama_pot ?: "Plant")
                    )
                }
            }.collect { _myPlants.value = it }
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