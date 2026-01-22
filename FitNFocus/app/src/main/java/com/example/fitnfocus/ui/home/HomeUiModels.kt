package com.example.fitnfocus.ui.home

import com.example.fitnfocus.domain.SessionStatus
import java.time.LocalDate

/**
 * UI-Modelle für den HomeScreen / Dashboard.
 * Getrennt vom ViewModel für Single Responsibility.
 */

/**
 * Aggregierter UI-State für das Dashboard.
 */
data class DashboardState(
    val todayLearningItems: List<TodayLearningItem> = emptyList(),
    val todayCompletedTopics: List<CompletedTopicItem> = emptyList(),
    val todayFocusMinutes: Int = 0,
    val totalPlannedMinutes: Int = 0
)

/**
 * UI-Modell für eine Session auf dem Dashboard.
 * Enthält alle Informationen, die die UI zur Darstellung benötigt.
 */
data class TodayLearningItem(
    val sessionId: Int,
    val goalId: Int?,
    val moduleName: String,
    val topic: String,
    val durationMinutes: Int,
    val status: SessionStatus,
    val notes: String,
    val isTopicCompleted: Boolean
)

/**
 * UI-Modell für ein heute abgeschlossenes Thema.
 */
data class CompletedTopicItem(
    val moduleName: String,
    val topic: String,
    val completedAt: LocalDate?
)