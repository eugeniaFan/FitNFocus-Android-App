package com.example.fitnfocus.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.ActivityRepository
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.TopicProgressRepository
import com.example.fitnfocus.data.repository.UserPreferencesRepository
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.User
import com.example.fitnfocus.domain.StudySession
import com.example.fitnfocus.domain.TopicProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * UI-Modell für "Lernziel für heute" auf dem Dashboard.
 * Erweitert um Session-Details für interaktive Funktionen.
 */
data class TodayLearningItem(
    val sessionId: Int,           // Session ID für Updates
    val goalId: Int?,             // Goal ID für Topic-Completion
    val moduleName: String,
    val topic: String,
    val durationMinutes: Int,
    val status: SessionStatus,
    val notes: String,
    val isTopicCompleted: Boolean // Wird aus TopicProgress abgeleitet
)

/**
 * Abgeschlossene Themen für heute.
 */
data class CompletedTopicItem(
    val moduleName: String,
    val topic: String,
    val completedAt: LocalDate?
)

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val activityRepository: ActivityRepository,
    private val sessionRepository: SessionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val topicProgressRepository: TopicProgressRepository
) : ViewModel() {

    private val dateFormatter: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    } else {
        null
    }

    // Activity & Focus Stats
    private val _todaySteps = MutableStateFlow(0)
    val todaySteps = _todaySteps.asStateFlow()

    private val _todayFocusMinutes = MutableStateFlow(0)
    val todayFocusMinutes = _todayFocusMinutes.asStateFlow()

    // Geplante Gesamtminuten
    private val _totalPlannedMinutes = MutableStateFlow(0)
    val totalPlannedMinutes = _totalPlannedMinutes.asStateFlow()

    // User-Daten (Flow - automatisch aktualisiert)
    val user: StateFlow<User> = userPreferencesRepository.userFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = User()
        )

    // Lernziele als Flow
    val learningGoals: StateFlow<List<LearningGoal>> = learningGoalRepository.getActiveGoals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Abgeschlossene Topics als Flow
    private val completedTopics = topicProgressRepository.getAllCompletedTopics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Sessions als Flow (nur API 26+)
    private val allSessions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        sessionRepository.getAllSessions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    } else {
        MutableStateFlow<List<StudySession>>(emptyList())
    }

    // Combined Flow für Dashboard-Daten
    private val _todayLearningItems = MutableStateFlow<List<TodayLearningItem>>(emptyList())
    val todayLearningItems: StateFlow<List<TodayLearningItem>> = _todayLearningItems.asStateFlow()

    private val _todayCompletedTopics = MutableStateFlow<List<CompletedTopicItem>>(emptyList())
    val todayCompletedTopics: StateFlow<List<CompletedTopicItem>> = _todayCompletedTopics.asStateFlow()

    private val _todayCompletedSessions = MutableStateFlow<List<TodayLearningItem>>(emptyList())
    val todayCompletedSessions: StateFlow<List<TodayLearningItem>> = _todayCompletedSessions.asStateFlow()

    private val _selectedSessionForEdit = MutableStateFlow<TodayLearningItem?>(null)
    val selectedSessionForEdit: StateFlow<TodayLearningItem?> = _selectedSessionForEdit.asStateFlow()

    init {
        // Reaktives Dashboard: Aktualisiert sich automatisch bei Änderungen
        setupReactiveDashboard()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupReactiveDashboard() {
        viewModelScope.launch {
            // Kombiniere alle relevanten Flows
            combine(
                allSessions,
                learningGoals,
                completedTopics
            ) { sessions, goals, completedTopicsList ->
                Triple(sessions, goals, completedTopicsList)
            }.collect { (sessions, goals, completedTopicsList) ->
                updateDashboard(sessions, goals, completedTopicsList)
            }
        }
    }

    private suspend fun updateDashboard(
        allSessions: List<StudySession>,
        goals: List<LearningGoal>,
        completedTopics: List<TopicProgress>
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val today = LocalDate.now()
        val todayString = today.format(dateFormatter)

        // Activity-Daten laden
        _todaySteps.value = activityRepository.getActivityByDate(todayString)?.steps ?: 0

        // Fokus-Minuten berechnen
        _todayFocusMinutes.value = sessionRepository.getTotalMinutesByDate(today)

        // Geplante Sessions (alle Daten) + heute nicht-geplante Sessions (IN_PROGRESS, STOPPED, COMPLETED)
        val plannedSessions = allSessions.filter { it.status == SessionStatus.PLANNED }
        val todayNonPlannedSessions = allSessions.filter {
            it.date == today && it.status != SessionStatus.PLANNED
        }

        val sessionsToShow = (plannedSessions + todayNonPlannedSessions).distinctBy { it.id }

        // Map zu UI-Modellen
        val completedTopicsSet = completedTopics
            .filter { it.isCompleted }
            .map { it.topicName }
            .toSet()

        _todayLearningItems.value = sessionsToShow.map { session ->
            val goalForTopic = goals.firstOrNull { goal ->
                goal.topics.any { it.equals(session.topic, ignoreCase = true) }
            }

            TodayLearningItem(
                sessionId = session.id,
                goalId = goalForTopic?.id ?: session.goalId,
                moduleName = goalForTopic?.moduleName ?: "Lernen",
                topic = session.topic,
                durationMinutes = session.durationMinutes,
                status = session.status,
                notes = session.notes,
                isTopicCompleted = session.topic in completedTopicsSet
            )
        }.sortedWith(
            // Sortierung: PLANNED zuerst, dann IN_PROGRESS/STOPPED, dann COMPLETED
            // Innerhalb jeder Gruppe: neueste Sessions zuerst (nach sessionId absteigend)
            compareBy<TodayLearningItem> { item ->
                when (item.status) {
                    SessionStatus.PLANNED -> 0
                    SessionStatus.IN_PROGRESS -> 1
                    SessionStatus.STOPPED -> 2
                    SessionStatus.COMPLETED -> 3
                }
            }.thenByDescending { it.sessionId }
        )

        _totalPlannedMinutes.value = sessionsToShow.sumOf { it.durationMinutes }

        // Heute abgeschlossene Topics
        val todayCompletedTopicsProgress = completedTopics.filter {
            it.isCompleted && it.completedAt == today
        }

        _todayCompletedTopics.value = todayCompletedTopicsProgress.map { progress ->
            val goal = goals.find { it.id == progress.goalId }
            CompletedTopicItem(
                moduleName = goal?.moduleName ?: "Lernen",
                topic = progress.topicName,
                completedAt = progress.completedAt
            )
        }
    }

    /**
     * Manuelles Neuladen (für Pull-to-Refresh o.ä.)
     * Verwendet die aktuellen StateFlow-Werte die durch den reaktiven Flow aktualisiert werden.
     */
    fun loadTodayDashboard() {
        viewModelScope.launch {
            val sessions = allSessions.value
            val goals = learningGoals.value
            val completed = completedTopics.value
            updateDashboard(sessions, goals, completed)
        }
    }

    fun selectSessionForEdit(item: TodayLearningItem?) {
        _selectedSessionForEdit.value = item
    }

    /**
     * Aktualisiert den Status einer Session.
     * Dashboard aktualisiert sich automatisch durch Flow.
     */
    fun updateSessionStatus(sessionId: Int, status: SessionStatus) {
        viewModelScope.launch {
            sessionRepository.updateSessionStatus(sessionId, status)
            // Kein manuelles Reload nötig - Flow aktualisiert automatisch
        }
    }

    /**
     * Aktualisiert die Notizen einer Session.
     */
    fun updateSessionNotes(sessionId: Int, notes: String) {
        viewModelScope.launch {
            sessionRepository.updateSessionNotes(sessionId, notes)
        }
    }

    /**
     * Markiert ein Topic als abgeschlossen.
     */
    fun markTopicCompleted(goalId: Int, topic: String, isCompleted: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        viewModelScope.launch {
            topicProgressRepository.markTopicCompleted(goalId, topic, isCompleted)
        }
    }

    /**
     * Setzt das Onboarding zurück (für Testzwecke).
     */
    fun resetOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.clearAllPreferences()
        }
    }
}