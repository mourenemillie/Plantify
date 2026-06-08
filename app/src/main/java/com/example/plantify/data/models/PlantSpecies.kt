package com.example.plantify.data.models

import androidx.compose.ui.graphics.Color

data class PlantSpecies(
    val id: String,
    val name: String,
    val scientificName: String = "",
    val difficulty: String,
    val difficultyColor: Color,
    val duration: String,
    val wateringInstructions: String,
    val emoji: String = "🌱",
    val description: String = ""
)
