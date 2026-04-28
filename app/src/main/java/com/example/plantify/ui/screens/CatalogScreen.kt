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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.theme.PlantifyTextGray

data class PlantCategory(
    val name: String,
    val difficulty: String,
    val difficultyColor: Color,
    val duration: String,
    val watering: String,
    val imageRes: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onAddPlantClick: () -> Unit = {},
    onPlantClick: (String) -> Unit = {}
) {
    val plants = listOf(
        PlantCategory("Tomato", "Easy", Color(0xFFE8F5E9), "60-80 days", "Water daily"),
        PlantCategory("Red Chili", "Medium", Color(0xFFFFF3E0), "70-90 days", "Water 2x/day"),
        PlantCategory("Spinach", "Easy", Color(0xFFE8F5E9), "40-50 days", "Water daily"),
        PlantCategory("Mustard Greens", "Easy", Color(0xFFE8F5E9), "30-40 days", "Water daily"),
        PlantCategory("Lettuce", "Easy", Color(0xFFE8F5E9), "45-55 days", "Water 2x/day"),
        PlantCategory("Green Onion", "Very easy", Color(0xFFE8F5E9), "60-80 days", "Water daily"),
        PlantCategory("Bell Pepper", "Medium", Color(0xFFFFF3E0), "70-85 days", "Water 2x/day"),
        PlantCategory("Cucumber", "Easy", Color(0xFFE8F5E9), "50-65 days", "Water daily"),
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlantClick,
                containerColor = PlantifyMediumGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PlantifyMediumGreen)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Plant Catalog",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search plants...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = PlantifyMediumGreen
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Extra padding for FAB
                ) {
                    items(plants) { plant ->
                        PlantItem(plant, onAddPlantClick)
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun PlantItem(plant: PlantCategory, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🌱", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = plant.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = plant.difficultyColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = plant.difficulty,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        color = if (plant.difficulty == "Medium") Color(0xFFE65100) else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = " • ${plant.duration} • ${plant.watering}",
                    fontSize = 12.sp,
                    color = PlantifyTextGray
                )
            }
        }

        IconButton(
            onClick = onAddClick,
            modifier = Modifier
                .size(32.dp)
                .border(1.dp, Color(0xFFB9F1E1), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color(0xFF0D674E),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
