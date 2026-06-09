package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.GrowthNote
import com.example.plantify.data.GrowthProgressItem
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GrowthProgressViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _growthItems = MutableStateFlow<List<GrowthProgressItem>>(emptyList())
    val growthItems: StateFlow<List<GrowthProgressItem>> = _growthItems.asStateFlow()

    private val _notes = MutableStateFlow<Map<String, List<GrowthNote>>>(emptyMap())
    val notes: StateFlow<Map<String, List<GrowthNote>>> = _notes.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        combine(repository.myPlants, repository.allCatalog) { myPlants, catalog ->
            myPlants.map { entity ->
                val catalogInfo = catalog.find { it.id_tanaman == entity.id_tanaman }
                val emoji = catalogInfo?.emoji_icon ?: when (entity.nama_pot?.lowercase()) {
                    "tomato" -> "🍅"
                    "red chili" -> "🌶️"
                    "spinach" -> "🥬"
                    "mustard greens" -> "🥬"
                    "lettuce" -> "🥗"
                    "green onion" -> "🌿"
                    "bell pepper" -> "🫑"
                    "cucumber" -> "🥒"
                    else -> "🌱"
                }
                val name = entity.nama_pot?.ifEmpty { catalogInfo?.nama_tanaman ?: "My Plant" } ?: (catalogInfo?.nama_tanaman ?: "My Plant")

                val days = calculateDays(entity.tanggal_mulai_tanam)
                val totalDays = catalogInfo?.durasi_panen ?: 60
                val progressRatio = if (totalDays > 0) days.toFloat() / totalDays else 0f
                val currentStageIdx = when {
                    progressRatio < 0.1f -> 0
                    progressRatio < 0.35f -> 1
                    progressRatio < 0.7f -> 2
                    else -> 3
                }
                val remaining = (totalDays - days).coerceAtLeast(0)

                GrowthProgressItem(
                    plantEmoji = emoji,
                    plantName = name,
                    currentDay = days,
                    totalDays = totalDays,
                    stages = listOf("Seed", "Sprout", "Vegetative", "Harvest"),
                    currentStageIndex = currentStageIdx,
                    estimateDate = if (remaining > 0) "Est. in $remaining days" else "🎉 Ready to harvest!",
                    progress = progressRatio.coerceIn(0f, 1f)
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