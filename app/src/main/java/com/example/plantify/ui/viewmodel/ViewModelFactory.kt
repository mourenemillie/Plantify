package com.example.plantify.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plantify.data.local.PlantDatabase
import com.example.plantify.data.remote.AiService
import com.example.plantify.data.remote.LocationApiClient
import com.example.plantify.data.remote.NominatimApiClient
import com.example.plantify.data.remote.BmkgApiClient
import com.example.plantify.data.repository.LocationRepository
import com.example.plantify.data.repository.PlantRepository

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = PlantDatabase.getDatabase(context)
        val aiService = AiService()
        val plantRepository = PlantRepository(database.plantDao(), aiService)

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(plantRepository) as T
        }
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(plantRepository) as T
        }
        if (modelClass.isAssignableFrom(GrowthProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GrowthProgressViewModel(plantRepository) as T
        }
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            val locationRepository = LocationRepository(
                LocationApiClient.instance,
                NominatimApiClient.instance,
                BmkgApiClient.instance
            )
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(locationRepository, plantRepository) as T
        }
        if (modelClass.isAssignableFrom(AddPlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPlantViewModel(plantRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}