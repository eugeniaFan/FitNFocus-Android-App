package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.StudyRepository
import com.example.fitnfocus.domain.StudySession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyViewModel(
    private val repository: StudyRepository
) : ViewModel() {

    // UI State
    private val _subject = MutableStateFlow("")
    val subject = _subject.asStateFlow()

    private val _duration = MutableStateFlow("")
    val duration = _duration.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage = _statusMessage.asStateFlow()


    fun onSubjectChange(newValue: String) {
        _subject.value = newValue
    }

    fun onDurationChange(newValue: String) {
        _duration.value = newValue
    }


    fun saveStudySession(date: String) {
        val minutes = _duration.value.toIntOrNull()

        if (minutes == null) {
            _statusMessage.value = "Please enter a valid number"
            return
        }

        val session = StudySession(
            id = 0,
            subject = _subject.value,
            durationMinutes = minutes,
            date = date
        )

        viewModelScope.launch {
            repository.insertStudySession(session)
            _statusMessage.value = "Saved!"
        }
    }
}