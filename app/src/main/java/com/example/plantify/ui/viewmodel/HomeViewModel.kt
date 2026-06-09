package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantify.R
import com.example.plantify.data.Plant
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
import com.example.plantify.R

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

    private fun loadMockData() {
        _plants.value = listOf(
            Plant(
                id = UUID.randomUUID().toString(),
                name = "Cherry Tomato",
                daysGrown = 15,
                progress = 0.85f,
                nextWatering = "Today",
                imageUrl = "https://images.unsplash.com/photo-1592841200221-a6898f307baa?w=200&h=200&fit=crop",
                imageRes = R.drawable.ic_plant_tomato
            ),
            Plant(
                id = UUID.randomUUID().toString(),
                name = "Red Chili",
                daysGrown = 22,
                progress = 0.92f,
                nextWatering = "Tomorrow",
                imageUrl = "https://images.unsplash.com/photo-1588252303782-cb80119abd6e?w=200&h=200&fit=crop",
                imageRes = R.drawable.ic_plant_red_chili
            ),
            Plant(
                id = UUID.randomUUID().toString(),
                name = "Spinach",
                daysGrown = 8,
                progress = 0.45f,
                nextWatering = "Today",
                imageUrl = "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=200&h=200&fit=crop",
                imageRes = R.drawable.ic_plant_spinach
            )
        )

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
                    val imgRes = when(nameStr.lowercase()) {
                        "tomato" -> R.drawable.ic_plant_tomato
                        "red chili" -> R.drawable.ic_plant_red_chili
                        "spinach" -> R.drawable.ic_plant_spinach
                        "mustard greens" -> R.drawable.ic_plant_mustard_greens
                        "lettuce" -> R.drawable.ic_plant_lettuce
                        "green onion" -> R.drawable.ic_plant_green_onion
                        "bell pepper" -> R.drawable.ic_plant_bell_pepper
                        "cucumber" -> R.drawable.ic_plant_cucumber
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
}
