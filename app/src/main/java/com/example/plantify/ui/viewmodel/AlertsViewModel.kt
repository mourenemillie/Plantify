package com.example.plantify.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.plantify.data.AlertItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlertsViewModel : ViewModel() {

    private val _alerts = MutableStateFlow<List<AlertItem>>(emptyList())
    val alerts: StateFlow<List<AlertItem>> = _alerts.asStateFlow()

    init {
        loadAlerts()
    }

    private fun loadAlerts() {
        _alerts.value = listOf(
            AlertItem(
                title = "Time to water your plants",
                desc = "Cherry Tomato and Spinach need watering",
                time = "2 min ago",
                icon = Icons.Default.Notifications,
                iconBgColor = Color(0xFFE3F2FD),
                isUnread = true
            ),
            AlertItem(
                title = "Fertilizing reminder",
                desc = "Red Chili needs fertilizing today",
                time = "1 hour ago",
                icon = Icons.Default.ArrowForward,
                iconBgColor = Color(0xFFE8F5E9),
                isUnread = true
            ),
            AlertItem(
                title = "Plant milestone reached!",
                desc = "Your Spinach has grown for 1 week",
                time = "3 hours ago",
                icon = Icons.Default.Star,
                iconBgColor = Color(0xFFFFF3E0),
                isUnread = false
            ),
            AlertItem(
                title = "Watering completed",
                desc = "Great job! You watered Basil",
                time = "Yesterday",
                icon = Icons.Default.CheckCircle,
                iconBgColor = Color(0xFFE3F2FD),
                isUnread = false
            ),
            AlertItem(
                title = "Plant care tip",
                desc = "Tomatoes grow better with consistent watering",
                time = "2 days ago",
                icon = Icons.Default.Info,
                iconBgColor = Color(0xFFF5F5F5),
                isUnread = false
            )
        )
    }

    fun markAllAsRead() {
        _alerts.value = _alerts.value.map { it.copy(isUnread = false) }
    }
}
