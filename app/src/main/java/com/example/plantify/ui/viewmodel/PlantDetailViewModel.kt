package com.example.plantify.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.PlantDetail
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class PlantDetailViewModel(
    private val repository: PlantRepository
) : ViewModel() {

    private val _plantDetail = MutableStateFlow<PlantDetail?>(null)
    val plantDetail: StateFlow<PlantDetail?> = _plantDetail.asStateFlow()

    // True while the AI request is in flight. The UI can show a small indicator
    // next to the AI-generated cards (About / Care Guide / Growing Tips).
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // In-memory cache: skip the AI round-trip if the user reopens the same plant.
    private val aiCache = mutableMapOf<Int, AiInfo>()

    fun loadPlant(plantId: String) {
        viewModelScope.launch {
            val id = plantId.toIntOrNull()
            if (id == null) {
                _plantDetail.value = null
                return@launch
            }

            val entry = repository.getCatalogById(id)
            if (entry == null) {
                _plantDetail.value = null
                return@launch
            }

            val difficulty = entry.difficulty ?: "Easy"
            val difficultyColor = when (difficulty) {
                "Medium" -> Color(0xFFFFF3E0)
                "Hard" -> Color(0xFFFFEBEE)
                else -> Color(0xFFE8F5E9)
            }
            val category = when (entry.nama_tanaman) {
                "Spinach", "Lettuce", "Mustard Greens", "Green Onion" -> "Leafy Greens"
                else -> "Vegetables"
            }

            // Cache hit -> render straight away with no AI call.
            val cached = aiCache[id]
            if (cached != null) {
                _plantDetail.value = buildDetail(entry, category, difficulty, difficultyColor, cached)
                return@launch
            }

            // First render: header is real, AI-driven cards show a friendly placeholder
            // built from the catalog row so the screen is never empty.
            val placeholder = placeholderInfo(entry)
            _plantDetail.value = buildDetail(entry, category, difficulty, difficultyColor, placeholder)

            // Then enrich from AI in the background.
            _isAiLoading.value = true
            val aiInfo = fetchAiInfo(entry.nama_tanaman) ?: fallbackInfo(entry)
            aiCache[id] = aiInfo
            _plantDetail.value = buildDetail(entry, category, difficulty, difficultyColor, aiInfo)
            _isAiLoading.value = false
        }
    }

    private suspend fun fetchAiInfo(plantName: String): AiInfo? {
        return try {
            val json = repository.generatePlantInfo(plantName) ?: return null
            val obj = JSONObject(json)
            val tipsArr = obj.optJSONArray("tips")
            val tips = mutableListOf<String>()
            if (tipsArr != null) {
                for (i in 0 until tipsArr.length()) tips.add(tipsArr.getString(i))
            }
            val description = obj.optString("description").trim()
            if (description.isBlank()) return null
            AiInfo(
                description = description,
                watering = obj.optString("watering").ifBlank { "As needed" },
                sunlight = obj.optString("sunlight").ifBlank { "Full sun (6-8 hrs)" },
                temperature = obj.optString("temperature").ifBlank { "20-27°C" },
                fertilizing = obj.optString("fertilizing").ifBlank { "Every 2 weeks" },
                tips = if (tips.isEmpty()) listOf(
                    "Water on schedule.",
                    "Give it enough sunlight.",
                    "Check for pests weekly."
                ) else tips
            )
        } catch (e: Exception) {
            null
        }
    }

    // Shown while the AI is still working.
    private fun placeholderInfo(entry: PlantCatalogEntity): AiInfo {
        val watering = entry.interval_siram?.let {
            if (it == 1) "Every day" else "Every $it days"
        } ?: "As needed"
        val fertilizing = entry.interval_pupuk?.let { "Every $it days" } ?: "Every 2 weeks"
        return AiInfo(
            description = "Generating fresh growing tips for ${entry.nama_tanaman}…",
            watering = watering,
            sunlight = "Full sun (6-8 hrs)",
            temperature = "20-27°C",
            fertilizing = fertilizing,
            tips = listOf("Loading personalized growing tips…")
        )
    }

    // Shown if the AI call ultimately fails — built only from the catalog row.
    private fun fallbackInfo(entry: PlantCatalogEntity): AiInfo {
        val watering = entry.interval_siram?.let {
            if (it == 1) "Every day" else "Every $it days"
        } ?: "As needed"
        val fertilizing = entry.interval_pupuk?.let { "Every $it days" } ?: "Every 2 weeks"
        return AiInfo(
            description = "${entry.nama_tanaman} is a great plant to grow at home. With the right care, " +
                    "it will reward you with a healthy harvest.",
            watering = watering,
            sunlight = "Full sun (6-8 hrs)",
            temperature = "20-27°C",
            fertilizing = fertilizing,
            tips = listOf(
                "Water on a regular schedule.",
                "Make sure it gets enough sunlight.",
                "Check for pests weekly.",
                "Harvest at the right time for best flavor."
            )
        )
    }

    private fun buildDetail(
        entry: PlantCatalogEntity,
        category: String,
        difficulty: String,
        difficultyColor: Color,
        info: AiInfo
    ) = PlantDetail(
        id = entry.id_tanaman.toString(),
        name = entry.nama_tanaman,
        emoji = entry.emoji_icon ?: "🌱",
        category = category,
        difficulty = difficulty,
        difficultyColor = difficultyColor,
        description = info.description,
        wateringFrequency = info.watering,
        sunlight = info.sunlight,
        temperature = info.temperature,
        fertilizing = info.fertilizing,
        tips = info.tips
    )

    private data class AiInfo(
        val description: String,
        val watering: String,
        val sunlight: String,
        val temperature: String,
        val fertilizing: String,
        val tips: List<String>
    )
}