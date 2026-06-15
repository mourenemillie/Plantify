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
        combine(repository.allSchedules, repository.myPlants) { schedules, plants ->
            val items = schedules.map { entity ->
                val plantName = plants.find { it.id_kebun == entity.id_kebun }?.nama_pot ?: "Unknown Plant"
                
                val iconRes = when (entity.jenis_tugas) {
                    "Watering" -> R.drawable.ic_water_drop
                    "Fertilizing" -> R.drawable.ic_bolt
                    else -> R.drawable.ic_book
                }

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

    fun toggleDone(item: TaskScheduleEntity) {
        viewModelScope.launch {
            val updatedItem = item.copy(status_tugas = if (item.status_tugas == "Done") "Pending" else "Done")
            repository.updateSchedule(updatedItem)
        }
    }
}
