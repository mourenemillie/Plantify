package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddPlantViewModel : ViewModel() {

    private val _selectedPlant = MutableStateFlow("Tomato (60-80 days)")
    val selectedPlant: StateFlow<String> = _selectedPlant.asStateFlow()

    private val _plantingDate = MutableStateFlow("April 20, 2026")
    val plantingDate: StateFlow<String> = _plantingDate.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    fun updateLocation(newLocation: String) {
        _location.value = newLocation
    }
}
