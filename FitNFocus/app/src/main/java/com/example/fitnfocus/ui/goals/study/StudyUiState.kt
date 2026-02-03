package com.example.fitnfocus.ui.goals.study

import com.example.fitnfocus.domain.StudySession

/**
 * Navigation state for the study feature.
 * Defines the screens shown within the study flow.
 */
sealed class LearningNavigationState {
    // Overview of all learning goals.
    data object Overview : LearningNavigationState()

    // Detail view for a learning goal with its topics.
    data class GoalDetail(val goalId: Int) : LearningNavigationState()

    // Session list for a specific topic.
    data class TopicDetail(val goalId: Int, val topic: String) : LearningNavigationState()

    // Timer screen for an active session.
    data class SessionTimer(
        val goalId: Int,
        val topic: String,
        val moduleName: String,
        val durationMinutes: Int,
        val sessionId: Int
    ) : LearningNavigationState()
}

/**
 * Status of a topic based on sessions and topic progress.
 */
enum class TopicStatus {
    // No session created for this topic yet
    NOT_STARTED,

    // At least one session created, but topic not completed
    IN_PROGRESS,

    // Topic marked as completed
    COMPLETED
}

/**
 * UI model for a selectable topic.
 */
data class TopicItem(
    val name: String,
    val goalId: Int?,           // null when added manually
    val goalName: String? = null // module name for display
)

data class GoalTopicKey(val goalId: Int, val topic: String)

/**
 * UI state for adding a new learning goal.
 */
data class AddGoalUiState(
    val showSheet: Boolean = false,
    val moduleName: String = "",
    val examDateText: String = "",
    val topics: List<String> = emptyList(),
    val currentTopic: String = "",
    val isSaving: Boolean = false
)

/**
 * UI state for editing an existing learning goal.
 */
data class EditGoalUiState(
    val showSheet: Boolean = false,
    val goalId: Int = 0,
    val moduleName: String = "",
    val examDateText: String = "",
    val topics: List<String> = emptyList(),
    val currentTopic: String = "",
    val isSaving: Boolean = false
)

/**
 * UI state for session dialogs.
 */
data class SessionDialogUiState(
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val editingSession: StudySession? = null,
    val deletingSession: StudySession? = null,
    val newTopic: String = "",
    val selectedTopic: TopicItem? = null,
    val isSaving: Boolean = false
)
