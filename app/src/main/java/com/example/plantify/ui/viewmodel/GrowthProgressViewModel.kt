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

import androidx.lifecycle.viewModelScope
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GrowthProgressViewModel(private val repository: PlantRepository) : ViewModel() {

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

    private fun loadData() {
        combine(repository.myPlants, repository.allCatalog) { myPlants, catalog ->
            myPlants.map { entity ->
                val catalogInfo = catalog.find { it.id_tanaman == entity.id_tanaman }
                val emoji = catalogInfo?.emoji_icon ?: "🌱"
                val name = if (!entity.nama_pot.isNullOrEmpty()) entity.nama_pot else (catalogInfo?.nama_tanaman ?: "My Plant")
                
                val days = calculateDays(entity.tanggal_mulai_tanam)
                val totalDays = 60 // Default estimate for now
                val currentStageIdx = if (days < 5) 0 else if (days < 20) 1 else if (days < 40) 2 else 3

                GrowthProgressItem(
                    plantEmoji = emoji,
                    plantName = name!!,
                    currentDay = days,
                    totalDays = totalDays,
                    stages = listOf("Seed", "Sprout", "Vegetative", "Harvest"),
                    currentStageIndex = currentStageIdx,
                    estimateDate = "Est. in ${totalDays - days} days",
                    progress = entity.progress_persen.toFloat() / 100f
                )
            }
        }
        .onEach { _growthItems.value = it }
        .launchIn(viewModelScope)
    }

    private fun calculateDays(startDate: String): Int {
        if (startDate.isEmpty()) return 0
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = format.parse(startDate)
            val today = Date()
            val diff = today.time - (start?.time ?: today.time)
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            0
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