package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.theme.PlantifyTextGray
import com.example.plantify.ui.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = viewModel(),
    onAddNewTypeClick: () -> Unit = {}, // For the FAB
    onAddPlantClick: (Int) -> Unit = {}, // For the card plus button (passes ID)
    onPlantClick: (String) -> Unit = {}
) {
    val catalog by viewModel.catalog.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewTypeClick,
                containerColor = PlantifyMediumGreen,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Plant Type")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = PlantifyMediumGreen,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 40.dp)
            ) {
                Column {
                    Text(
                        text = "Discover",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Plant Catalog",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Modern Search Bar
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search plants...", color = Color.Gray, fontSize = 15.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PlantifyMediumGreen) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = PlantifyMediumGreen
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(catalog) { plant ->
                        PlantItem(plant, onAddClick = { onAddPlantClick(plant.id_tanaman) })
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun PlantItem(plant: PlantCatalogEntity, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = plant.emoji_icon ?: "🌱", fontSize = 24.sp)
        }

            Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = plant.nama_tanaman,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                val difficultyColor = when (plant.difficulty) {
                    "Easy" -> Color(0xFFE8F5E9)
                    "Medium" -> Color(0xFFFFF3E0)
                    else -> Color(0xFFF5F5F5)
                }
                Surface(
                    color = difficultyColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = plant.difficulty ?: "Easy",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        color = if (plant.difficulty == "Medium") Color(0xFFE65100) else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = " • ${plant.durasi_panen} days • Siram ${plant.interval_siram} hari",
                    fontSize = 12.sp,
                    color = PlantifyTextGray
                )
            }

            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(PlantifyMediumGreen, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
