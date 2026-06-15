package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.repository.PlantRepository
import com.example.plantify.data.AlertItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AlertsViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _alerts = MutableStateFlow<List<AlertItem>>(emptyList())
    val alerts: StateFlow<List<AlertItem>> = _alerts.asStateFlow()

    init {
        observeSchedules()
    }

    private fun observeSchedules() {
        combine(repository.allSchedules, repository.myPlants) { schedules, plants ->
            val dynamicAlerts = mutableListOf<AlertItem>()

            val pendingSchedules = schedules.filter { it.status_tugas != "Done" }
            pendingSchedules.forEach { schedule ->
                val plantName = plants.find { it.id_kebun == schedule.id_kebun }?.nama_pot ?: "Your plant"
                val (title, desc, icon, bgColor) = when (schedule.jenis_tugas.lowercase()) {
                    "penyiraman", "watering" -> listOf(
                        "Time to water your plant! 💧",
                        "$plantName needs watering at ${schedule.waktu_eksekusi}",
                        Icons.Default.Notifications,
                        Color(0xFFE3F2FD)
                    )
                    "pemupukan", "fertilizing" -> listOf(
                        "Fertilizing reminder 🌿",
                        "$plantName needs fertilizing today",
                        Icons.Default.Star,
                        Color(0xFFE8F5E9)
                    )
                    "pemanenan", "harvesting" -> listOf(
                        "Harvest time! 🎉",
                        "$plantName is ready to harvest!",
                        Icons.Default.CheckCircle,
                        Color(0xFFFFF3E0)
                    )
                    else -> listOf(
                        "Care reminder",
                        "$plantName needs attention: ${schedule.jenis_tugas}",
                        Icons.Default.Info,
                        Color(0xFFF5F5F5)
                    )
                }
                @Suppress("UNCHECKED_CAST")
                dynamicAlerts.add(
                    AlertItem(
                        title = title as String,
                        desc = desc as String,
                        time = "Scheduled: ${schedule.waktu_eksekusi}",
                        icon = icon as androidx.compose.ui.graphics.vector.ImageVector,
                        iconBgColor = bgColor as Color,
                        isUnread = true
                    )
                )
            }

            val doneSchedules = schedules.filter { it.status_tugas == "Done" }.take(3)
            doneSchedules.forEach { schedule ->
                val plantName = plants.find { it.id_kebun == schedule.id_kebun }?.nama_pot ?: "Your plant"
                dynamicAlerts.add(
                    AlertItem(
                        title = "${schedule.jenis_tugas} completed ✅",
                        desc = "Great job! You completed ${schedule.jenis_tugas.lowercase()} for $plantName",
                        time = "Done",
                        icon = Icons.Default.CheckCircle,
                        iconBgColor = Color(0xFFE8F5E9),
                        isUnread = false
                    )
                )
            }

            // Fallback jika tidak ada data sama sekali
            if (dynamicAlerts.isEmpty()) {
                dynamicAlerts.addAll(getDefaultAlerts())
            }

            dynamicAlerts.toList()
        }
        .onEach { _alerts.value = it }
        .launchIn(viewModelScope)
    }

    fun markAllAsRead() {
        _alerts.value = _alerts.value.map { it.copy(isUnread = false) }
    }

    private fun getDefaultAlerts(): List<AlertItem> = listOf(
        AlertItem(
            title = "Welcome to Plantify! 🌱",
            desc = "Add your first plant to start getting personalized reminders",
            time = "Now",
            icon = Icons.Default.Star,
            iconBgColor = Color(0xFFE8F5E9),
            isUnread = true
        ),
        AlertItem(
            title = "Plant care tip 💡",
            desc = "Most vegetables need at least 6 hours of sunlight per day",
            time = "Tips",
            icon = Icons.Default.Info,
            iconBgColor = Color(0xFFFFF3E0),
            isUnread = false
        )
    )
}
