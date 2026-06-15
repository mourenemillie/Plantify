package com.example.plantify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantify.data.local.entity.TaskScheduleEntity
import com.example.plantify.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(private val repository: PlantRepository) : ViewModel() {

    private val _allSchedules = MutableStateFlow<List<TaskScheduleEntity>>(emptyList())
    val allSchedules: StateFlow<List<TaskScheduleEntity>> = _allSchedules.asStateFlow()

    init {
        loadSchedule()
    }

    // Mengambil data jadwal langsung dari database lokal
    private fun loadSchedule() {
        viewModelScope.launch {
            repository.allSchedules.collect {
                _allSchedules.value = it
            }
        }
        viewModelScope.launch {
            repository.syncWithSupabase()
        }
    }

    // Mengubah status tugas selesai (Done) atau belum (Pending)
    fun toggleDone(item: TaskScheduleEntity) {
        viewModelScope.launch {
            val updatedItem = item.copy(status_tugas = if (item.status_tugas == "Done") "Pending" else "Done")
            repository.updateSchedule(updatedItem)
        }
    }
}