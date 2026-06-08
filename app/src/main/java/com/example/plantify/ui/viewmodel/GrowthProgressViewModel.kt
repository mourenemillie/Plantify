package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantify.data.GrowthNote
import com.example.plantify.data.GrowthProgressItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class GrowthProgressViewModel : ViewModel() {

    private val _growthItems = MutableStateFlow<List<GrowthProgressItem>>(emptyList())
    val growthItems: StateFlow<List<GrowthProgressItem>> = _growthItems.asStateFlow()
    private val _notes = MutableStateFlow<Map<String, List<GrowthNote>>>(emptyMap())
    val notes: StateFlow<Map<String, List<GrowthNote>>> = _notes.asStateFlow()

    init {
        loadData()
    }

    private data class PlantSeed(
        val emoji: String,
        val name: String,
        val plantingYear: Int,
        val plantingMonth: Int,
        val plantingDay: Int,
        val totalDays: Int,
        val stages: List<String>
    )

    private fun loadData() {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val plantSeeds = listOf(
            PlantSeed(
                emoji = "🍅", name = "Cherry Tomato",
                plantingYear = 2026, plantingMonth = 4, plantingDay = 24,
                totalDays = 70,
                stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit")
            ),
            PlantSeed(
                emoji = "🌶️", name = "Red Chili",
                plantingYear = 2026, plantingMonth = 4, plantingDay = 17,
                totalDays = 85,
                stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit")
            ),
            PlantSeed(
                emoji = "🥬", name = "Spinach",
                plantingYear = 2026, plantingMonth = 5, plantingDay = 1,
                totalDays = 45,
                stages = listOf("Seed", "Sprout", "Veg", "Harvest")
            )
        )

        val outputFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)

        _growthItems.value = plantSeeds.map { seed ->
            val plantingCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, seed.plantingYear)
                set(Calendar.MONTH, seed.plantingMonth - 1)
                set(Calendar.DAY_OF_MONTH, seed.plantingDay)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val diffMillis = today.timeInMillis - plantingCal.timeInMillis
            val rawDay = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
            val currentDay = rawDay.coerceIn(0, seed.totalDays)
            val progress = (currentDay.toFloat() / seed.totalDays).coerceIn(0f, 1f)
            val stageCount = seed.stages.size
            val currentStageIndex = when {
                progress >= 1f -> stageCount - 1
                else -> (progress * stageCount).toInt().coerceIn(0, stageCount - 1)
            }
            val harvestCal = plantingCal.clone() as Calendar
            harvestCal.add(Calendar.DAY_OF_MONTH, seed.totalDays)

            GrowthProgressItem(
                plantEmoji = seed.emoji,
                plantName = seed.name,
                currentDay = currentDay,
                totalDays = seed.totalDays,
                stages = seed.stages,
                currentStageIndex = currentStageIndex,
                estimateDate = outputFormatter.format(harvestCal.time),
                progress = progress
            )
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