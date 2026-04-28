package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.ui.theme.PlantifyMediumGreen

@Composable
fun GrowthProgressScreen(onBackClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header dengan tombol back agar navigasi work
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PlantifyMediumGreen)
                .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Growth Progress",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GrowthCard(
                    plantEmoji = "🍅",
                    plantName = "Cherry Tomato",
                    currentDay = 15,
                    totalDays = 70,
                    stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit"),
                    currentStageIndex = 2,
                    estimateDate = "June 15, 2026",
                    progress = 0.21f
                )
            }
            item {
                GrowthCard(
                    plantEmoji = "🌶️",
                    plantName = "Red Chili",
                    currentDay = 22,
                    totalDays = 85,
                    stages = listOf("Seed", "Sprout", "Veg", "Flower", "Fruit"),
                    currentStageIndex = 2,
                    estimateDate = "July 2, 2026",
                    progress = 0.26f
                )
            }
            item {
                GrowthCard(
                    plantEmoji = "🥬",
                    plantName = "Spinach",
                    currentDay = 8,
                    totalDays = 45,
                    stages = listOf("Seed", "Sprout", "Veg", "Harvest"),
                    currentStageIndex = 1,
                    estimateDate = "June 4, 2026",
                    progress = 0.18f
                )
            }
        }
    }
}

@Composable
fun GrowthCard(
    plantEmoji: String,
    plantName: String,
    currentDay: Int,
    totalDays: Int,
    stages: List<String>,
    currentStageIndex: Int,
    estimateDate: String,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$plantEmoji $plantName", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Day $currentDay / $totalDays", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            // Timeline (Sesuai Gambar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                stages.forEachIndexed { index, stage ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    if (index <= currentStageIndex) Color(0xFF27AE60) else Color(0xFFBDC3C7),
                                    CircleShape
                                )
                                .padding(2.dp)
                        ) {
                            if (index == currentStageIndex) {
                                Box(modifier = Modifier.fillMaxSize().background(Color.White, CircleShape))
                            }
                        }
                        Text(text = stage, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    }
                    if (index < stages.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f).padding(bottom = 14.dp),
                            color = if (index < currentStageIndex) Color(0xFF27AE60) else Color(0xFFBDC3C7),
                            thickness = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF27AE60),
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Box Estimasi Panen (Tadinya hilang di merge kawan Anda)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Estimated harvest", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "~$estimateDate", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /* Action */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PlantifyMediumGreen),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlantifyMediumGreen)
            ) {
                Text(text = "+ Log today's growth note", fontWeight = FontWeight.Bold)
            }
        }
    }
}
