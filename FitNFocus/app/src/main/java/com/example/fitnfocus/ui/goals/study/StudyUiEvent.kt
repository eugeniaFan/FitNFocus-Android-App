package com.example.fitnfocus.ui.goals.study

import com.example.fitnfocus.domain.CalendarEventData

/**
 * One-time UI events for study feature.
 * Events emitted by ViewModel and consumed by UI for toasts, navigation, etc.
 */
sealed interface StudyUiEvent {
    data class ShowMessage(val message: String) : StudyUiEvent
    data object CloseAddDialog : StudyUiEvent
    data class OpenCalendarInsert(val event: CalendarEventData) : StudyUiEvent
}