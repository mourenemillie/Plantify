package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.viewmodel.AddPlantViewModel
import com.example.plantify.ui.viewmodel.AiRecommendationState
import com.example.plantify.ui.viewmodel.PlantOption
import com.example.plantify.ui.viewmodel.ViewModelFactory
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun AddPlantScreen(
    plantName: String? = null,
    viewModel: AddPlantViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onBackClick: () -> Unit = {},
    onPlantSaved: () -> Unit = {}
) {
    val selectedPlant by viewModel.selectedPlant.collectAsState()
    val plantingDay by viewModel.plantingDay.collectAsState()
    val plantingMonth by viewModel.plantingMonth.collectAsState()
    val plantingYear by viewModel.plantingYear.collectAsState()
    val location by viewModel.location.collectAsState()
    val plantError by viewModel.plantError.collectAsState()
    val dateError by viewModel.dateError.collectAsState()
    val aiState by viewModel.aiState.collectAsState()

    val scope = rememberCoroutineScope()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showPlantDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(plantName) {
        if (!plantName.isNullOrEmpty()) {
            val option = viewModel.plantOptions.find { it.name.equals(plantName, ignoreCase = true) }
            if (option != null) {
                viewModel.selectPlant(option)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.savedEvent.collect {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        SuccessDialog(
            plantName = selectedPlant?.name ?: "",
            onDismiss = {
                showSuccessDialog = false
                onPlantSaved()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header bar
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
                    Text("Step 1 of 1", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    Text("Add New Plant", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Pilih Tanaman ──
            FormCard {
                Text("Select Plant", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(
                        value = if (selectedPlant != null) "${selectedPlant!!.emoji} ${selectedPlant!!.name} (~${selectedPlant!!.totalDays} days)" else "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Choose a plant...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PlantifyMediumGreen)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = if (plantError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        enabled = false
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showPlantDropdown = true })
                    DropdownMenu(
                        expanded = showPlantDropdown,
                        onDismissRequest = { showPlantDropdown = false }
                    ) {
                        viewModel.plantOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(option.emoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(option.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                            Text("~${option.totalDays} days to harvest", fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        }
                                    }
                                },
                                onClick = {
                                    viewModel.selectPlant(option)
                                    showPlantDropdown = false
                                }
                            )
                        }
                    }
                }
                if (plantError) {
                    Text("Please select a plant", color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // ── Tanggal Tanam ──
            FormCard {
                Text("Planting Date", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateField(value = plantingDay, onValueChange = viewModel::updateDay,
                        placeholder = "DD", modifier = Modifier.weight(1f), isError = dateError)
                    DateField(value = plantingMonth, onValueChange = viewModel::updateMonth,
                        placeholder = "MM", modifier = Modifier.weight(1f), isError = dateError)
                    DateField(value = plantingYear, onValueChange = viewModel::updateYear,
                        placeholder = "YYYY", modifier = Modifier.weight(2f), isError = dateError)
                }
                if (dateError) {
                    Text("Please enter a valid date", color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // ── Lokasi ──
            FormCard {
                Text("Location / Pot Name", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = viewModel::updateLocation,
                    placeholder = { Text("e.g. Balcony pot #1",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = PlantifyMediumGreen,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            // ── AI Smart Advisor Card ──
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = PlantifyMediumGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Care Advisor (AI)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (aiState) {
                        is AiRecommendationState.Idle -> {
                            Text(
                                text = "Pilih tanaman dan isi tanggal di atas, lalu AI akan otomatis memberikan jadwal perawatan untukmu.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )
                        }
                        is AiRecommendationState.Loading -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = PlantifyMediumGreen,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("AI sedang membuat jadwal perawatan...",
                                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        is AiRecommendationState.Success -> {
                            Surface(
                                color = PlantifyMediumGreen.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = (aiState as AiRecommendationState.Success).recommendation,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 19.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        is AiRecommendationState.Error -> {
                            Text(
                                text = (aiState as AiRecommendationState.Error).message,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.getAiRecommendation() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PlantifyMediumGreen
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PlantifyMediumGreen),
                        enabled = aiState !is AiRecommendationState.Loading
                    ) {
                        Text(
                            text = if (aiState is AiRecommendationState.Success) "🔄 Regenerate AI" else "✨ Get AI Recommendation",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Tombol Simpan ──
            Button(
                onClick = { scope.launch { viewModel.savePlant() } },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (aiState is AiRecommendationState.Success) "✅ Save with AI Schedule" else "💾 Save Plant",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun DateField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 13.sp) },
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else PlantifyMediumGreen,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
private fun SuccessDialog(plantName: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = PlantifyMediumGreen,
                modifier = Modifier.size(48.dp)
            )
        },
        title = { Text("Plant Added! 🌱", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Text(
                text = "$plantName has been added to your garden.\nCek Schedule dan Growth Progress untuk melihat jadwal perawatannya!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Ke Home", color = Color.White)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}