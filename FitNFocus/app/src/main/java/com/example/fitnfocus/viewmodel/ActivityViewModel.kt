package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.ActivityRepository
import com.example.fitnfocus.domain.DailyActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate


class ActivityViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    private val _activity = MutableStateFlow<DailyActivity?>(null)
    val activity = _activity.asStateFlow()

    fun loadActivity(date: String) {
        viewModelScope.launch {
            _activity.value = repository.getActivityByDate(date)
        }
    }


    fun addSteps(stepsToAdd: Int) {
        val today = LocalDate.now().toString()
        viewModelScope.launch {
            val current = repository.getActivityByDate(today)
                ?: DailyActivity(
                    date = today,
                    steps = 0,
                    highMovementMinutes = 0
                )
            val updated = current.copy(steps = current.steps + stepsToAdd)
            repository.insertActivity(updated)
            _activity.value = updated

        }


    }

    fun saveActivity(activity: DailyActivity) {
        viewModelScope.launch {
            repository.insertActivity(activity)
        }
    }
}