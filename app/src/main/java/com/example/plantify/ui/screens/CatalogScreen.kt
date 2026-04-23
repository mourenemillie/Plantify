package com.example.plantify.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CatalogScreen(
    onAddPlantClick: () -> Unit,
    onPlantClick: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onAddPlantClick) {
            Text(text = "Add Plant")
        }
        // Example list item
        Button(onClick = { onPlantClick("Plant ID 1") }) {
            Text(text = "View Plant Detail")
        }
    }
}
