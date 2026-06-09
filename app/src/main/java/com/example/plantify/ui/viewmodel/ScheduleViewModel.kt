package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.ScheduleGroup
import com.example.plantify.data.ScheduleItem
import com.example.plantify.data.ScheduleRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {

    val scheduleGroups: StateFlow<List<ScheduleGroup>> = ScheduleRepository.scheduleGroups

    fun toggleDone(item: ScheduleItem) {
        ScheduleRepository.toggleDone(item)
    }
}