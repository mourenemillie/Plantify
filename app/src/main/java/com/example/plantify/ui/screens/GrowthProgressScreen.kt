package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.theme.PlantifyLightGray

data class GrowthPlant(
    val name: String,
    val emoji: String,
    val currentDay: Int,
    val totalDays: Int,
    val stages: List<String>,
    val currentStageIndex: Int,
    val estimatedHarvest: String
)

@Composable
fun GrowthProgressScreen(onBackClick: () -> Unit = {}) {
    val plants = listOf(
        GrowthPlant(
            "Cherry Tomato", "🍅", 15, 70,
            listOf("Seed", "Sprout", "Veg", "Flower", "Fruit"), 2,
            "June 15, 2026"
        ),
        GrowthPlant(
            "Red Chili", "🌶️", 22, 85,
            listOf("Seed", "Sprout", "Veg", "Flower", "Fruit"), 2,
            "July 2, 2026"
        ),
        GrowthPlant(
            "Spinach", "🥬", 8, 45,
            listOf("Seed", "Sprout", "Veg", "Harvest"), 1,
            "June 4, 2026"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PlantifyMediumGreen)
                .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Growth Progress",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(plants) { plant ->
                GrowthCard(plant)
            }
        }
    }
}

@Composable
fun GrowthCard(plant: GrowthPlant) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = plant.emoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = plant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Text(
                    text = "Day ${plant.currentDay} / ${plant.totalDays}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline
            GrowthTimeline(plant.stages, plant.currentStageIndex)

            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress Bar
            val progress = plant.currentDay.toFloat() / plant.totalDays.toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PlantifyMediumGreen,
                trackColor = Color.LightGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PlantifyLightGray)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Estimated harvest", color = Color.Gray, fontSize = 14.sp)
                    Text(text = "~${plant.estimatedHarvest}", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PlantifyMediumGreen),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlantifyMediumGreen)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log today's growth note", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun GrowthTimeline(stages: List<String>, currentIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, stage ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (index <= currentIndex) PlantifyMediumGreen else Color.Gray)
                        .padding(2.dp)
                ) {
                    if (index == currentIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stage,
                    fontSize = 10.sp,
                    color = if (index <= currentIndex) Color.Black else Color.Gray
                )
            }
            if (index < stages.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 14.dp),
                    color = Color.LightGray,
                    thickness = 1.dp
                )
            }
        }
    }
}
