package com.example.plantify.ui.viewmodel

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
}
