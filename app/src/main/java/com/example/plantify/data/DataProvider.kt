package com.example.plantify.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import com.example.plantify.data.models.Plant
import com.example.plantify.data.models.PlantSpecies
import com.example.plantify.data.models.ScheduleItem
import com.example.plantify.data.models.ScheduleGroup
import com.example.plantify.data.models.Notification

object DataProvider {
    val plantSpeciesList = mutableStateListOf(
        PlantSpecies("1", "Tomato", "Solanum lycopersicum", "Easy", Color(0xFFE8F5E9), "60-80 days", "Water daily", "🍅"),
        PlantSpecies("2", "Red Chili", "Capsicum annuum", "Medium", Color(0xFFFFF3E0), "70-90 days", "Water 2x/day", "🌶️"),
        PlantSpecies("3", "Spinach", "Spinacia oleracea", "Easy", Color(0xFFE8F5E9), "40-50 days", "Water daily", "🥬"),
        PlantSpecies("4", "Mustard Greens", "Brassica juncea", "Easy", Color(0xFFE8F5E9), "30-40 days", "Water daily", "🥗"),
        PlantSpecies("5", "Lettuce", "Lactuca sativa", "Easy", Color(0xFFE8F5E9), "45-55 days", "Water 2x/day", "🥬"),
        PlantSpecies("6", "Green Onion", "Allium fistulosum", "Very easy", Color(0xFFE8F5E9), "60-80 days", "Water daily", "🧅"),
        PlantSpecies("7", "Bell Pepper", "Capsicum annuum", "Medium", Color(0xFFFFF3E0), "70-85 days", "Water 2x/day", "🫑"),
        PlantSpecies("8", "Cucumber", "Cucumis sativus", "Easy", Color(0xFFE8F5E9), "50-65 days", "Water daily", "🥒")
    )

    val myPlants = mutableStateListOf(
        Plant(
            id = "p1",
            speciesId = "1",
            name = "Cherry Tomato",
            datePlanted = "2026-05-20",
            currentDay = 15,
            totalDays = 70,
            progress = 0.21f,
            nextWatering = "Today",
            stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit"),
            currentStageIndex = 2,
            estimateHarvestDate = "June 15, 2026",
            emoji = "🍅"
        ),
        Plant(
            id = "p2",
            speciesId = "2",
            name = "Red Chili",
            datePlanted = "2026-05-13",
            currentDay = 22,
            totalDays = 85,
            progress = 0.26f,
            nextWatering = "Tomorrow",
            stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit"),
            currentStageIndex = 2,
            estimateHarvestDate = "July 2, 2026",
            emoji = "🌶️"
        ),
        Plant(
            id = "p3",
            speciesId = "3",
            name = "Spinach",
            datePlanted = "2026-05-27",
            currentDay = 8,
            totalDays = 45,
            progress = 0.18f,
            nextWatering = "Today",
            stages = listOf("Seed", "Sprout", "Veg", "Harvest"),
            currentStageIndex = 1,
            estimateHarvestDate = "June 4, 2026",
            emoji = "🥬"
        )
    )

    val scheduleGroups = mutableStateListOf(
        ScheduleGroup(
            "Today",
            mutableStateListOf(
                ScheduleItem("s1", "Watering", "Cherry Tomato", "08:00 AM", null, null, Color(0xFF009688), Color(0xFFE0F2F1)),
                ScheduleItem("s2", "Watering", "Spinach", "08:00 AM", null, null, Color(0xFF009688), Color(0xFFE0F2F1)),
                ScheduleItem("s3", "Fertilizing", "Red Chili", "09:00 AM", null, null, Color(0xFF00BCD4), Color(0xFFE0F7FA))
            )
        )
    )

    val notifications = mutableStateListOf(
        Notification("n1", "Time to water", "Cherry Tomato & Spinach", "2 min ago", Icons.Default.Notifications, null, Color(0xFFE3F2FD), true),
        Notification("n2", "Fertilizing reminder", "Red Chili needs it today", "1 hour ago", Icons.Default.ArrowForward, null, Color(0xFFE8F5E9), true)
    )

    // CRUD Plants
    fun addPlant(plant: Plant) {
        myPlants.add(plant)
    }

    fun deletePlant(plantId: String) {
        val plant = myPlants.find { it.id == plantId }
        val plantName = plant?.name
        myPlants.removeIf { it.id == plantId }
        // Clean up schedules for this plant name
        scheduleGroups.forEach { group ->
            (group.items as? MutableList)?.removeIf { it.plantName == plantName }
        }
    }

    // CRUD Catalog
    fun addPlantSpecies(species: PlantSpecies) {
        plantSpeciesList.add(species)
    }

    fun deletePlantSpecies(speciesId: String) {
        plantSpeciesList.removeIf { it.id == speciesId }
    }

    // CRUD Schedules
    fun addScheduleItem(date: String, item: ScheduleItem) {
        val group = scheduleGroups.find { it.date == date }
        if (group != null) {
            (group.items as? MutableList)?.add(item)
        } else {
            scheduleGroups.add(ScheduleGroup(date, mutableStateListOf(item)))
        }
    }

    fun toggleScheduleItemDone(itemId: String) {
        scheduleGroups.forEach { group ->
            val index = group.items.indexOfFirst { it.id == itemId }
            if (index != -1) {
                val item = group.items[index]
                (group.items as MutableList)[index] = item.copy(isDone = !item.isDone)
            }
        }
    }

    fun deleteScheduleItem(itemId: String) {
        scheduleGroups.forEach { group ->
            (group.items as? MutableList)?.removeIf { it.id == itemId }
        }
    }

    // Notifications
    fun markAllNotificationsRead() {
        val updated = notifications.map { it.copy(isUnread = false) }
        notifications.clear()
        notifications.addAll(updated)
    }
}
