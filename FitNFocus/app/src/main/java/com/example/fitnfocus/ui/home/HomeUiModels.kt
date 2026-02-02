package com.example.fitnfocus.ui.home

import com.example.fitnfocus.domain.SessionStatus
import java.time.LocalDate

/**
 * UI models for home screen dashboard.
 * Separated from ViewModel for single responsibility.
 */

/**
 * Aggregated UI state for the dashboard.
 */
data class DashboardState(
    val todayLearningItems: List<TodayLearningItem> = emptyList(),
    val todayCompletedTopics: List<CompletedTopicItem> = emptyList(),
    val todayFocusMinutes: Int = 0,
    val totalPlannedMinutes: Int = 0
)

/**
 * UI model for a session card on the dashboard.
 */
data class TodayLearningItem(
    val sessionId: Int,
    val goalId: Int?,
    val moduleName: String,
    val topic: String,
    val durationMinutes: Int,
    val elapsedSeconds: Int = 0,
    val status: SessionStatus,
    val notes: String,
    val isTopicCompleted: Boolean
)

/**
 * UI model for a topic completed today.
 */
data class CompletedTopicItem(
    val moduleName: String,
    val topic: String,
    val completedAt: LocalDate?
)