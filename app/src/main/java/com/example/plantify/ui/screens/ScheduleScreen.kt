package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.ui.theme.*

data class ScheduleItem(
    val title: String,
    val plantName: String,
    val time: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTint: Color,
    val isDone: Boolean = false
)

data class ScheduleGroup(
    val date: String,
    val items: List<ScheduleItem>
)

@Composable
fun ScheduleScreen() {
    val scheduleGroups = listOf(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = "Care Schedule",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(24.dp),
            color = Color(0xFF001F3F)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 0.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(scheduleGroups) { group ->
                ScheduleSection(group)
            }
        }
    }
}

@Composable
fun ScheduleSection(group: ScheduleGroup) {
    Column {
        Text(
            text = group.date,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        group.items.forEach { item ->
            ScheduleCard(item)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ScheduleCard(item: ScheduleItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (item.isDone) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Green)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(item.iconBgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Text(
                    text = item.plantName,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = item.time,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
