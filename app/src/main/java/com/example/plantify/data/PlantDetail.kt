package com.example.plantify.data

import androidx.compose.ui.graphics.Color

data class PlantDetail(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String,
    val difficulty: String,
    val difficultyColor: Color,
    val description: String,
    val wateringFrequency: String,
    val sunlight: String,
    val temperature: String,
    val fertilizing: String,
    val tips: List<String>
)