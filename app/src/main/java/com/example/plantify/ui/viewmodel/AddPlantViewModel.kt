package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.BuildConfig
import com.example.plantify.data.PlantEntry
import com.example.plantify.data.PlantRepository
import com.example.plantify.data.repository.PlantRepository as RoomPlantRepository
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.local.entity.TaskScheduleEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import javax.net.ssl.HttpsURLConnection

data class PlantOption(val name: String, val emoji: String, val totalDays: Int, val idTanaman: Int)

sealed class AiRecommendationState {
    object Idle : AiRecommendationState()
    object Loading : AiRecommendationState()
    data class Success(val recommendation: String) : AiRecommendationState()
    data class Error(val message: String) : AiRecommendationState()
}

class AddPlantViewModel(private val roomRepository: RoomPlantRepository? = null) : ViewModel() {
    private val GROQ_API_KEY = BuildConfig.GROQ_API_KEY
    private val GROQ_URL = "https://api.groq.com/openai/v1/chat/completions"

    val plantOptions = listOf(
        PlantOption("Tomato", "🍅", 70, 1),
        PlantOption("Red Chili", "🌶️", 85, 2),
        PlantOption("Spinach", "🥬", 45, 3),
        PlantOption("Mustard Greens", "🥬", 35, 4),
        PlantOption("Lettuce", "🥗", 50, 5),
        PlantOption("Green Onion", "🌿", 70, 6),
        PlantOption("Bell Pepper", "🫑", 80, 7),
        PlantOption("Cucumber", "🥒", 55, 8),
    )

    private val _selectedPlant = MutableStateFlow<PlantOption?>(null)
    val selectedPlant: StateFlow<PlantOption?> = _selectedPlant.asStateFlow()

    private val _plantingDay = MutableStateFlow("")
    val plantingDay: StateFlow<String> = _plantingDay.asStateFlow()

    private val _plantingMonth = MutableStateFlow("")
    val plantingMonth: StateFlow<String> = _plantingMonth.asStateFlow()

    private val _plantingYear = MutableStateFlow("")
    val plantingYear: StateFlow<String> = _plantingYear.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

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
        tryAutoTriggerAi()
    }

    fun updateDay(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) {
            _plantingDay.value = value
            _dateError.value = false
            tryAutoTriggerAi()
        }
    }

    fun updateMonth(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) {
            _plantingMonth.value = value
            _dateError.value = false
            tryAutoTriggerAi()
        }
    }

    fun updateYear(value: String) {
        if (value.length <= 4 && value.all { it.isDigit() }) {
            _plantingYear.value = value
            _dateError.value = false
            tryAutoTriggerAi()
        }
    }

    fun updateLocation(value: String) { _location.value = value }

    /** Auto-trigger AI begitu plant + tanggal (4 digit) lengkap */
    private fun tryAutoTriggerAi() {
        val plant = _selectedPlant.value ?: return
        val d = _plantingDay.value.toIntOrNull() ?: return
        val m = _plantingMonth.value.toIntOrNull() ?: return
        val y = _plantingYear.value.toIntOrNull() ?: return
        if (d !in 1..31 || m !in 1..12 || y < 2020 || _plantingYear.value.length < 4) return
        if (_aiState.value is AiRecommendationState.Idle) {
            getAiRecommendation()
        }
    }

    fun getAiRecommendation() {
        val plant = _selectedPlant.value ?: run { _plantError.value = true; return }
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
                    Kamu adalah ahli perawatan tanaman. Berikan rekomendasi jadwal perawatan singkat untuk:
                    - Tanaman: ${plant.name}
                    - Tanggal tanam: $dateStr
                    - Lokasi: $location
                    - Perkiraan panen: ${plant.totalDays} hari

                    Jawab 2-3 kalimat dalam Bahasa Indonesia. Sebutkan: frekuensi penyiraman, jadwal pemupukan, satu tips. Buat menyemangati!
                    """.trimIndent()

                val result = callGroqApi(prompt)
                _aiState.value = AiRecommendationState.Success(result)
            } catch (e: Exception) {
                android.util.Log.e("GroqAPI", "Error: ${e.message}", e)
                if (e.message?.contains("401") == true || GROQ_API_KEY.isEmpty() || GROQ_API_KEY == "YOUR_API_KEY") {
                    // Fallback to mock data if API key is invalid/missing
                    val mockRecommendation = "Siram ${plant.name} setiap hari. Berikan pupuk organik setiap 2 minggu. Pastikan tanaman mendapat sinar matahari yang cukup. Semangat bertanam!"
                    _aiState.value = AiRecommendationState.Success(mockRecommendation)
                } else {
                    _aiState.value = AiRecommendationState.Error("Gagal terhubung ke AI: ${e.message}")
                }
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
                    put(JSONObject().apply { put("role", "user"); put("content", prompt) })
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
            JSONObject(response).getJSONArray("choices")
                .getJSONObject(0).getJSONObject("message").getString("content").trim()
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
        val wateringFreq = when (option.name) {
            "Red Chili", "Bell Pepper" -> "Every 3 days"
            "Spinach", "Lettuce", "Mustard Greens" -> "Every day"
            else -> "Every 2 days"
        }
        val stages = when (option.name) {
            "Spinach", "Mustard Greens", "Lettuce", "Green Onion" ->
                listOf("Seed", "Sprout", "Veg", "Harvest")
            else -> listOf("Seed", "Sprout", "Veg", "Flower", "Fruit")
        }

        // === Simpan ke Room DB ===
        if (roomRepository != null) {
            val dateStr = "${_plantingYear.value}-${_plantingMonth.value.padStart(2,'0')}-${_plantingDay.value.padStart(2,'0')}"

            val plant = MyPlantEntity(
                id_tanaman = option.idTanaman,  // ID sesuai jenis tanaman
                tanggal_mulai_tanam = dateStr,
                nama_pot = option.name,
                progress_persen = 0f,
                next_watering = "08:00",
                status_tanaman = "Sehat"
            )
            val newId = roomRepository.addPlant(plant)

            // Buat schedule Penyiraman + Pemupukan
            val schedules = listOf(
                TaskScheduleEntity(id_kebun = newId.toInt(), jenis_tugas = "Penyiraman", waktu_eksekusi = "07:00", status_tugas = "Pending"),
                TaskScheduleEntity(id_kebun = newId.toInt(), jenis_tugas = "Pemupukan", waktu_eksekusi = "16:00", status_tugas = "Pending")
            )
            roomRepository.saveSchedules(schedules)
        }

        // Juga simpan ke mock PlantRepository (HomeScreen)
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
