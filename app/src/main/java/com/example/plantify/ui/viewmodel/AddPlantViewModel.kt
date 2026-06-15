package com.example.plantify.ui.viewmodel

import com.example.plantify.BuildConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.local.entity.TaskScheduleEntity
import com.example.plantify.data.models.Province
import com.example.plantify.data.models.Regency
import com.example.plantify.data.models.District
import com.example.plantify.data.models.Village
import com.example.plantify.data.remote.AiService
import com.example.plantify.data.remote.LocationService
import com.example.plantify.data.remote.WeatherService
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPlantViewModel(
    private val repository: PlantRepository,
    private val aiService: AiService = AiService(),
    private val locationService: LocationService = LocationService(),
    private val weatherService: WeatherService = WeatherService()
) : ViewModel() {

    private val _selectedPlant = MutableStateFlow<PlantCatalogEntity?>(null)
    val selectedPlant: StateFlow<PlantCatalogEntity?> = _selectedPlant.asStateFlow()

    private val _plantingDate = MutableStateFlow(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()))
    val plantingDate: StateFlow<String> = _plantingDate.asStateFlow()

    private val _locationName = MutableStateFlow("")
    val locationName: StateFlow<String> = _locationName.asStateFlow()

    // Catalog State
    private val _catalog = MutableStateFlow<List<PlantCatalogEntity>>(emptyList())
    val catalog: StateFlow<List<PlantCatalogEntity>> = _catalog.asStateFlow()

    // Location Selection State
    private val _provinces = MutableStateFlow<List<Province>>(emptyList())
    val provinces: StateFlow<List<Province>> = _provinces.asStateFlow()

    private val _selectedProvince = MutableStateFlow<Province?>(null)
    val selectedProvince: StateFlow<Province?> = _selectedProvince.asStateFlow()

    private val _regencies = MutableStateFlow<List<Regency>>(emptyList())
    val regencies: StateFlow<List<Regency>> = _regencies.asStateFlow()

    private val _selectedRegency = MutableStateFlow<Regency?>(null)
    val selectedRegency: StateFlow<Regency?> = _selectedRegency.asStateFlow()

    private val _districts = MutableStateFlow<List<District>>(emptyList())
    val districts: StateFlow<List<District>> = _districts.asStateFlow()

    private val _selectedDistrict = MutableStateFlow<District?>(null)
    val selectedDistrict: StateFlow<District?> = _selectedDistrict.asStateFlow()

    private val _villages = MutableStateFlow<List<Village>>(emptyList())
    val villages: StateFlow<List<Village>> = _villages.asStateFlow()

    private val _selectedVillage = MutableStateFlow<Village?>(null)
    val selectedVillage: StateFlow<Village?> = _selectedVillage.asStateFlow()

    // AI Recommendation State
    private val _aiRecommendation = MutableStateFlow<String>("Select a plant and location to see recommendations.")
    val aiRecommendation: StateFlow<String> = _aiRecommendation.asStateFlow()

    private val _aiTasks = MutableStateFlow<List<TaskScheduleEntity>>(emptyList())

    private val _isLoadingAi = MutableStateFlow(false)
    val isLoadingAi: StateFlow<Boolean> = _isLoadingAi.asStateFlow()

    init {
        loadProvinces()
    }

    fun loadCatalog(preSelectedId: Int = 0) {
        viewModelScope.launch {
            repository.allCatalog.collect { list ->
                _catalog.value = list
                if (preSelectedId != 0) {
                    list.find { it.id_tanaman == preSelectedId }?.let {
                        selectPlant(it)
                    }
                }
            }
        }
    }

    private fun loadProvinces() {
        viewModelScope.launch {
            _provinces.value = locationService.getProvinces()
        }
    }

    fun selectProvince(province: Province) {
        _selectedProvince.value = province
        _selectedRegency.value = null
        _selectedDistrict.value = null
        _selectedVillage.value = null
        _regencies.value = emptyList()
        viewModelScope.launch {
            _regencies.value = locationService.getRegencies(province.code)
        }
    }

    fun selectRegency(regency: Regency) {
        _selectedRegency.value = regency
        _selectedDistrict.value = null
        _selectedVillage.value = null
        _districts.value = emptyList()
        viewModelScope.launch {
            _districts.value = locationService.getDistricts(regency.code)
        }
    }

    fun selectDistrict(district: District) {
        _selectedDistrict.value = district
        _selectedVillage.value = null
        _villages.value = emptyList()
        viewModelScope.launch {
            _villages.value = locationService.getVillages(district.code)
        }
    }

    fun selectVillage(village: Village) {
        _selectedVillage.value = village
        generateAiRecommendation()
    }

    fun selectPlant(species: PlantCatalogEntity) {
        _selectedPlant.value = species
        generateAiRecommendation()
    }

    fun updateLocationName(newName: String) {
        _locationName.value = newName
    }

    private fun generateAiRecommendation() {
        val plant = _selectedPlant.value ?: return
        val village = _selectedVillage.value ?: return

        viewModelScope.launch {
            _isLoadingAi.value = true
            _aiRecommendation.value = "Fetching weather and generating recommendation..."
            
            val weather = weatherService.getCurrentWeather(village.code)
            val result = aiService.generateCareSchedule(plant.nama_tanaman, "Normal", weather)
            
            println("AI Response: $result")

            if (result != null) {
                try {
                    val json = JSONObject(result)
                    _aiRecommendation.value = json.optString("recommendation_text", "Here is your care schedule.")
                    val tasksJson = json.optJSONArray("tasks")
                    val tasks = mutableListOf<TaskScheduleEntity>()
                    if (tasksJson != null) {
                        for (i in 0 until tasksJson.length()) {
                            val obj = tasksJson.getJSONObject(i)
                            tasks.add(TaskScheduleEntity(
                                id_kebun = 0, // Placeholder
                                jenis_tugas = obj.getString("type"),
                                waktu_eksekusi = obj.getString("time"),
                                status_tugas = "Pending"
                            ))
                        }
                    }
                    _aiTasks.value = tasks
                } catch (e: Exception) {
                    _aiRecommendation.value = "Failed to parse AI response."
                }
            } else {
                _aiRecommendation.value = "Failed to get AI recommendation."
            }
            _isLoadingAi.value = false
        }
    }

    fun savePlantWithAiSchedule(onSuccess: () -> Unit) {
        val species = _selectedPlant.value ?: return
        viewModelScope.launch {
            val plant = MyPlantEntity(
                id_tanaman = species.id_tanaman,
                tanggal_mulai_tanam = _plantingDate.value,
                nama_pot = _locationName.value,
                progress_persen = 0f,
                next_watering = _aiTasks.value.find { it.jenis_tugas == "Watering" }?.waktu_eksekusi,
                status_tanaman = "Growing"
            )
            repository.addPlantWithSchedules(plant, _aiTasks.value)
            onSuccess()
        }
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

    fun generateAiSchedule(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val condition = "Location: ${_location.value}, Date: ${_plantingDate.value}, Weather: 32°C Sunny"
            
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
