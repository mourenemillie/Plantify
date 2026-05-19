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
import com.example.plantify.data.ScheduleGroup
import com.example.plantify.data.ScheduleItem
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.viewmodel.ScheduleViewModel

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = viewModel()) {
    val scheduleGroups by viewModel.scheduleGroups.collectAsState()

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
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(scheduleGroups) { group ->
                ScheduleSection(group, onToggle = { item ->
                    viewModel.toggleDone(item)
                })
            }
        }
    }
}

@Composable
fun ScheduleSection(group: ScheduleGroup, onToggle: (ScheduleItem) -> Unit) {
    Column {
        Text(
            text = group.date,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        group.items.forEach { item ->
            ScheduleCard(item, onToggle = { onToggle(item) })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ScheduleCard(item: ScheduleItem, onToggle: () -> Unit) {
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
                    .then(
                        if (item.isDone) {
                            Modifier.background(PlantifyMediumGreen, CircleShape)
                        } else {
                            Modifier.border(2.dp, Color.LightGray, CircleShape)
                        }
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (item.isDone) {
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
                    .background(item.iconBgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.iconRes),
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
