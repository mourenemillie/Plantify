package com.example.plantify.data.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ScheduleItem(
    val id: String,
    val title: String,
    val plantName: String,
    val time: String,
    val icon: ImageVector? = null,
    val iconRes: Int? = null,
    val iconBgColor: Color,
    val iconTint: Color,
    val isDone: Boolean = false
)

data class ScheduleGroup(
    val date: String,
    val items: List<ScheduleItem>
)
