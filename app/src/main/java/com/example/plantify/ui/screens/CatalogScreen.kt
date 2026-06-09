package com.example.plantify.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantify.data.PlantCategory
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.theme.PlantifyTextGray
import com.example.plantify.ui.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = viewModel(),
    onAddPlantClick: () -> Unit = {},
    onPlantClick: (String) -> Unit = {}
) {
    val plants by viewModel.plants.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlantClick,
                containerColor = PlantifyMediumGreen,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
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
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
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

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search plants...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = PlantifyMediumGreen,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(plants) { plant ->
                        CatalogPlantItem(plant, onAddPlantClick)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogPlantItem(plant: PlantCategory, onAddClick: () -> Unit) {
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
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Image Container with soft background
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F8E9)),
                contentAlignment = Alignment.Center
            ) {
                if (plant.imageRes != 0) {
                    Image(
                        painter = painterResource(id = plant.imageRes),
                        contentDescription = plant.name,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Text(text = "🌱", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = plant.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = plant.difficultyColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${plant.duration} • ${plant.watering}",
                        fontSize = 12.sp,
                        color = PlantifyTextGray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = " • ${plant.duration} • ${plant.watering}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
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
                tint = PlantifyMediumGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
