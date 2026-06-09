package com.example.plantify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantify.data.GrowthNote
import com.example.plantify.data.GrowthProgressItem
import com.example.plantify.ui.theme.PlantifyMediumGreen
import com.example.plantify.ui.viewmodel.GrowthProgressViewModel
import com.example.plantify.ui.viewmodel.ViewModelFactory
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthProgressScreen(
    viewModel: GrowthProgressViewModel = viewModel(factory = ViewModelFactory(LocalContext.current)),
    onBackClick: () -> Unit = {}
) {
    val growthItems by viewModel.growthItems.collectAsState()
    val notes by viewModel.notes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PlantifyMediumGreen)
                .padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Growth Progress",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(growthItems) { item ->
                val plantNotes = notes[item.plantName] ?: emptyList()
                GrowthCard(
                    item = item,
                    notes = plantNotes,
                    onSaveNote = { noteText ->
                        viewModel.addNote(item.plantName, noteText)
                    }
                )
            }
        }
    }
}

@Composable
fun GrowthCard(
    item: GrowthProgressItem,
    notes: List<GrowthNote>,
    onSaveNote: (String) -> Unit
) {
    // State untuk dialog
    var showDialog by remember { mutableStateOf(false) }

    // Tampilkan dialog kalau showDialog = true
    if (showDialog) {
        LogNoteDialog(
            plantName = item.plantName,
            onDismiss = { showDialog = false },
            onSave = { noteText ->
                onSaveNote(noteText)
                showDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Nama tanaman + hari
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.plantEmoji} ${item.plantName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Day ${item.currentDay} / ${item.totalDays}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Timeline stage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.stages.forEachIndexed { index, stage ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    if (index <= item.currentStageIndex) Color(0xFF27AE60) else Color(0xFFBDC3C7),
                                    CircleShape
                                )
                        ) {
                            if (index == item.currentStageIndex) {
                                Box(modifier = Modifier.fillMaxSize().background(Color.White, CircleShape))
                            }
                        }
                        Text(
                            text = stage,
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (index < item.stages.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f).padding(bottom = 14.dp),
                            color = if (index < item.currentStageIndex) Color(0xFF27AE60) else Color(0xFFBDC3C7),
                            thickness = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { item.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF27AE60),
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estimasi panen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Estimated harvest", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "~${item.estimateDate}", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PlantifyMediumGreen),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlantifyMediumGreen)
            ) {
                Text(text = "+ Log today's growth note", fontWeight = FontWeight.Bold)
            }

            // Tampilkan daftar catatan
            if (notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Growth Notes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(8.dp))
                notes.forEach { note ->
                    NoteItem(note = note)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: GrowthNote) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FBF9), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = PlantifyMediumGreen,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = note.note, fontSize = 13.sp, color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = note.date, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun LogNoteDialog(
    plantName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Growth Note",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = plantName,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("e.g. Daun mulai kuning, tinggi 15cm...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PlantifyMediumGreen,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onSave(text) },
                colors = ButtonDefaults.buttonColors(containerColor = PlantifyMediumGreen),
                shape = RoundedCornerShape(8.dp),
                enabled = text.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}