package com.example.plantify.data

data class Plant(
    val id: String,
    val name: String,
    val daysGrown: Int,
    val progress: Float,
    val nextWatering: String
)

data class PlantTask(
    val id: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val type: TaskType
)

enum class TaskType {
    WATERING, FERTILIZING, PRUNING, HARVESTING
}

data class AlertItem(
    val title: String,
    val desc: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconBgColor: androidx.compose.ui.graphics.Color,
    val isUnread: Boolean
)

data class PlantCategory(
    val name: String,
    val difficulty: String,
    val difficultyColor: androidx.compose.ui.graphics.Color,
    val duration: String,
    val watering: String,
    val imageRes: Int = 0
)

data class ScheduleItem(
    val title: String,
    val plantName: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconBgColor: androidx.compose.ui.graphics.Color,
    val iconTint: androidx.compose.ui.graphics.Color,
    val isDone: Boolean = false
)

data class ScheduleGroup(
    val date: String,
    val items: List<ScheduleItem>
)

data class GrowthProgressItem(
    val plantEmoji: String,
    val plantName: String,
    val currentDay: Int,
    val totalDays: Int,
    val stages: List<String>,
    val currentStageIndex: Int,
    val estimateDate: String,
    val progress: Float
)
