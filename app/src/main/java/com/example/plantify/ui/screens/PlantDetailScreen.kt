package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.viewmodel.PlantDetailViewModel

@Composable
fun PlantDetailScreen(
    plantId: String,
    onBackClick: () -> Unit = {},
    onGrowthProgressClick: () -> Unit = {},
    viewModel: PlantDetailViewModel = viewModel()
) {
    LaunchedEffect(plantId) {
        viewModel.loadPlant(plantId)
    }

    val plant by viewModel.plantDetail.collectAsState()

    if (plant == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PlantifyMediumGreen)
        }
        return
    }

    val p = plant!!
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PlantifyMediumGreen)
                .padding(top = 16.dp, bottom = 24.dp, start = 8.dp, end = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Column {
                    Text(
                        text = "${p.emoji} ${p.name}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = p.category,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Card: Progress
            DetailCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Growth Progress", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = p.difficultyColor
                    ) {
                        Text(
                            text = p.difficulty,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (p.difficulty == "Medium") Color(0xFFE65100) else Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stage timeline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    p.stages.forEachIndexed { index, stage ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        if (index <= p.currentStageIndex) PlantifyMediumGreen else Color(0xFFBDC3C7),
                                        CircleShape
                                    )
                            )
                            Text(stage, fontSize = 9.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                        }
                        if (index < p.stages.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f).padding(bottom = 14.dp),
                                color = if (index < p.currentStageIndex) PlantifyMediumGreen else Color(0xFFE0E0E0),
                                thickness = 2.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { p.progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = PlantifyMediumGreen,
                    trackColor = Color(0xFFE0E0E0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Day ${p.currentDay} of ${p.totalDays}", fontSize = 13.sp, color = Color.Gray)
                    Text("${(p.progress * 100).toInt()}% complete", fontSize = 13.sp, color = PlantifyMediumGreen, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onGrowthProgressClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("View Full Growth Progress", fontWeight = FontWeight.Bold)
                }
            }

            // Card: Deskripsi
            DetailCard {
                Text("About", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(p.description, fontSize = 14.sp, color = Color(0xFF555555), lineHeight = 22.sp)
            }

            // Card: Info Perawatan
            DetailCard {
                Text("Care Guide", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareChip(emoji = "💧", label = "Watering", value = p.wateringFrequency, modifier = Modifier.weight(1f))
                    CareChip(emoji = "☀️", label = "Sunlight", value = p.sunlight, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareChip(emoji = "🌡️", label = "Temperature", value = p.temperature, modifier = Modifier.weight(1f))
                    CareChip(emoji = "🌿", label = "Fertilizing", value = p.fertilizing, modifier = Modifier.weight(1f))
                }
            }

            // Card: Tips
            DetailCard {
                Text("Growing Tips", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                p.tips.forEach { tip ->
                    Row(
                        modifier = Modifier.padding(bottom = 10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PlantifyMediumGreen,
                            modifier = Modifier.size(18.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(tip, fontSize = 14.sp, color = Color(0xFF555555), lineHeight = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DetailCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun CareChip(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(text = "$emoji  $label", fontSize = 11.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
    }
}