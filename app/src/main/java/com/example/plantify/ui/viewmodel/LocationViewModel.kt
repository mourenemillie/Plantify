package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.repository.LocationRepository
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LocationViewModel(
    private val locationRepository: LocationRepository,
    private val plantRepository: PlantRepository
) : ViewModel() {

    private val _weatherCondition = MutableStateFlow<String?>(null)
    val weatherCondition: StateFlow<String?> = _weatherCondition

    private val _locationText = MutableStateFlow<String>("Locating...")
    val locationText: StateFlow<String> = _locationText

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _schedulePreview = MutableStateFlow<List<com.example.plantify.data.local.entity.TaskScheduleEntity>?>(null)
    val schedulePreview: StateFlow<List<com.example.plantify.data.local.entity.TaskScheduleEntity>?> = _schedulePreview

    fun fetchLocationAndWeather(lat: Double, lon: Double) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _locationText.value = "Menganalisis lokasi..."
            try {
                // 1. Reverse Geocode with OSM Nominatim
                val address = locationRepository.reverseGeocode(lat, lon)
                if (address != null) {
                    val villageName = address.village ?: address.suburb ?: address.town ?: "Unknown"
                    val districtName = address.city_district ?: address.county ?: "Unknown"
                    val regencyName = address.city ?: address.county ?: "Unknown"
                    val provinceName = address.state ?: "Unknown"

                    _locationText.value = "$villageName, $districtName"

                    // 2. Hierarchical Match wilayah.id
                    val provinces = locationRepository.getProvinces()
                    val provId = provinces.find { it.name.equals(provinceName, true) || it.name.contains(provinceName, true) || provinceName.contains(it.name, true) }?.code
                    
                    if (provId != null) {
                        val regencies = locationRepository.getRegencies(provId)
                        val cleanRegencyName = regencyName.replace("Kota ", "", true).replace("Kabupaten ", "", true)
                        val regId = regencies.find { it.name.contains(cleanRegencyName, true) || cleanRegencyName.contains(it.name, true) }?.code ?: regencies.firstOrNull()?.code

                        if (regId != null) {
                            val districts = locationRepository.getDistricts(regId)
                            val cleanDistrictName = districtName.replace("Kecamatan ", "", true)
                            val distId = districts.find { it.name.contains(cleanDistrictName, true) || cleanDistrictName.contains(it.name, true) }?.code ?: districts.firstOrNull()?.code

                            if (distId != null) {
                                val villages = locationRepository.getVillages(distId)
                                val cleanVillageName = villageName.replace("Kelurahan ", "", true).replace("Desa ", "", true)
                                val villId = villages.find { it.name.contains(cleanVillageName, true) || cleanVillageName.contains(it.name, true) }?.code ?: villages.firstOrNull()?.code

                                if (villId != null) {
                                    // wilayah.id format is already correct: 11.01.01.1001
                                    val adm4 = villId
                                    
                                    // 4. Fetch BMKG
                                    val weather = locationRepository.getBmkgWeather(adm4)
                                    _weatherCondition.value = weather
                                    _locationText.value = "$villageName, $districtName"
                                } else {
                                    _weatherCondition.value = "Unknown Village"
                                }
                            } else {
                                _weatherCondition.value = "Unknown District"
                            }
                        } else {
                            _weatherCondition.value = "Unknown Regency"
                        }
                    } else {
                        _weatherCondition.value = "Unknown Province"
                    }
                } else {
                    _locationText.value = "Lokasi tidak diketahui"
                    _weatherCondition.value = "Gagal mengambil data lokasi."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _weatherCondition.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLocationManually(newLocation: String) {
        _locationText.value = newLocation
        // If they manually edit the location, we bypass BMKG strictly to avoid being blocked.
        // We set the weather condition to their manual string. Gemini AI is smart enough to infer 
        // the general weather for that region (e.g. "Rajabasa, Lampung") if BMKG data is missing.
        _weatherCondition.value = "Location: $newLocation"
    }

    fun generateScheduleForPlant(plantName: String, idKebun: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val condition = _weatherCondition.value ?: "Normal/Unknown"
            
            var targetIdKebun = idKebun
            if (targetIdKebun == 0) {
                // Determine plant ID from name if possible (very basic matching for now)
                var idTanaman = 1 // Default
                val catalog = plantRepository.allCatalog.first()
                val matched = catalog.find { plantName.contains(it.nama_tanaman, ignoreCase = true) }
                if (matched != null) {
                    idTanaman = matched.id_tanaman
                }

                val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val newPlant = com.example.plantify.data.local.entity.MyPlantEntity(
                    id_tanaman = idTanaman,
                    tanggal_mulai_tanam = format.format(java.util.Date()),
                    nama_pot = plantName,
                    progress_persen = 0f,
                    next_watering = "",
                    status_tanaman = "Sehat"
                )
                targetIdKebun = plantRepository.addPlant(newPlant).toInt()
            }
            
            val schedules = plantRepository.generateScheduleWithAI(plantName, condition, targetIdKebun)
            _schedulePreview.value = schedules
            _isLoading.value = false
        }
    }

    fun savePreviewedSchedule() {
        viewModelScope.launch {
            val schedules = _schedulePreview.value
            if (schedules != null && schedules.isNotEmpty()) {
                plantRepository.saveSchedules(schedules)
            }
            _schedulePreview.value = null
        }
    }

    fun dismissPreview() {
        _schedulePreview.value = null
    }
}
