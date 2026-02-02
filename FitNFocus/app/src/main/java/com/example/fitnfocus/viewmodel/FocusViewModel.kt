package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.domain.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for the Focus screen.
 * Shows earned coins and the collection overview.
 * Timer logic is handled in SessionTimerViewModel.
 */
class FocusViewModel(
    private val sessionRepository: SessionRepository,
    private val learningGoalRepository: LearningGoalRepository,
) : ViewModel() {

    private val _completedSessionsCount = MutableStateFlow(0)
    val completedSessionsCount = _completedSessionsCount.asStateFlow()

    fun loadTodaySessions() {
        viewModelScope.launch {
            val today = LocalDate.now()

            val todaySessions = sessionRepository.getSessionsByDate(today)
            _completedSessionsCount.value = todaySessions.count {
                it.status == SessionStatus.COMPLETED
            }
        }
    }
}
