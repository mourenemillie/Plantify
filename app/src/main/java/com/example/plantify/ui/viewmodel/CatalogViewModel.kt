package com.example.plantify.ui.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _catalog = MutableStateFlow<List<PlantCatalogEntity>>(emptyList())
    val catalog: StateFlow<List<PlantCatalogEntity>> = _catalog.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadCatalog()
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            repository.allCatalog.collect {
                _catalog.value = it
            }
        }
        // Force a sync to get the latest from Supabase
        viewModelScope.launch {
            repository.syncWithSupabase()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
