package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddPlantTypeViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _difficulty = MutableStateFlow("Easy")
    val difficulty: StateFlow<String> = _difficulty.asStateFlow()

    private val _harvestDuration = MutableStateFlow("30")
    val harvestDuration: StateFlow<String> = _harvestDuration.asStateFlow()

    private val _waterInterval = MutableStateFlow("1")
    val waterInterval: StateFlow<String> = _waterInterval.asStateFlow()

    private val _fertilizerInterval = MutableStateFlow("14")
    val fertilizerInterval: StateFlow<String> = _fertilizerInterval.asStateFlow()

    private val _emojiIcon = MutableStateFlow("🌱")
    val emojiIcon: StateFlow<String> = _emojiIcon.asStateFlow()

    fun updateName(newName: String) { _name.value = newName }
    fun updateDifficulty(newDifficulty: String) { _difficulty.value = newDifficulty }
    fun updateHarvestDuration(newDuration: String) { _harvestDuration.value = newDuration }
    fun updateWaterInterval(newInterval: String) { _waterInterval.value = newInterval }
    fun updateFertilizerInterval(newInterval: String) { _fertilizerInterval.value = newInterval }
    fun updateEmojiIcon(newEmoji: String) { _emojiIcon.value = newEmoji }

    fun savePlantType(onSuccess: () -> Unit) {
        val plant = PlantCatalogEntity(
            id_tanaman = 0,
            nama_tanaman = _name.value,
            difficulty = _difficulty.value,
            durasi_panen = _harvestDuration.value.toIntOrNull() ?: 30,
            interval_siram = _waterInterval.value.toIntOrNull() ?: 1,
            interval_pupuk = _fertilizerInterval.value.toIntOrNull() ?: 14,
            emoji_icon = _emojiIcon.value
        )
        viewModelScope.launch {
            repository.addPlantType(plant)
            onSuccess()
        }
    }
}
