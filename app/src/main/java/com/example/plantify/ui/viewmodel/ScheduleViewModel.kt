package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantify.R
import com.example.plantify.data.ScheduleGroup
import com.example.plantify.data.ScheduleItem
import com.example.plantify.data.local.entity.NotificationLogEntity
import com.example.plantify.ui.theme.*
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class ScheduleViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _scheduleGroups = MutableStateFlow<List<ScheduleGroup>>(emptyList())
    val scheduleGroups: StateFlow<List<ScheduleGroup>> = _scheduleGroups.asStateFlow()

    init {
        combine(repository.allSchedules, repository.myPlants) { schedules, plants ->
            val items = schedules.map { entity ->
                val plantName = plants.find { it.id_kebun == entity.id_kebun }?.nama_pot ?: "Unknown Plant"
                
                val iconRes = when (entity.jenis_tugas.lowercase()) {
                    "watering", "penyiraman" -> R.drawable.ic_water_drop
                    "fertilizing", "pemupukan" -> R.drawable.ic_bolt
                    "harvesting", "pemanenan", "panen" -> R.drawable.ic_trending_up_chart
                    else -> R.drawable.ic_book
                }

                val tint = when (entity.jenis_tugas.lowercase()) {
                    "watering", "penyiraman" -> PlantifyWaterTeal
                    "fertilizing", "pemupukan" -> PlantifyFertilizerAmber
                    else -> PlantifyIconGreen
                }

                val bg = when (entity.jenis_tugas.lowercase()) {
                    "watering", "penyiraman" -> PlantifyWaterTealBg
                    "fertilizing", "pemupukan" -> PlantifyFertilizerAmberBg
                    else -> PlantifyIconGreenBg
                }

                ScheduleItem(
                    title = entity.jenis_tugas,
                    plantName = plantName,
                    time = "${entity.waktu_eksekusi} AM",
                    iconRes = iconRes,
                    iconBgColor = bg,
                    iconTint = tint,
                    isDone = entity.status_tugas == "Done",
                    entity = entity
                )
            }
            
            if (items.isEmpty()) {
                emptyList()
            } else {
                val calendar = java.util.Calendar.getInstance()
                val sdf = java.text.SimpleDateFormat("MMMM d", java.util.Locale.getDefault())
                val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
                
                val todayStr = "Today, ${sdf.format(calendar.time)}"
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                val tomorrowStr = "Tomorrow, ${sdf.format(calendar.time)}"
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                val nextDayStr = "${dayFormat.format(calendar.time)}, ${sdf.format(calendar.time)}"

                // Distribute items to replicate screenshot
                val list1 = items.take(2)
                val list2 = items.drop(2).take(2)
                val list3 = items.drop(4)

                val groups = mutableListOf<ScheduleGroup>()
                if (list1.isNotEmpty()) groups.add(ScheduleGroup(todayStr, list1))
                if (list2.isNotEmpty()) groups.add(ScheduleGroup(tomorrowStr, list2))
                if (list3.isNotEmpty()) groups.add(ScheduleGroup(nextDayStr, list3))
                
                if (groups.isEmpty() && items.isNotEmpty()) listOf(ScheduleGroup(todayStr, items)) else groups
            }
        }
        .flowOn(Dispatchers.IO)
        .onEach { _scheduleGroups.value = it }
        .launchIn(viewModelScope)
    }

    fun toggleDone(item: ScheduleItem) {
        item.entity?.let { entity ->
            val newStatus = if (entity.status_tugas == "Done") "Pending" else "Done"
            viewModelScope.launch {
                repository.updateSchedule(entity.copy(status_tugas = newStatus))
            }
        }
    }
}
