package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.R
import com.example.plantify.ui.theme.*

@Composable
fun HomeScreen(onPlantClick: () -> Unit = {}) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
    ) {
        HomeHeader()

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset(y = (-40).dp)
        ) {
            TasksCard()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.my_plants),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A212E)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlantItem(
                name = stringResource(R.string.plant_cherry_tomato),
                days = 15,
                progress = 0.85f,
                nextWatering = stringResource(R.string.today),
                onClick = onPlantClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlantItem(
                name = stringResource(R.string.plant_red_chili),
                days = 22,
                progress = 0.92f,
                nextWatering = stringResource(R.string.tomorrow),
                onClick = onPlantClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlantItem(
                name = stringResource(R.string.plant_spinach),
                days = 8,
                progress = 0.45f,
                nextWatering = stringResource(R.string.today),
                onClick = onPlantClick
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                color = PlantifyMediumGreen,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.greeting),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(R.string.my_garden),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = stringResource(R.string.plant_count),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Weather Widget
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.wrapContentWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wb_sunny),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.weather_sunny),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun TasksCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.todays_tasks),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A212E)
                )
                Text(
                    text = stringResource(R.string.tasks_pending),
                    color = Color(0xFF50D67F),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TaskItem(
                iconRes = R.drawable.ic_water_drop,
                iconBgColor = Color(0xFFE0F2F1),
                iconTint = Color(0xFF009688),
                title = stringResource(R.string.task_watering),
                subtitle = "Cherry Tomato",
                time = "08:00 AM"
            )

            Spacer(modifier = Modifier.height(12.dp))

            TaskItem(
                iconRes = R.drawable.ic_water_drop,
                iconBgColor = Color(0xFFE0F2F1),
                iconTint = Color(0xFF009688),
                title = stringResource(R.string.task_watering),
                subtitle = "Spinach",
                time = "08:00 AM"
            )

            Spacer(modifier = Modifier.height(12.dp))

            TaskItem(
                iconRes = R.drawable.ic_bolt,
                iconBgColor = Color(0xFFE0F7FA),
                iconTint = Color(0xFF00BCD4),
                title = stringResource(R.string.task_fertilizing),
                subtitle = "Red Chili",
                time = "09:00 AM"
            )
        }
    }
}

@Composable
private fun TaskItem(
    iconRes: Int,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    time: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F8E9).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = iconTint
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A212E))
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }

        Text(text = time, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PlantItem(
    name: String,
    days: Int,
    progress: Float,
    nextWatering: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_book),
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A212E))
                    Text(text = stringResource(R.string.days_grown, days), fontSize = 12.sp, color = Color.Gray)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = stringResource(R.string.next_watering), fontSize = 10.sp, color = Color.Gray)
                    Text(
                        text = nextWatering,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = Color(0xFF1F6F5F),
                    trackColor = Color(0xFFEEEEEE),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    PlantifyTheme {
        HomeScreen()
    }
}
