package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GrowthProgressScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF136A4F))
                .padding(vertical = 20.dp, horizontal = 24.dp)
        ) {
            Text(
                text = "Growth Progress",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
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
                    progress = 0.3f
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
                    progress = 0.4f
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
                    progress = 0.25f
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
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$plantEmoji $plantName", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Day $currentDay / $totalDays", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
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
                        Divider(
                            modifier = Modifier.width(30.dp).padding(bottom = 14.dp),
                            color = if (index < currentStageIndex) Color(0xFF27AE60) else Color(0xFFBDC3C7),
                            thickness = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFE0E0E0), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(Color(0xFF27AE60), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* Action */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF136A4F)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF136A4F))
            ) {
                Text(text = "+ Log today's growth note", fontWeight = FontWeight.Bold)
            }
        }
    }
}