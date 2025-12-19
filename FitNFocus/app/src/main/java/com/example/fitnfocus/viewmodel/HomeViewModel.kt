package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.ActivityRepository
import com.example.fitnfocus.data.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate



class HomeViewModel(
    private val activityRepository: ActivityRepository,
    private val studyRepository: StudyRepository
) : ViewModel() {
    private  val _todaySteps = MutableStateFlow(0)
    val todaySteps = _todaySteps.asStateFlow()


    private val _todayFocusMinutes = MutableStateFlow(0)
    val todayFocusMinutes = _todayFocusMinutes.asStateFlow()

    fun loadTodayDashboard() {
        val today = LocalDate.now().toString()

        viewModelScope.launch {
            _todaySteps.value =
                activityRepository.getActivityByDate(today)?.steps ?: 0

            _todayFocusMinutes.value =
                studyRepository.getTotalMinutesByDate(today)

        }
    }
}