package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantify.R
import com.example.plantify.data.ScheduleGroup
import com.example.plantify.data.ScheduleItem
import com.example.plantify.ui.theme.PlantifyWaterTeal
import com.example.plantify.ui.theme.PlantifyWaterTealBg
import com.example.plantify.ui.theme.PlantifyFertilizerAmber
import com.example.plantify.ui.theme.PlantifyFertilizerAmberBg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScheduleViewModel : ViewModel() {

    private val _scheduleGroups = MutableStateFlow<List<ScheduleGroup>>(emptyList())
    val scheduleGroups: StateFlow<List<ScheduleGroup>> = _scheduleGroups.asStateFlow()

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        _scheduleGroups.value = listOf(
            ScheduleGroup(
                "Today, April 5",
                listOf(
                    ScheduleItem(
                        "Watering",
                        "Cherry Tomato",
                        "08:00 AM",
                        R.drawable.ic_water_drop,
                        PlantifyWaterTealBg,
                        PlantifyWaterTeal
                    ),
                    ScheduleItem(
                        "Watering",
                        "Spinach",
                        "08:00 AM",
                        R.drawable.ic_water_drop,
                        PlantifyWaterTealBg,
                        PlantifyWaterTeal
                    ),
                    ScheduleItem(
                        "Fertilizing",
                        "Red Chili",
                        "09:00 AM",
                        R.drawable.ic_bolt,
                        PlantifyFertilizerAmberBg,
                        PlantifyFertilizerAmber
                    )
                )
            ),
            ScheduleGroup(
                "Tomorrow, April 6",
                listOf(
                    ScheduleItem(
                        "Watering",
                        "Cherry Tomato",
                        "08:00 AM",
                        R.drawable.ic_water_drop,
                        PlantifyWaterTealBg,
                        PlantifyWaterTeal
                    ),
                    ScheduleItem(
                        "Watering",
                        "Red Chili",
                        "08:00 AM",
                        R.drawable.ic_water_drop,
                        PlantifyWaterTealBg,
                        PlantifyWaterTeal
                    )
                )
            ),
            ScheduleGroup(
                "Sunday, April 7",
                listOf(
                    ScheduleItem(
                        "Watering",
                        "Cherry Tomato",
                        "08:00 AM",
                        R.drawable.ic_water_drop,
                        PlantifyWaterTealBg,
                        PlantifyWaterTeal
                    ),
                    ScheduleItem(
                        "Watering",
                        "Spinach",
                        "08:00 AM",
                        R.drawable.ic_water_drop,
                        PlantifyWaterTealBg,
                        PlantifyWaterTeal
                    ),
                    ScheduleItem(
                        "Fertilizing",
                        "...",
                        "09:00 AM",
                        R.drawable.ic_bolt,
                        PlantifyFertilizerAmberBg,
                        PlantifyFertilizerAmber
                    )
                )
            )
        )
    }

    fun toggleDone(item: ScheduleItem) {
        _scheduleGroups.value = _scheduleGroups.value.map { group ->
            group.copy(
                items = group.items.map {
                    if (it === item) { // Ubah di sini jadi ===
                        it.copy(isDone = !it.isDone)
                    } else {
                        it
                    }
                }
            )
        }
    }
}