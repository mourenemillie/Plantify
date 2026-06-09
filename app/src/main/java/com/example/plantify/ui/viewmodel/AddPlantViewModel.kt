package com.example.plantify.ui.viewmodel

import com.example.plantify.BuildConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.remote.AiService
import com.example.plantify.data.repository.PlantRepository
import com.example.plantify.data.PlantEntry
import com.example.plantify.data.PlantRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPlantViewModel(private val plantRepository: PlantRepository) : ViewModel() {

    private val aiService = AiService()
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

<<<<<<< HEAD
    private val _selectedPlant = MutableStateFlow("") // Dikosongkan agar user bisa milih
    val selectedPlant: StateFlow<String> = _selectedPlant.asStateFlow()

    private val _plantingDate = MutableStateFlow(
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
    )
    val plantingDate: StateFlow<String> = _plantingDate.asStateFlow()
=======
data class PlantOption(val name: String, val emoji: String, val totalDays: Int)
sealed class AiRecommendationState {
    object Idle : AiRecommendationState()
    object Loading : AiRecommendationState()
    data class Success(val recommendation: String) : AiRecommendationState()
    data class Error(val message: String) : AiRecommendationState()
}

class AddPlantViewModel : ViewModel() {
    private val GROQ_API_KEY = BuildConfig.GROQ_API_KEY
    private val GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"

    val plantOptions = listOf(
        PlantOption("Tomato", "🍅", 70),
        PlantOption("Red Chili", "🌶️", 85),
        PlantOption("Spinach", "🥬", 45),
        PlantOption("Mustard Greens", "🥬", 35),
        PlantOption("Lettuce", "🥗", 50),
        PlantOption("Green Onion", "🌿", 70),
        PlantOption("Bell Pepper", "🫑", 80),
        PlantOption("Cucumber", "🥒", 55),
    )

    private val _selectedPlant = MutableStateFlow<PlantOption?>(null)
    val selectedPlant: StateFlow<PlantOption?> = _selectedPlant.asStateFlow()

    private val _plantingDay = MutableStateFlow("")
    val plantingDay: StateFlow<String> = _plantingDay.asStateFlow()

    private val _plantingMonth = MutableStateFlow("")
    val plantingMonth: StateFlow<String> = _plantingMonth.asStateFlow()

    private val _plantingYear = MutableStateFlow("")
    val plantingYear: StateFlow<String> = _plantingYear.asStateFlow()
>>>>>>> origin/Hasna

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun updateSelectedPlant(plant: String) {
        _selectedPlant.value = plant
    }

    fun updatePlantingDate(date: String) {
        _plantingDate.value = date
    }

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

    fun generateAiSchedule(weatherCondition: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val condition = "Location: ${_location.value}, Date: ${_plantingDate.value}, Weather: $weatherCondition"

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
<<<<<<< HEAD
}
=======
}
    private val _plantError = MutableStateFlow(false)
    val plantError: StateFlow<Boolean> = _plantError.asStateFlow()

    private val _dateError = MutableStateFlow(false)
    val dateError: StateFlow<Boolean> = _dateError.asStateFlow()

    private val _aiState = MutableStateFlow<AiRecommendationState>(AiRecommendationState.Idle)
    val aiState: StateFlow<AiRecommendationState> = _aiState.asStateFlow()

    private val _savedEvent = MutableSharedFlow<String>()
    val savedEvent = _savedEvent.asSharedFlow()

    fun selectPlant(option: PlantOption) {
        _selectedPlant.value = option
        _plantError.value = false
        _aiState.value = AiRecommendationState.Idle
    }

    fun updateDay(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) {
            _plantingDay.value = value; _dateError.value = false
        }
    }

    fun updateMonth(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) {
            _plantingMonth.value = value; _dateError.value = false
        }
    }

    fun updateYear(value: String) {
        if (value.length <= 4 && value.all { it.isDigit() }) {
            _plantingYear.value = value; _dateError.value = false
        }
    }

    fun updateLocation(value: String) { _location.value = value }
    fun getAiRecommendation() {
        val plant = _selectedPlant.value ?: run {
            _plantError.value = true; return
        }
        val d = _plantingDay.value.toIntOrNull()
        val m = _plantingMonth.value.toIntOrNull()
        val y = _plantingYear.value.toIntOrNull()
        if (d == null || m == null || y == null || d !in 1..31 || m !in 1..12 || y < 2020) {
            _dateError.value = true; return
        }

        val monthNames = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        val dateStr = "$d ${monthNames[m-1]} $y"
        val location = _location.value.ifBlank { "home garden" }

        viewModelScope.launch {
            _aiState.value = AiRecommendationState.Loading
            try {
                val prompt = """
                    Kamu adalah ahli perawatan tanaman. Berikan rekomendasi jadwal perawatan yang singkat dan ramah untuk:
                    - Tanaman: ${plant.name}
                    - Tanggal tanam: $dateStr
                    - Lokasi: $location
                    - Perkiraan panen: ${plant.totalDays} hari sejak tanam
    
                    Jawab dalam 2-3 kalimat saja dalam Bahasa Indonesia. Sertakan: frekuensi penyiraman, jadwal pemupukan, dan satu tips penting.
                    Spesifik dengan waktu dan intervalnya. Buat praktis dan menyemangati.
                    """.trimIndent()

                val result = callGroqApi(prompt)
                _aiState.value = AiRecommendationState.Success(result)
            } catch (e: Exception) {
                val msg = e.message ?: "Unknown error"
                android.util.Log.e("GroqAPI", "Error: $msg", e)
                _aiState.value = AiRecommendationState.Error("Error: $msg")
            }
        }
    }

    private suspend fun callGroqApi(prompt: String): String {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val url = URL(GROQ_URL)
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $GROQ_API_KEY")
            connection.doOutput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            val body = JSONObject().apply {
                put("model", "llama-3.1-8b-instant")
                put("max_tokens", 200)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }.toString()

            OutputStreamWriter(connection.outputStream).use { it.write(body) }

            val responseCode = connection.responseCode
            val response = if (responseCode == 200) {
                connection.inputStream.bufferedReader().readText()
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.readText() ?: "No error body"
                throw Exception("HTTP $responseCode: $errorBody")
            }

            val json = JSONObject(response)
            json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
        }
    }

    private fun isFormValid(): Boolean {
        var valid = true
        if (_selectedPlant.value == null) { _plantError.value = true; valid = false }
        val d = _plantingDay.value.toIntOrNull()
        val m = _plantingMonth.value.toIntOrNull()
        val y = _plantingYear.value.toIntOrNull()
        if (d == null || m == null || y == null || d !in 1..31 || m !in 1..12 || y < 2020) {
            _dateError.value = true; valid = false
        }
        return valid
    }

    suspend fun savePlant() {
        if (!isFormValid()) return

        val option = _selectedPlant.value!!
        val stages = when (option.name) {
            "Spinach", "Mustard Greens", "Lettuce", "Green Onion" ->
                listOf("Seed", "Sprout", "Veg", "Harvest")
            else ->
                listOf("Seed", "Sprout", "Veg", "Flower", "Fruit")
        }
        val wateringFreq = when (option.name) {
            "Red Chili", "Bell Pepper" -> "Every 3 days"
            "Spinach", "Lettuce", "Mustard Greens" -> "Every day"
            else -> "Every 2 days"
        }

        val entry = PlantEntry(
            id = option.name.lowercase().replace(" ", "_"),
            name = option.name,
            emoji = option.emoji,
            totalDays = option.totalDays,
            plantingYear = _plantingYear.value.toInt(),
            plantingMonth = _plantingMonth.value.toInt(),
            plantingDay = _plantingDay.value.toInt(),
            location = _location.value.ifBlank { "My Garden" },
            wateringFrequency = wateringFreq,
            stages = stages
        )

        PlantRepository.addPlant(entry)
        _savedEvent.emit(option.name)
    }
}
>>>>>>> origin/Hasna
