package com.example.fitnfocus.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.TopicProgressRepository
import com.example.fitnfocus.data.repository.UserPreferencesRepository
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.User
import com.example.fitnfocus.domain.StudySession
import com.example.fitnfocus.domain.TopicProgress
import com.example.fitnfocus.ui.home.CompletedTopicItem
import com.example.fitnfocus.ui.home.DashboardState
import com.example.fitnfocus.ui.home.TodayLearningItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel für den HomeScreen / Dashboard.
 *
 * Verantwortlich für:
 * - Bereitstellung von UI-State für das Dashboard
 * - Verarbeitung von User-Aktionen (Session-Updates, Topic-Completion)
 *
 * State wird rein reaktiv aus Repository-Flows abgeleitet.
 */
@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val sessionRepository: SessionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val topicProgressRepository: TopicProgressRepository
) : ViewModel() {

    private val _selectedSessionForEdit = MutableStateFlow<TodayLearningItem?>(null)

    // ==================== PUBLIC STATE FLOWS ====================

    val user: StateFlow<User> = userPreferencesRepository.userFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = User()
        )
    val selectedSessionForEdit: StateFlow<TodayLearningItem?> = _selectedSessionForEdit.asStateFlow()

    /**
     * Dashboard-State: Kombiniert Sessions, Goals und TopicProgress reaktiv.
     */
    val dashboardState: StateFlow<DashboardState> = combine(
        sessionRepository.getAllSessions(),
        learningGoalRepository.getActiveGoals(),
        topicProgressRepository.getAllCompletedTopics()
    ) { sessions, goals, completedTopics ->
        buildDashboardState(sessions, goals, completedTopics)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    /**
     * Schritte heute - aus ActivityRepository geladen. (Nicht in Nutzung)
     */

    // ==================== PUBLIC EVENT FUNCTIONS ====================

    fun selectSessionForEdit(item: TodayLearningItem?) {
        _selectedSessionForEdit.value = item
    }

    fun updateSessionStatus(sessionId: Int, status: SessionStatus) {
        viewModelScope.launch {
            sessionRepository.updateSessionStatus(sessionId, status)
        }
    }

    fun updateSessionNotes(sessionId: Int, notes: String) {
        viewModelScope.launch {
            sessionRepository.updateSessionNotes(sessionId, notes)
        }
    }

    fun markTopicCompleted(goalId: Int?, topic: String, isCompleted: Boolean) {
        if (goalId == null) return
        viewModelScope.launch {
            topicProgressRepository.markTopicCompleted(goalId, topic, isCompleted)
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.clearAllPreferences()
        }
    }

    private fun buildDashboardState(
        allSessions: List<StudySession>,
        goals: List<LearningGoal>,
        completedTopics: List<TopicProgress>
    ): DashboardState {
        val today = LocalDate.now()

        // Sessions für Dashboard: nur Sessions von heute (egal Status)
        val todaySessions = allSessions
            .filter { it.date == today && it.goalId != null}
            .distinctBy { it.id }

        // Nenner: alle Minuten der heutigen Sessions (egal Status)
        val totalPlannedMinutesToday = todaySessions.sumOf { it.durationMinutes }

        // Zähler: nur Minuten der heute COMPLETED Sessions
        val todayCompletedMinutes = todaySessions
            .filter { it.status == SessionStatus.COMPLETED }
            .sumOf { it.durationMinutes }

        // Completed Topics Set für schnellen Lookup
        val completedTopicKeys: Set<Pair<Int, String>> = completedTopics
            .asSequence()
            .filter { it.isCompleted }
            .map { progress ->
                val gid = progress.goalId
                gid to progress.topicName
            }
            .toSet()

        val completedTopicNames: Set<String> = completedTopics
            .asSequence()
            .filter { it.isCompleted }
            .map { it.topicName }
            .toSet()

        // UI-Liste für heute
        val learningItems = todaySessions.map { session ->
            val goalForSession = session.goalId?.let { gid ->
                goals.firstOrNull { it.id == gid }
            }

            val fallbackGoalByTopic = if (goalForSession == null) {
                goals.firstOrNull { goal ->
                    goal.topics.any { it.equals(session.topic, ignoreCase = true) }
                }
            } else null

            val resolvedGoal = goalForSession ?: fallbackGoalByTopic
            val itemGoalId = resolvedGoal?.id ?: session.goalId

            val isCompletedForItem = if (itemGoalId != null) {
                (itemGoalId to session.topic) in completedTopicKeys
            } else {
                session.topic in completedTopicNames
            }

            TodayLearningItem(
                sessionId = session.id,
                goalId = itemGoalId,
                moduleName = resolvedGoal?.moduleName ?: "Lernen",
                topic = session.topic,
                durationMinutes = session.durationMinutes,
                status = session.status,
                notes = session.notes,
                isTopicCompleted = isCompletedForItem
            )
        }.sortedWith(
            compareBy<TodayLearningItem> { item ->
                when (item.status) {
                    SessionStatus.PLANNED -> 0
                    SessionStatus.IN_PROGRESS -> 1
                    SessionStatus.STOPPED -> 2
                    SessionStatus.COMPLETED -> 3
                }
            }.thenByDescending { it.sessionId }
        )

        // Heute abgeschlossene Topics
        val todayCompletedItems = completedTopics
            .filter { it.isCompleted && it.completedAt == today }
            .map { progress ->
                val goal = goals.find { it.id == progress.goalId }
                CompletedTopicItem(
                    moduleName = goal?.moduleName ?: "Lernen",
                    topic = progress.topicName,
                    completedAt = progress.completedAt
                )
            }

        return DashboardState(
            todayLearningItems = learningItems,
            todayCompletedTopics = todayCompletedItems,
            todayFocusMinutes = todayCompletedMinutes,
            totalPlannedMinutes = totalPlannedMinutesToday
        )
    }
}
