package com.example.plantify.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.lifecycle.ViewModel
import com.example.plantify.data.ScheduleGroup
import com.example.plantify.data.ScheduleItem
import com.example.plantify.ui.theme.PlantifyIconBlue
import com.example.plantify.ui.theme.PlantifyIconBlueBg
import com.example.plantify.ui.theme.PlantifyIconGreen
import com.example.plantify.ui.theme.PlantifyIconGreenBg
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
                    ScheduleItem("Watering", "Cherry Tomato", "08:00 AM", Icons.Default.Info, PlantifyIconBlue, PlantifyIconBlueBg),
                    ScheduleItem("Watering", "Spinach", "08:00 AM", Icons.Default.Info, PlantifyIconBlue, PlantifyIconBlueBg),
                    ScheduleItem("Fertilizing", "Red Chili", "09:00 AM", Icons.Default.Add, PlantifyIconGreen, PlantifyIconGreenBg)
                )
            ),
            ScheduleGroup(
                "Tomorrow, April 6",
                listOf(
                    ScheduleItem("Watering", "Cherry Tomato", "08:00 AM", Icons.Default.Info, PlantifyIconBlue, PlantifyIconBlueBg),
                    ScheduleItem("Watering", "Red Chili", "08:00 AM", Icons.Default.Info, PlantifyIconBlue, PlantifyIconBlueBg)
                )
            ),
            ScheduleGroup(
                "Sunday, April 7",
                listOf(
                    ScheduleItem("Watering", "Cherry Tomato", "08:00 AM", Icons.Default.Info, PlantifyIconBlue, PlantifyIconBlueBg),
                    ScheduleItem("Watering", "Spinach", "08:00 AM", Icons.Default.Info, PlantifyIconBlue, PlantifyIconBlueBg),
                    ScheduleItem("Fertilizing", "...", "09:00 AM", Icons.Default.Add, PlantifyIconGreen, PlantifyIconGreenBg)
                )
            )
        )
    }
}
