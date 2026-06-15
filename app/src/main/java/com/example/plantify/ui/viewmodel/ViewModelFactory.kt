package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plantify.data.repository.PlantRepository

class ViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CatalogViewModel::class.java) -> {
                CatalogViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddPlantViewModel::class.java) -> {
                AddPlantViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ScheduleViewModel::class.java) -> {
                ScheduleViewModel(repository) as T
            }
            modelClass.isAssignableFrom(GrowthProgressViewModel::class.java) -> {
                GrowthProgressViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddPlantTypeViewModel::class.java) -> {
                AddPlantTypeViewModel(repository) as T
            }
            // Menambahkan AlertsViewModel dari branch Hasna untuk halaman notifikasi
            modelClass.isAssignableFrom(AlertsViewModel::class.java) -> {
                AlertsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}