package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.ActivityRepository
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
 * ViewModel für den Profil-Screen.
 * Lädt die User-Daten aus dem DataStore und die Lernziele aus Room.
 */
class ProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val sessionRepository: SessionRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    /**
     * User-Daten aus dem Onboarding.
     */
    val user: StateFlow<User> = userPreferencesRepository.userFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = User()
        )

    /**
     * Alle aktiven Lernziele.
     */
    val learningGoals: StateFlow<List<LearningGoal>> = learningGoalRepository.getActiveGoals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Setzt das komplette Profil zurück:
     * - User-Präferenzen (DataStore)
     * - Lernziele
     * - Sessions
     * - Topic-Fortschritte automatisch wenn Goals gelöscht werden
     * - Aktivitäten
     *
     * Danach wird das Onboarding beim nächsten App-Start erneut angezeigt.
     */
    fun resetProfile() {
        viewModelScope.launch {
            // Alle Daten löschen
            sessionRepository.deleteAll()
            learningGoalRepository.deleteAll()
            activityRepository.deleteAll()
            // User-Präferenzen zuletzt löschen (triggert Onboarding)
            userPreferencesRepository.clearAllPreferences()
        }
    }
}

