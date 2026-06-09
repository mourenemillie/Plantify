package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantify.R
import com.example.plantify.data.local.entity.TaskScheduleEntity
import com.example.plantify.ui.theme.*
import com.example.plantify.ui.viewmodel.ScheduleViewModel

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = viewModel()) {
    val schedules by viewModel.allSchedules.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header sama kayak screen lain
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PlantifyMediumGreen)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Care Schedule",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(schedules) { task ->
                ScheduleCard(task, onToggle = {
                    viewModel.toggleDone(task)
                })
            }
        }
    }
}

@Composable
fun ScheduleCard(item: TaskScheduleEntity, onToggle: () -> Unit) {
    val isDone = item.status_tugas == "Done"
    val iconRes = when (item.jenis_tugas) {
        "Watering" -> R.drawable.ic_water_drop
        "Fertilizing" -> R.drawable.ic_bolt
        else -> R.drawable.ic_book
    }
    val iconTint = when (item.jenis_tugas) {
        "Watering" -> PlantifyWaterTeal
        "Fertilizing" -> PlantifyFertilizerAmber
        else -> PlantifyIconGreen
    }
    val iconBg = when (item.jenis_tugas) {
        "Watering" -> PlantifyWaterTealBg
        "Fertilizing" -> PlantifyFertilizerAmberBg
        else -> Color(0xFFE8F5E9)
    }

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
            // Clickable circle checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (isDone) PlantifyMediumGreen else Color.Transparent, CircleShape)
                    .border(if (isDone) 0.dp else 2.dp, if (isDone) Color.Transparent else Color.LightGray, CircleShape)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.jenis_tugas,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Text(
                    text = "Plant ID: ${item.id_kebun}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = item.waktu_eksekusi,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
