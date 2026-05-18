package com.example.plantify.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.plantify.data.PlantCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CatalogViewModel : ViewModel() {

    private val _plants = MutableStateFlow<List<PlantCategory>>(emptyList())
    val plants: StateFlow<List<PlantCategory>> = _plants.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadCatalog()
    }

    private fun loadCatalog() {
        _plants.value = listOf(
            PlantCategory("Tomato", "Easy", Color(0xFFE8F5E9), "60-80 days", "Water daily"),
            PlantCategory("Red Chili", "Medium", Color(0xFFFFF3E0), "70-90 days", "Water 2x/day"),
            PlantCategory("Spinach", "Easy", Color(0xFFE8F5E9), "40-50 days", "Water daily"),
            PlantCategory("Mustard Greens", "Easy", Color(0xFFE8F5E9), "30-40 days", "Water daily"),
            PlantCategory("Lettuce", "Easy", Color(0xFFE8F5E9), "45-55 days", "Water 2x/day"),
            PlantCategory("Green Onion", "Very easy", Color(0xFFE8F5E9), "60-80 days", "Water daily"),
            PlantCategory("Bell Pepper", "Medium", Color(0xFFFFF3E0), "70-85 days", "Water 2x/day"),
            PlantCategory("Cucumber", "Easy", Color(0xFFE8F5E9), "50-65 days", "Water daily"),
        )
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // Here you could filter _plants based on the query, but we keep it simple for now
    }
}
