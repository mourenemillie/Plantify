package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.local.entity.PlantCatalogEntity
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatalogViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val catalog: StateFlow<List<PlantCatalogEntity>> =
        combine(repository.allCatalog, _searchQuery) { all, query ->
            val q = query.trim()
            if (q.isEmpty()) {
                all
            } else {
                all.filter { plant ->
                    plant.nama_tanaman.contains(q, ignoreCase = true) ||
                            plant.difficulty?.contains(q, ignoreCase = true) == true
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        // Force a sync to get the latest from Supabase
        viewModelScope.launch {
            repository.syncWithSupabase()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}