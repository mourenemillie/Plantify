package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.repository.PlantRepository
import com.example.plantify.data.remote.SupabaseConfig
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _userName = MutableStateFlow("Registered User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _memberSince = MutableStateFlow("Joined recently")
    val memberSince: StateFlow<String> = _memberSince.asStateFlow()

    private val _plantsCount = MutableStateFlow(0)
    val plantsCount: StateFlow<Int> = _plantsCount.asStateFlow()

    private val _daysActive = MutableStateFlow(0)
    val daysActive: StateFlow<Int> = _daysActive.asStateFlow()

    private val _tasksDone = MutableStateFlow(0)
    val tasksDone: StateFlow<Int> = _tasksDone.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // COROUTINE 1: Mengamati data tanaman secara real-time dari database lokal (Room/SQLite via Repository).
        viewModelScope.launch {
            repository.myPlants.collect {
                _plantsCount.value = it.size
            }
        }
        viewModelScope.launch {
            repository.allSchedules.collect { schedules ->
                _tasksDone.value = schedules.count { it.status_tugas == "Done" }
            }
        }
        viewModelScope.launch {
            try {
                val user = SupabaseConfig.supabase.auth.currentUserOrNull()
                if (user != null) {
                    _userName.value = user.email ?: "Registered User"
                    // You could parse created_at if available
                }
            } catch (e: Exception) {
                // Not logged in or Auth not initialized
            }
            repository.syncWithSupabase()
        }
    }
}
