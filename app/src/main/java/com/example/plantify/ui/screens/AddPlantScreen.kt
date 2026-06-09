package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.plantify.ui.theme.PlantifyLightGreen
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.viewmodel.AddPlantViewModel
import com.example.plantify.ui.viewmodel.AiRecommendationState
import com.example.plantify.ui.viewmodel.PlantOption
import kotlinx.coroutines.launch

@Composable
fun AddPlantScreen(
    viewModel: AddPlantViewModel = viewModel(),
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
            .background(Color(0xFFF8F9FA))
    ) {

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

            FormCard {
                Text("Select Plant", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(
                        value = if (selectedPlant != null) "${selectedPlant!!.emoji} ${selectedPlant!!.name} (~${selectedPlant!!.totalDays} days)" else "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Choose a plant...", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PlantifyMediumGreen)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = if (plantError) Color.Red else Color.LightGray,
                            disabledTextColor = Color.Black
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
                                            Text("~${option.totalDays} days to harvest", fontSize = 12.sp, color = Color.Gray)
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
                    Text("Please select a plant", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            FormCard {
                Text("Planting Date", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateField(value = plantingDay, onValueChange = viewModel::updateDay, placeholder = "DD", modifier = Modifier.weight(1f), isError = dateError)
                    DateField(value = plantingMonth, onValueChange = viewModel::updateMonth, placeholder = "MM", modifier = Modifier.weight(1f), isError = dateError)
                    DateField(value = plantingYear, onValueChange = viewModel::updateYear, placeholder = "YYYY", modifier = Modifier.weight(2f), isError = dateError)
                }
                if (dateError) {
                    Text("Please enter a valid date", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }

            FormCard {
                Text("Location / Pot Name", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = viewModel::updateLocation,
                    placeholder = { Text("e.g. Balcony pot #1", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = PlantifyMediumGreen
                    )
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = PlantifyLightGreen.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF0D674E),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Care Advisor (AI)",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D674E),
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (aiState) {
                        is AiRecommendationState.Idle -> {
                            Text(
                                text = "Fill in plant and date above, then tap the button below to get a personalized care schedule from AI.",
                                fontSize = 13.sp,
                                color = Color(0xFF0D674E),
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
                                Text("Generating recommendation...", fontSize = 13.sp, color = Color(0xFF0D674E))
                            }
                        }
                        is AiRecommendationState.Success -> {
                            Text(
                                text = (aiState as AiRecommendationState.Success).recommendation,
                                fontSize = 13.sp,
                                color = Color(0xFF0D674E),
                                lineHeight = 19.sp
                            )
                        }
                        is AiRecommendationState.Error -> {
                            Text(
                                text = (aiState as AiRecommendationState.Error).message,
                                fontSize = 13.sp,
                                color = Color.Red
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tombol Get AI Recommendation
                    OutlinedButton(
                        onClick = { viewModel.getAiRecommendation() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D674E)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0D674E)),
                        enabled = aiState !is AiRecommendationState.Loading
                    ) {
                        Text(
                            text = if (aiState is AiRecommendationState.Success) "🔄 Regenerate" else "✨ Get AI Recommendation",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tombol Use AI Schedule
            Button(
                onClick = { scope.launch { viewModel.savePlant() } },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlantifyMediumGreen,
                    disabledContainerColor = Color(0xFFB0BEC5)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = aiState is AiRecommendationState.Success
            ) {
                Text("Use AI Schedule", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            // Tombol Customize Manually
            OutlinedButton(
                onClick = { scope.launch { viewModel.savePlant() } },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PlantifyMediumGreen),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlantifyMediumGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Customize manually instead", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 13.sp) },
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = if (isError) Color.Red else Color.LightGray,
            focusedBorderColor = PlantifyMediumGreen
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
        title = { Text("Plant Added!", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Text(
                text = "$plantName has been added to your garden. Track its growth in the Growth Progress screen.",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Go to Home")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}