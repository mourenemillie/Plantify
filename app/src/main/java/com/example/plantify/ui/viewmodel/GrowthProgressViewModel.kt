package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.GrowthProgressItem
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Model data untuk catatan pertumbuhan
data class GrowthNote(val date: String, val note: String)

class GrowthProgressViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _growthItems = MutableStateFlow<List<GrowthProgressItem>>(emptyList())
    val growthItems: StateFlow<List<GrowthProgressItem>> = _growthItems.asStateFlow()

    private val _notes = MutableStateFlow<Map<String, List<GrowthNote>>>(emptyMap())
    val notes: StateFlow<Map<String, List<GrowthNote>>> = _notes.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Menggabungkan data tanaman dan katalog secara realtime
            combine(repository.myPlants, repository.allCatalog) { myPlants, catalog ->
                myPlants.map { plant ->
                    val catalogInfo = catalog.find { it.id_tanaman == plant.id_tanaman }
                    
                    // Menghitung jumlah hari tumbuh dari tanggal di database
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val startDate = try { sdf.parse(plant.tanggal_mulai_tanam) } catch (e: Exception) { Date() }
                    val diff = Date().time - (startDate?.time ?: Date().time)
                    val daysGrown = (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
                    
                    val totalDays = catalogInfo?.durasi_panen ?: 30
                    val progress = (daysGrown.toFloat() / totalDays).coerceIn(0f, 1f)
                    
                    val stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit")
                    val currentStageIndex = (progress * (stages.size - 1)).toInt()

                    // Perhitungan kalender estimasi panen
                    val calendar = Calendar.getInstance()
                    calendar.time = startDate ?: Date()
                    calendar.add(Calendar.DAY_OF_YEAR, totalDays)
                    val estimateDate = sdf.format(calendar.time)

                    GrowthProgressItem(
                        plantEmoji = catalogInfo?.emoji_icon ?: "🌱",
                        plantName = plant.nama_pot?.ifEmpty { catalogInfo?.nama_tanaman ?: "Unknown" } ?: (catalogInfo?.nama_tanaman ?: "Unknown"),
                        currentDay = daysGrown,
                        totalDays = totalDays,
                        stages = stages,
                        currentStageIndex = currentStageIndex,
                        estimateDate = estimateDate,
                        progress = progress
                    )
                }
            }.collect { items ->
                _growthItems.value = items
            }
        }

        viewModelScope.launch {
            repository.syncWithSupabase()
        }
    }

    // Fitur tambah catatan dari Hasna
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