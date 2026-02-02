package com.example.fitnfocus.viewmodel

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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for home screen dashboard.
 * Provides dashboard UI state and handles user actions for sessions and topics.
 */
class HomeViewModel(
    private val sessionRepository: SessionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val topicProgressRepository: TopicProgressRepository
) : ViewModel() {


    // Public state flows
    val user: StateFlow<User> = userPreferencesRepository.userFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = User()
        )


    /**
     * Dashboard state combining sessions, goals, and topic progress.
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

        val todaySessions = allSessions
            .filter { it.date == today && it.goalId != null }
            .distinctBy { it.id }

        val totalPlannedMinutesToday = todaySessions.sumOf { it.durationMinutes }

        val todayCompletedMinutes = todaySessions
            .filter { it.status == SessionStatus.COMPLETED }
            .sumOf { it.durationMinutes }

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
                elapsedSeconds = session.elapsedSeconds,
                status = session.status,
                notes = session.notes,
                isTopicCompleted = isCompletedForItem
            )
        }.sortedWith(
            compareBy<TodayLearningItem> { item ->
                when (item.status) {
                    SessionStatus.PLANNED -> 0
                    SessionStatus.STOPPED -> 1
                    SessionStatus.COMPLETED -> 2
                }
            }.thenByDescending { it.sessionId }
        )

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
