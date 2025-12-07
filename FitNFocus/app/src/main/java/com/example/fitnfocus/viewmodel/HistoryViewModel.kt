package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.ActivityRepository
import com.example.fitnfocus.data.repository.StudyRepository
import com.example.fitnfocus.domain.DailyActivity
import com.example.fitnfocus.domain.StudySession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel (
    private val activityRepository: ActivityRepository,
    private val studyRepository: StudyRepository
) : ViewModel() {

    private val _studyHistory = MutableStateFlow<List<StudySession>>(emptyList())
    val studyHistory = _studyHistory.asStateFlow()

    private val _activityHistory = MutableStateFlow<List<DailyActivity>>(emptyList())
    val activityHistory = _activityHistory.asStateFlow()

    init {
        loadStudyHistory()
//        loadActivityHistory()
    }


    private fun loadStudyHistory() {
        viewModelScope.launch {
            studyRepository.getAllStudySessions().collect {
                _studyHistory.value = it
            }
        }
    }

//    private fun loadActivityHistory() {
//        viewModelScope.launch {
//            activityRepository.getAllActivities().collect {
//                _activityHistory.value = it
//            }
//        }
//    }
}