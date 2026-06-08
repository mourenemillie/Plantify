package com.example.plantify.data.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Notification(
    val id: String,
    val title: String,
    val desc: String,
    val time: String,
    val icon: ImageVector? = null,
    val iconRes: Int? = null,
    val iconBgColor: Color,
    val isUnread: Boolean
)
