package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
<<<<<<< HEAD
=======
import androidx.lifecycle.viewModelScope
>>>>>>> origin/Hasna
import com.example.plantify.R
import com.example.plantify.data.Plant
import com.example.plantify.data.PlantRepository
import com.example.plantify.data.PlantTask
import com.example.plantify.data.TaskType
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
<<<<<<< HEAD
import java.util.UUID
import androidx.lifecycle.viewModelScope
=======
import kotlinx.coroutines.launch
import java.util.UUID
>>>>>>> origin/Hasna

class HomeViewModel(private val plantRepository: PlantRepository) : ViewModel() {

    private val _plants = MutableStateFlow<List<Plant>>(emptyList())
    val plants: StateFlow<List<Plant>> = _plants.asStateFlow()

    private val _tasks = MutableStateFlow<List<PlantTask>>(emptyList())
    val tasks: StateFlow<List<PlantTask>> = _tasks.asStateFlow()

    private val _weatherCondition = MutableStateFlow("28°C — Sunny")
    val weatherCondition: StateFlow<String> = _weatherCondition.asStateFlow()

    init {
        loadRealData()
    }

<<<<<<< HEAD
    private fun loadMockData() {
        _plants.value = listOf(
            Plant(
                id = UUID.randomUUID().toString(),
                name = "Cherry Tomato",
                daysGrown = 15,
                progress = 0.85f,
                nextWatering = "Today",
                imageRes = R.drawable.ic_plant_tomato
            ),
            Plant(
                id = UUID.randomUUID().toString(),
                name = "Red Chili",
                daysGrown = 22,
                progress = 0.92f,
                nextWatering = "Tomorrow",
                imageRes = R.drawable.ic_plant_red_chili
            ),
            Plant(
                id = UUID.randomUUID().toString(),
                name = "Spinach",
                daysGrown = 8,
                progress = 0.45f,
                nextWatering = "Today",
                imageRes = R.drawable.ic_plant_spinach
            )
        )
=======
    fun updateWeather(condition: String) {
        _weatherCondition.value = condition
>>>>>>> origin/Hasna
    }

    private fun loadRealData() {
        viewModelScope.launch {
            combine(
                plantRepository.myPlants,
                plantRepository.allCatalog,
                plantRepository.allSchedules
            ) { myPlantsList, catalogList, schedulesList ->
                // Map Plants
                val mappedPlants = myPlantsList.map { entity ->
                    val catalogInfo = catalogList.find { it.id_tanaman == entity.id_tanaman }
                    val emoji = catalogInfo?.emoji_icon ?: "🌱"
                    val nameStr = catalogInfo?.nama_tanaman ?: "Unknown"
                    val imgRes = when {
                        nameStr.contains("tomato", ignoreCase = true) -> R.drawable.ic_plant_tomato
                        nameStr.contains("red chili", ignoreCase = true) -> R.drawable.ic_plant_red_chili
                        nameStr.contains("spinach", ignoreCase = true) -> R.drawable.ic_plant_spinach
                        nameStr.contains("mustard", ignoreCase = true) -> R.drawable.ic_plant_mustard_greens
                        nameStr.contains("lettuce", ignoreCase = true) -> R.drawable.ic_plant_lettuce
                        nameStr.contains("onion", ignoreCase = true) -> R.drawable.ic_plant_green_onion
                        nameStr.contains("pepper", ignoreCase = true) -> R.drawable.ic_plant_bell_pepper
                        nameStr.contains("cucumber", ignoreCase = true) -> R.drawable.ic_plant_cucumber
                        else -> 0
                    }
                    
                    Plant(
                        id = entity.id_kebun.toString(),
                        name = if (!entity.nama_pot.isNullOrEmpty()) entity.nama_pot else nameStr,
                        daysGrown = calculateDays(entity.tanggal_mulai_tanam),
                        progress = entity.progress_persen.toFloat() / 100f,
                        nextWatering = if (!entity.next_watering.isNullOrEmpty()) entity.next_watering else "Not Set",
                        imageUrl = "",
                        imageRes = imgRes
                    )
                }
                
                // Map Tasks (Pending only)
                val mappedTasks = schedulesList.filter { it.status_tugas != "Done" }.map { task ->
                    val typeEnum = when(task.jenis_tugas.lowercase()) {
                        "penyiraman" -> TaskType.WATERING
                        "pemupukan" -> TaskType.FERTILIZING
                        "pemanenan" -> TaskType.HARVESTING
                        else -> TaskType.PRUNING
                    }
                    val plant = myPlantsList.find { it.id_kebun == task.id_kebun }
                    val catalogInfo = catalogList.find { it.id_tanaman == plant?.id_tanaman }
                    val name = if (plant?.nama_pot.isNullOrEmpty()) {
                        catalogInfo?.nama_tanaman ?: "Unknown"
                    } else {
                        plant!!.nama_pot!!
                    }
                    
                    PlantTask(
                        id = task.id_tugas.toString(),
                        title = task.jenis_tugas,
                        subtitle = name,
                        time = task.waktu_eksekusi,
                        type = typeEnum
                    )
                }

                Pair(mappedPlants, mappedTasks)
            }.collect { (plants, tasks) ->
                _plants.value = plants
                _tasks.value = tasks
            }
        }
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
        viewModelScope.launch {
            PlantRepository.plants.collect { entries ->
                _plants.value = entries.map { entry ->
                    val currentDay = PlantRepository.calcCurrentDay(entry)
                    val progress = PlantRepository.calcProgress(entry)
                    val nextWatering = when {
                        currentDay % 2 == 0 -> "Today"
                        else -> "Tomorrow"
                    }
                    Plant(
                        id = entry.id,
                        name = entry.name,
                        daysGrown = currentDay,
                        progress = progress,
                        nextWatering = nextWatering
                    )
                }
                _tasks.value = entries
                    .filter { entry ->
                        val currentDay = PlantRepository.calcCurrentDay(entry)
                        currentDay % 2 == 0
                    }
                    .map { entry ->
                        PlantTask(
                            id = UUID.randomUUID().toString(),
                            title = "Watering",
                            subtitle = entry.name,
                            time = "08:00 AM",
                            type = TaskType.WATERING
                        )
                    }
            }
        }
    }
}