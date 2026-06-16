package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.local.entity.MyPlantEntity
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.local.entity.TaskScheduleEntity
import com.example.plantify.data.remote.model.Wilayah
import com.example.plantify.data.remote.AiService
import com.example.plantify.data.repository.LocationRepository
import com.example.plantify.data.remote.LocationApiClient
import com.example.plantify.data.remote.NominatimApiClient
import com.example.plantify.data.remote.BmkgApiClient
import com.example.plantify.data.remote.WeatherService
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPlantViewModel(
    private val repository: PlantRepository,
    private val aiService: AiService = AiService(),
    private val locationRepository: LocationRepository = LocationRepository(
        LocationApiClient.instance, 
        NominatimApiClient.instance, 
        BmkgApiClient.instance
    ),
    private val weatherService: WeatherService = WeatherService()
) : ViewModel() {

    private val _selectedPlant = MutableStateFlow<PlantCatalogEntity?>(null)
    val selectedPlant: StateFlow<PlantCatalogEntity?> = _selectedPlant.asStateFlow()

    private val _plantingDate = MutableStateFlow(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()))
    val plantingDate: StateFlow<String> = _plantingDate.asStateFlow()

    private val _locationName = MutableStateFlow("")
    val locationName: StateFlow<String> = _locationName.asStateFlow()

    // Daftar tanaman dari database
    private val _catalog = MutableStateFlow<List<PlantCatalogEntity>>(emptyList())
    val catalog: StateFlow<List<PlantCatalogEntity>> = _catalog.asStateFlow()

    // Pilihan wilayah berjenjang
    private val _provinces = MutableStateFlow<List<Wilayah>>(emptyList())
    val provinces: StateFlow<List<Wilayah>> = _provinces.asStateFlow()

    private val _selectedProvince = MutableStateFlow<Wilayah?>(null)
    val selectedProvince: StateFlow<Wilayah?> = _selectedProvince.asStateFlow()

    private val _regencies = MutableStateFlow<List<Wilayah>>(emptyList())
    val regencies: StateFlow<List<Wilayah>> = _regencies.asStateFlow()

    private val _selectedRegency = MutableStateFlow<Wilayah?>(null)
    val selectedRegency: StateFlow<Wilayah?> = _selectedRegency.asStateFlow()

    private val _districts = MutableStateFlow<List<Wilayah>>(emptyList())
    val districts: StateFlow<List<Wilayah>> = _districts.asStateFlow()

    private val _selectedDistrict = MutableStateFlow<Wilayah?>(null)
    val selectedDistrict: StateFlow<Wilayah?> = _selectedDistrict.asStateFlow()

    private val _villages = MutableStateFlow<List<Wilayah>>(emptyList())
    val villages: StateFlow<List<Wilayah>> = _villages.asStateFlow()

    private val _selectedVillage = MutableStateFlow<Wilayah?>(null)
    val selectedVillage: StateFlow<Wilayah?> = _selectedVillage.asStateFlow()

    // Status rekomendasi AI
    private val _aiRecommendation = MutableStateFlow("Select a plant and location to see recommendations.")
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
            _provinces.value = locationRepository.getProvinces()
        }
    }

    // // Fungsi Dropdown Wilayah
    fun selectProvince(province: Wilayah) {
        _selectedProvince.value = province
        _selectedRegency.value = null
        _selectedDistrict.value = null
        _selectedVillage.value = null
        _regencies.value = emptyList()
        viewModelScope.launch {
            _regencies.value = locationRepository.getRegencies(province.code)
        }
    }

    fun selectRegency(regency: Wilayah) {
        _selectedRegency.value = regency
        _selectedDistrict.value = null
        _selectedVillage.value = null
        _districts.value = emptyList()
        viewModelScope.launch {
            _districts.value = locationRepository.getDistricts(regency.code)
        }
    }

    fun selectDistrict(district: Wilayah) {
        _selectedDistrict.value = district
        _selectedVillage.value = null
        _villages.value = emptyList()
        viewModelScope.launch {
            _villages.value = locationRepository.getVillages(district.code)
        }
    }

    fun selectVillage(village: Wilayah) {
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

    // // Logika AI berdasarkan lokasi dropdown
    fun generateAiRecommendation() {
        val plant = _selectedPlant.value ?: return
        val village = _selectedVillage.value ?: return

        viewModelScope.launch {
            _isLoadingAi.value = true
            _aiRecommendation.value = "Fetching weather and generating recommendation..."

            val weather = weatherService.getCurrentWeather(village.code)
            val result = aiService.generateCareSchedule(plant.nama_tanaman, "Normal", weather)

            if (result != null) {
                if (result.startsWith("ERROR:")) {
                    _aiRecommendation.value = result
                } else {
                    try {
                        val json = JSONObject(result)
                        _aiRecommendation.value = json.optString("recommendation_text", "Here is your care schedule.")
                        val tasksJson = json.optJSONArray("tasks")
                        val tasks = mutableListOf<TaskScheduleEntity>()
                        if (tasksJson != null) {
                            for (i in 0 until tasksJson.length()) {
                                val obj = tasksJson.getJSONObject(i)
                                tasks.add(TaskScheduleEntity(
                                    id_kebun = 0,
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
                }
            } else {
                _aiRecommendation.value = "Failed to get AI recommendation."
            }
            _isLoadingAi.value = false
        }
    }

    // // Simpan ke Database
    fun savePlantWithAiSchedule(onSuccess: () -> Unit) {
        val species = _selectedPlant.value ?: return
        viewModelScope.launch {
            val plant = MyPlantEntity(
                id_tanaman = species.id_tanaman,
                tanggal_mulai_tanam = _plantingDate.value,
                nama_pot = _locationName.value,
                progress_persen = 0f,
                next_watering = _aiTasks.value.find { it.jenis_tugas == "Watering" }?.waktu_eksekusi ?: "07:00 AM",
                status_tanaman = "Growing"
            )
            repository.addPlantWithSchedules(plant, _aiTasks.value)
            onSuccess()
        }
    }

    fun savePlantManually(plantName: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            var idTanaman = 1
            try {
                val catalogList = repository.allCatalog.first()
                val matched = catalogList.find { plantName.contains(it.nama_tanaman, ignoreCase = true) }
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
                next_watering = "07:00 AM",
                status_tanaman = "Sehat"
            )

            repository.addPlant(newPlant)
            onComplete()
        }
    }
}