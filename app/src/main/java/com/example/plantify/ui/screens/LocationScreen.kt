package com.example.plantify.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.viewmodel.LocationViewModel
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.material.icons.filled.Edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    plantName: String,
    idKebun: Int,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var mapLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var mapInitialized by remember { mutableStateOf(false) }

    val weatherCondition by viewModel.weatherCondition.collectAsState()
    val locationText by viewModel.locationText.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showEditLocationDialog by remember { mutableStateOf(false) }
    var editLocationInput by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || 
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || 
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getCurrentLocation(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 
                    null
                ).addOnSuccessListener { loc ->
                    if (loc != null) {
                        val lat = loc.latitude
                        val lon = loc.longitude
                        mapLocation = GeoPoint(lat, lon)
                        viewModel.fetchLocationAndWeather(lat, lon)
                    } else {
                        // Fallback to Jakarta if loc is null
                        mapLocation = GeoPoint(-6.2088, 106.8456)
                        viewModel.fetchLocationAndWeather(-6.2088, 106.8456)
                    }
                }
            }
        } else {
            // Fallback to Jakarta if denied
            mapLocation = GeoPoint(-6.2088, 106.8456)
            viewModel.fetchLocationAndWeather(-6.2088, 106.8456)
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        mapInitialized = true
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detecting Location", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PlantifyMediumGreen)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "We're locating your plant...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A212E)
            )
            
            Text(
                text = "Your location is mapped automatically to retrieve local weather data from BMKG.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            if (mapInitialized && mapLocation != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.LightGray, RoundedCornerShape(16.dp))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            MapView(ctx).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(16.0)
                                val point = mapLocation!!
                                controller.setCenter(point)
                                
                                val marker = Marker(this)
                                marker.position = point
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                marker.title = "Your Location"
                                overlays.add(marker)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PlantifyMediumGreen)
                }
            }

            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Weather",
                        tint = PlantifyMediumGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(locationText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PlantifyMediumGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { 
                                    editLocationInput = locationText
                                    showEditLocationDialog = true 
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Location", tint = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        if (isLoading) {
                            Text("Fetching weather from BMKG...", fontSize = 14.sp, color = Color.Gray)
                        } else {
                            Text(weatherCondition ?: "Unknown weather", fontSize = 14.sp, color = Color.DarkGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    viewModel.generateScheduleForPlant(plantName, idKebun)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), 
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        strokeWidth = 2.dp
                    )
                } else {
                    val textColor = Color.White
                    Text("✨ Generate AI Schedule", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                }
            }
        }
    }

    val schedulePreview by viewModel.schedulePreview.collectAsState()
    if (schedulePreview != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPreview() },
            title = { Text("AI Schedule Preview", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Based on your location's weather, here is the suggested schedule:")
                    Spacer(modifier = Modifier.height(8.dp))
                    schedulePreview!!.forEach { task ->
                        Text("• ${task.jenis_tugas} at ${task.waktu_eksekusi}")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.savePreviewedSchedule()
                    onNavigateBack()
                }, colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen)) {
                    Text("Confirm & Save", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissPreview() }) {
                    Text("Cancel", color = PlantifyMediumGreen)
                }
            }
        )
    }

    if (showEditLocationDialog) {
        AlertDialog(
            onDismissRequest = { showEditLocationDialog = false },
            title = { Text("Edit Location", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your actual district or village name manually.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editLocationInput,
                        onValueChange = { editLocationInput = it },
                        label = { Text("Location Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateLocationManually(editLocationInput)
                        showEditLocationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditLocationDialog = false }) {
                    Text("Cancel", color = PlantifyMediumGreen)
                }
            }
        )
    }
}
