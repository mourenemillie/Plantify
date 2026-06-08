package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.remote.AiService
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPlantViewModel(private val plantRepository: PlantRepository) : ViewModel() {

    private val aiService = AiService()

    private val _selectedPlant = MutableStateFlow("Tomato (60-80 days)")
    val selectedPlant: StateFlow<String> = _selectedPlant.asStateFlow()

    private val _plantingDate = MutableStateFlow("April 20, 2026")
    val plantingDate: StateFlow<String> = _plantingDate.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun updateLocation(newLocation: String) {
        _location.value = newLocation
    }

    fun savePlantManually(plantName: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            // Find corresponding catalog ID if possible
            var idTanaman = 1 // Default to Tomato if not found
            try {
                val catalog = plantRepository.allCatalog.first()
                val matched = catalog.find { plantName.contains(it.nama_tanaman, ignoreCase = true) }
                if (matched != null) {
                    idTanaman = matched.id_tanaman
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = format.format(Date())

            val newPlant = MyPlantEntity(
                id_tanaman = idTanaman,
                tanggal_mulai_tanam = dateStr,
                nama_pot = plantName,
                progress_persen = 0f,
                next_watering = "",
                status_tanaman = "Sehat"
            )

            plantRepository.addPlant(newPlant)
            onComplete()
        }
    }

    fun generateAiSchedule(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val condition = "Location: ${_location.value}, Date: ${_plantingDate.value}, Weather: 32°C Sunny"
            
            try {
                val result = aiService.generateCareSchedule(_selectedPlant.value, condition)
                _isLoading.value = false
                if (result != null) {
                    val startIndex = result.indexOf('{')
                    val endIndex = result.lastIndexOf('}')
                    if (startIndex != -1 && endIndex != -1 && endIndex >= startIndex) {
                        val cleanJson = result.substring(startIndex, endIndex + 1)
                        onSuccess(cleanJson)
                    } else {
                        onError("AI response did not contain JSON.")
                    }
                } else {
                    onError("AI did not return a response. Please try again.")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                onError("Error: ${e.message}")
            }
        }
    }
}
