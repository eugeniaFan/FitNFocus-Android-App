package com.example.fitnfocus.ui.goals.study

import com.example.fitnfocus.domain.StudySession

/**
 * Navigation-State für den Lern-Bereich.
 * Definiert alle möglichen Screens innerhalb des Study-Features.
 */
sealed class LearningNavigationState {
    /** Übersicht aller Lernziele */
    data object Overview : LearningNavigationState()

    /** Detail-Ansicht eines Lernziels mit Topics */
    data class GoalDetail(val goalId: Int) : LearningNavigationState()

    /** Sessions-Liste für ein bestimmtes Topic */
    data class TopicDetail(val goalId: Int, val topic: String) : LearningNavigationState()

    /** Timer-Screen für eine aktive Session */
    data class SessionTimer(
        val goalId: Int,
        val topic: String,
        val moduleName: String,
        val durationMinutes: Int,
        val sessionId: Int
    ) : LearningNavigationState()
}

/**
 * Status eines Topics basierend auf Sessions und TopicProgress.
 */
enum class TopicStatus {
    /** Keine Session für dieses Topic erstellt */
    NOT_STARTED,

    /** Mindestens eine Session erstellt, aber Topic nicht abgeschlossen */
    IN_PROGRESS,

    /** Topic als abgeschlossen markiert */
    COMPLETED
}

/**
 * Repräsentiert ein Topic zur Auswahl im Dropdown.
 */
data class TopicItem(
    val name: String,
    val goalId: Int?,           // null = manuell hinzugefügt
    val goalName: String? = null // Name des Moduls für Anzeige
)

/**
 * UI State für das Hinzufügen eines neuen Lernziels.
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
 * UI State für das Bearbeiten eines Lernziels.
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
 * UI State für Session-Dialoge.
 */
data class SessionDialogUiState(
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val editingSession: StudySession? = null,
    val deletingSession: StudySession? = null,

    // Add Dialog Fields
    val newTopic: String = "",
    val selectedTopic: TopicItem? = null,

    val isSaving: Boolean = false
)

