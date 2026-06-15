package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.plantify.ui.theme.PlantifyLightGreen
import com.example.plantify.ui.theme.PlantifyDarkGreen
import com.example.plantify.ui.viewmodel.AddPlantViewModel
import kotlinx.coroutines.launch

@Composable
fun AddPlantScreen(
    viewModel: AddPlantViewModel = viewModel(),
    preSelectedPlantId: Int = 0,
    onSuccess: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(preSelectedPlantId) {
        viewModel.loadCatalog(preSelectedPlantId)
    }

    val selectedPlant by viewModel.selectedPlant.collectAsState()
    val plantingDate by viewModel.plantingDate.collectAsState()
    val locationName by viewModel.locationName.collectAsState()
    val aiRecommendation by viewModel.aiRecommendation.collectAsState()
    val isLoadingAi by viewModel.isLoadingAi.collectAsState()

    val catalog by viewModel.catalog.collectAsState()
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PlantifyMediumGreen)
                .padding(top = 16.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
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
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // // Pilih Tanaman
            FormCard {
                Text("Select Plant", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                LocationDropdown("Plant Type", catalog.map { it.nama_tanaman }, selectedPlant?.nama_tanaman) { name ->
                    catalog.find { it.nama_tanaman == name }?.let { viewModel.selectPlant(it) }
                }
            }

            // // Tanggal Tanam
            FormCard {
                InputField(
                    label = "Planting date",
                    value = plantingDate,
                    onValueChange = { },
                    placeholder = "DD/MM/YYYY",
                    readOnly = true
                )
            }

            // // Lokasi Wilayah Permanen Pot
            FormCard {
                InputField(
                    label = "Location / pot name",
                    value = locationName,
                    onValueChange = { viewModel.updateLocationName(it) },
                    placeholder = "e.g. Balcony pot #1"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Location for Weather", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))

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
            }

            // // AI Smart Advisor Card
            Card(
                colors = CardDefaults.cardColors(containerColor = PlantifyLightGreen.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlantifyMediumGreen)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            text = "Smart Care Advisor (AI)",
                            fontWeight = FontWeight.Bold,
                            color = PlantifyDarkGreen,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoadingAi) {
                            Text("AI sedang membuat jadwal perawatan...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        } else if (aiRecommendation.isNotEmpty()) {
                            Text(text = aiRecommendation, fontSize = 13.sp, color = Color.Black, lineHeight = 18.sp)
                        } else {
                            Text(
                                text = "Pilih tanaman dan lokasi di atas, lalu AI akan otomatis memikirkan jadwal perawatan yang pas untukmu.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // // Tombol Simpan Aplikasi
            Button(
                onClick = { viewModel.savePlantWithAiSchedule { showSuccessDialog = true } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoadingAi && selectedPlant != null && selectedVillage != null
            ) {
                Text(text = "Use AI Schedule & Save", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    viewModel.savePlantManually(locationName.ifBlank { selectedPlant?.nama_tanaman ?: "Tanaman" }) {
                        showSuccessDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PlantifyMediumGreen),
                enabled = selectedPlant != null
            ) {
                Text(text = "Save Plant Manually", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showSuccessDialog) {
        SuccessDialog(
            plantName = selectedPlant?.nama_tanaman ?: "Tanaman",
            onDismiss = {
                showSuccessDialog = false
                onSuccess()
            }
        )
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
fun LocationDropdown(label: String, options: List<String>, selected: String?, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = selected ?: "Select $label",
                    color = if (selected == null) Color.Gray else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = PlantifyMediumGreen)
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
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    Column(modifier = modifier) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = Color.Gray, fontSize = 13.sp) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = PlantifyMediumGreen,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            readOnly = readOnly,
            singleLine = true
        )
    }
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