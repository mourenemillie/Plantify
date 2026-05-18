package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _plantsCount = MutableStateFlow(3)
    val plantsCount: StateFlow<Int> = _plantsCount.asStateFlow()

    private val _daysActive = MutableStateFlow(15)
    val daysActive: StateFlow<Int> = _daysActive.asStateFlow()

    private val _tasksDone = MutableStateFlow(24)
    val tasksDone: StateFlow<Int> = _tasksDone.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }
}
