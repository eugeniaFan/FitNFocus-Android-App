package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val activityRepository: ActivityRepository
) : ViewModel() {
    private  val _todaySteps = MutableStateFlow(0)
    val todaySteps = _todaySteps.asStateFlow()

    fun loadTodayActivity() {
        viewModelScope.launch {
            val today = "2025-12-07" // später ändern
            val activity = activityRepository.getActivityByDate(today)
            _todaySteps.value = activity?.steps ?: 0
        }

    }
}