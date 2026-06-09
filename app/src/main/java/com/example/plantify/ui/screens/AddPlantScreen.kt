package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.plantify.ui.theme.PlantifyLightGreen
import com.example.plantify.ui.theme.PlantifyDarkGreen
import com.example.plantify.ui.viewmodel.AddPlantViewModel

@Composable
fun AddPlantScreen(
    viewModel: AddPlantViewModel = viewModel(),
    preSelectedPlantId: Int = 0,
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {},
    onCustomizeManually: () -> Unit = {}
) {
    LaunchedEffect(preSelectedPlantId) {
        viewModel.loadCatalog(preSelectedPlantId)
    }

    val selectedPlant by viewModel.selectedPlant.collectAsState()
    val plantingDate by viewModel.plantingDate.collectAsState()
    val locationName by viewModel.locationName.collectAsState()
    val aiRecommendation by viewModel.aiRecommendation.collectAsState()
    val isLoadingAi by viewModel.isLoadingAi.collectAsState()

    val provinces by viewModel.provinces.collectAsState()
    val selectedProvince by viewModel.selectedProvince.collectAsState()
    val regencies by viewModel.regencies.collectAsState()
    val selectedRegency by viewModel.selectedRegency.collectAsState()
    val districts by viewModel.districts.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val villages by viewModel.villages.collectAsState()
    val selectedVillage by viewModel.selectedVillage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PlantifyMediumGreen)
                .padding(24.dp)
        ) {
            Text(
                text = "Step 1 of 2",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add New Plant",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            val catalog by viewModel.catalog.collectAsState()
            
            Text(text = "Select plant", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            
            LocationDropdown("Plant Type", catalog.map { it.nama_tanaman }, selectedPlant?.nama_tanaman) { name ->
                catalog.find { it.nama_tanaman == name }?.let { viewModel.selectPlant(it) }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InputField(label = "Planting date", value = plantingDate)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InputField(
                label = "Location / pot name",
                value = locationName,
                onValueChange = { viewModel.updateLocationName(it) },
                placeholder = "e.g. Balcony pot #1",
                readOnly = false
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Location for Weather", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Basic Dropdowns (simplified representation)
            LocationDropdown("Province", provinces.map { it.name }, selectedProvince?.name) { name ->
                provinces.find { it.name == name }?.let { viewModel.selectProvince(it) }
            }
            LocationDropdown("City/Regency", regencies.map { it.name }, selectedRegency?.name) { name ->
                regencies.find { it.name == name }?.let { viewModel.selectRegency(it) }
            }
            LocationDropdown("District", districts.map { it.name }, selectedDistrict?.name) { name ->
                districts.find { it.name == name }?.let { viewModel.selectDistrict(it) }
            }
            LocationDropdown("Village", villages.map { it.name }, selectedVillage?.name) { name ->
                villages.find { it.name == name }?.let { viewModel.selectVillage(it) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = PlantifyLightGreen.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlantifyMediumGreen)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    if (isLoadingAi) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PlantifyMediumGreen)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = PlantifyMediumGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Smart Care Advisor",
                            fontWeight = FontWeight.Bold,
                            color = PlantifyDarkGreen,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = aiRecommendation,
                            fontSize = 13.sp,
                            color = Color.Black, // High contrast
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.savePlantWithAiSchedule(onSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoadingAi && selectedPlant != null && selectedVillage != null
            ) {
                Text(text = "Use AI Schedule", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCustomizeManually,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PlantifyMediumGreen)
            ) {
                Text(text = "Customize manually instead", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LocationDropdown(label: String, options: List<String>, selected: String?, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = selected ?: "Select $label", color = if (selected == null) Color.Gray else Color.Black)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    placeholder: String = "",
    readOnly: Boolean = true
) {
    Column {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = PlantifyMediumGreen,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black
            ),
            readOnly = readOnly
        )
    }
}
