package com.example.plantify.data

import com.example.plantify.R
import com.example.plantify.ui.theme.PlantifyFertilizerAmber
import com.example.plantify.ui.theme.PlantifyFertilizerAmberBg
import com.example.plantify.ui.theme.PlantifyWaterTeal
import com.example.plantify.ui.theme.PlantifyWaterTealBg
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ScheduleRepository {

    private val _scheduleGroups = MutableStateFlow<List<ScheduleGroup>>(emptyList())
    val scheduleGroups: StateFlow<List<ScheduleGroup>> = _scheduleGroups.asStateFlow()

    init {
        loadDefaultSchedule()
    }

    private fun loadDefaultSchedule() {
        val dateFormatter = SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH)
        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        val dayAfter = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 2) }

        _scheduleGroups.value = listOf(
            ScheduleGroup(
                "Today, ${SimpleDateFormat("MMMM d", Locale.ENGLISH).format(today.time)}",
                listOf(
                    ScheduleItem("Watering", "Cherry Tomato", "08:00 AM", R.drawable.ic_water_drop, PlantifyWaterTealBg, PlantifyWaterTeal),
                    ScheduleItem("Watering", "Spinach", "08:00 AM", R.drawable.ic_water_drop, PlantifyWaterTealBg, PlantifyWaterTeal),
                    ScheduleItem("Fertilizing", "Red Chili", "09:00 AM", R.drawable.ic_bolt, PlantifyFertilizerAmberBg, PlantifyFertilizerAmber)
                )
            ),
            ScheduleGroup(
                "Tomorrow, ${SimpleDateFormat("MMMM d", Locale.ENGLISH).format(tomorrow.time)}",
                listOf(
                    ScheduleItem("Watering", "Cherry Tomato", "08:00 AM", R.drawable.ic_water_drop, PlantifyWaterTealBg, PlantifyWaterTeal),
                    ScheduleItem("Watering", "Red Chili", "08:00 AM", R.drawable.ic_water_drop, PlantifyWaterTealBg, PlantifyWaterTeal)
                )
            )
        )
    }

    fun addScheduleFromAi(plantName: String, aiRecommendation: String) {
        val dateFormatter = SimpleDateFormat("MMMM d", Locale.ENGLISH)
        val today = Calendar.getInstance()
        val wateringDays = extractWateringInterval(aiRecommendation)
        val fertilizingDays = extractFertilizingInterval(aiRecommendation)
        val wateringTime = extractTime(aiRecommendation) ?: "07:00 AM"

        val newItems = mutableListOf<ScheduleItem>()
        val newGroups = _scheduleGroups.value.toMutableList()

        for (i in 0..6 step wateringDays) {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, i) }
            val dateLabel = when (i) {
                0 -> "Today, ${dateFormatter.format(cal.time)}"
                1 -> "Tomorrow, ${dateFormatter.format(cal.time)}"
                else -> SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).format(cal.time)
            }
            val waterItem = ScheduleItem(
                title = "Watering",
                plantName = plantName,
                time = wateringTime,
                iconRes = R.drawable.ic_water_drop,
                iconBgColor = PlantifyWaterTealBg,
                iconTint = PlantifyWaterTeal
            )
            val existingGroup = newGroups.find { it.date == dateLabel }
            if (existingGroup != null) {
                val idx = newGroups.indexOf(existingGroup)
                newGroups[idx] = existingGroup.copy(items = existingGroup.items + waterItem)
            } else {
                newGroups.add(ScheduleGroup(dateLabel, listOf(waterItem)))
            }
        }

        val fertCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, fertilizingDays) }
        val fertLabel = SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).format(fertCal.time)
        val fertItem = ScheduleItem(
            title = "Fertilizing",
            plantName = plantName,
            time = "09:00 AM",
            iconRes = R.drawable.ic_bolt,
            iconBgColor = PlantifyFertilizerAmberBg,
            iconTint = PlantifyFertilizerAmber
        )
        val existingFertGroup = newGroups.find { it.date == fertLabel }
        if (existingFertGroup != null) {
            val idx = newGroups.indexOf(existingFertGroup)
            newGroups[idx] = existingFertGroup.copy(items = existingFertGroup.items + fertItem)
        } else {
            newGroups.add(ScheduleGroup(fertLabel, listOf(fertItem)))
        }

        // Urutkan berdasarkan tanggal
        _scheduleGroups.value = newGroups.sortedBy { group ->
            when {
                group.date.startsWith("Today") -> 0
                group.date.startsWith("Tomorrow") -> 1
                else -> {
                    try {
                        SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).parse(group.date)?.time?.toInt() ?: 999
                    } catch (e: Exception) { 999 }
                }
            }
        }
    }

    fun toggleDone(item: ScheduleItem) {
        _scheduleGroups.value = _scheduleGroups.value.map { group ->
            group.copy(items = group.items.map {
                if (it === item) it.copy(isDone = !it.isDone) else it
            })
        }
    }
    private fun extractWateringInterval(text: String): Int {
        val patterns = listOf(
            Regex("siram.*?(\\d+)\\s*hari"),
            Regex("water.*?every\\s*(\\d+)\\s*day"),
            Regex("setiap\\s*(\\d+)\\s*hari")
        )
        for (pattern in patterns) {
            val match = pattern.find(text.lowercase())
            if (match != null) return match.groupValues[1].toIntOrNull() ?: 2
        }
        return 2
    }
    private fun extractFertilizingInterval(text: String): Int {
        val patterns = listOf(
            Regex("pupuk.*?(\\d+)\\s*hari"),
            Regex("fertil.*?every\\s*(\\d+)\\s*day"),
            Regex("pemupukan.*?(\\d+)\\s*hari")
        )
        for (pattern in patterns) {
            val match = pattern.find(text.lowercase())
            if (match != null) return match.groupValues[1].toIntOrNull() ?: 14
        }
        return 14
    }
    private fun extractTime(text: String): String? {
        val pattern = Regex("(\\d{1,2})[:.]?(\\d{2})\\s*(am|pm|pagi|sore)?", RegexOption.IGNORE_CASE)
        val match = pattern.find(text) ?: return null
        val hour = match.groupValues[1].toIntOrNull() ?: return null
        val minute = match.groupValues[2]
        val period = if (hour < 12) "AM" else "PM"
        return String.format("%02d:%s %s", hour, minute, period)
    }
}