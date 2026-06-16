package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantify.R
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.theme.PlantifyTextGray
import com.example.plantify.ui.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = viewModel(),
    onAddNewTypeClick: () -> Unit = {},
    onAddPlantClick: (Int) -> Unit = {},
    onPlantClick: (String) -> Unit = {}
) {
    val catalog by viewModel.catalog.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        color = PlantifyMediumGreen,
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.discover),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(R.string.plant_catalog),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-40).dp)
            ) {
                // Floating search bar
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                stringResource(R.string.search_plants),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = PlantifyMediumGreen)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = PlantifyMediumGreen,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Empty state: shown when a search yields no matches.
                if (catalog.isEmpty() && searchQuery.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No plants found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try a different search term",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                catalog.forEach { plant ->
                    CatalogPlantItem(
                        plant = plant,
                        onAddClick = { onAddPlantClick(plant.id_tanaman) },
                        onItemClick = { onPlantClick(plant.id_tanaman.toString()) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        FloatingActionButton(
            onClick = onAddNewTypeClick,
            containerColor = PlantifyMediumGreen,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add New Plant Type")
        }
    }
}

@Composable
fun CatalogPlantItem(plant: PlantCatalogEntity, onAddClick: () -> Unit, onItemClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = plant.emoji_icon ?: "🌱", fontSize = 24.sp)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.nama_tanaman,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = " • ${plant.durasi_panen} days • Siram ${plant.interval_siram} hari",
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
                    tint = PlantifyMediumGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}