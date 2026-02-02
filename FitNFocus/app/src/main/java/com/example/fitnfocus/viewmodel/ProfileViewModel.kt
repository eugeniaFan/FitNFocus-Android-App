package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.UserPreferencesRepository
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.domain.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for profile screen.
 * Loads user data from DataStore and learning goals from Room database.
 */
class ProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val user: StateFlow<User> = userPreferencesRepository.userFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = User()
        )

    val learningGoals: StateFlow<List<LearningGoal>> = learningGoalRepository.getActiveGoals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Resets complete profile including preferences, goals, sessions, and activities.
     * Onboarding will be shown again on next app start.
     */
    fun resetProfile() {
        viewModelScope.launch {
            sessionRepository.deleteAll()
            learningGoalRepository.deleteAll()
            userPreferencesRepository.clearAllPreferences()
        }
    }
}

