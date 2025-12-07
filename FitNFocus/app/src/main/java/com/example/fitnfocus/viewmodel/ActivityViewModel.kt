package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.ActivityRepository
import com.example.fitnfocus.domain.DailyActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun saveActivity(activity: DailyActivity) {
        viewModelScope.launch {
            repository.insertActivity(activity)
        }
    }
}