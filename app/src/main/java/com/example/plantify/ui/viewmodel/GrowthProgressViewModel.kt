package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.GrowthNote
import com.example.plantify.data.GrowthProgressItem
import com.example.plantify.data.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GrowthProgressViewModel : ViewModel() {

    private val _growthItems = MutableStateFlow<List<GrowthProgressItem>>(emptyList())
    val growthItems: StateFlow<List<GrowthProgressItem>> = _growthItems.asStateFlow()

    private val _notes = MutableStateFlow<Map<String, List<GrowthNote>>>(emptyMap())
    val notes: StateFlow<Map<String, List<GrowthNote>>> = _notes.asStateFlow()

    init {
        viewModelScope.launch {
            PlantRepository.plants.collect { entries ->
                val outputFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)

                _growthItems.value = entries.map { entry ->
                    val currentDay = PlantRepository.calcCurrentDay(entry)
                    val progress = PlantRepository.calcProgress(entry)
                    val currentStageIndex = PlantRepository.calcStageIndex(entry)

                    val harvestCal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, entry.plantingYear)
                        set(Calendar.MONTH, entry.plantingMonth - 1)
                        set(Calendar.DAY_OF_MONTH, entry.plantingDay)
                    }
                    harvestCal.add(Calendar.DAY_OF_MONTH, entry.totalDays)

                    GrowthProgressItem(
                        plantEmoji = entry.emoji,
                        plantName = entry.name,
                        currentDay = currentDay,
                        totalDays = entry.totalDays,
                        stages = entry.stages,
                        currentStageIndex = currentStageIndex,
                        estimateDate = outputFormatter.format(harvestCal.time),
                        progress = progress
                    )
                }
            }
        }
    }

    fun addNote(plantName: String, noteText: String) {
        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val today = dateFormatter.format(Calendar.getInstance().time)
        val newNote = GrowthNote(date = today, note = noteText)
        val currentMap = _notes.value.toMutableMap()
        val existingNotes = currentMap[plantName] ?: emptyList()
        currentMap[plantName] = listOf(newNote) + existingNotes
        _notes.value = currentMap
    }
}