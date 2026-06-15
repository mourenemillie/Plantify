package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.GrowthProgressItem
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
        viewModelScope.launch {
            repository.myPlants.collect { plants ->
                val items = plants.map { plant ->
                    val catalog = repository.getCatalogById(plant.id_tanaman)
                    
                    // Simple logic to calculate days and progress
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val startDate = try { sdf.parse(plant.tanggal_mulai_tanam) } catch (e: Exception) { Date() }
                    val diff = Date().time - (startDate?.time ?: Date().time)
                    val daysGrown = (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
                    
                    val totalDays = catalog?.durasi_panen ?: 30
                    val progress = (daysGrown.toFloat() / totalDays).coerceIn(0f, 1f)
                    
                    val stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit")
                    val currentStageIndex = (progress * (stages.size - 1)).toInt()

                    val calendar = Calendar.getInstance()
                    calendar.time = startDate ?: Date()
                    calendar.add(Calendar.DAY_OF_YEAR, totalDays)
                    val estimateDate = sdf.format(calendar.time)

                    GrowthProgressItem(
                        plantEmoji = catalog?.emoji_icon ?: "🌱",
                        plantName = catalog?.nama_tanaman ?: "Unknown",
                        currentDay = daysGrown,
                        totalDays = totalDays,
                        stages = stages,
                        currentStageIndex = currentStageIndex,
                        estimateDate = estimateDate,
                        progress = progress
                    )
                }
                _growthItems.value = items
            }
        }
        viewModelScope.launch {
            repository.syncWithSupabase()
        }
    }
}