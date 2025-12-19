package com.example.fitnfocus.viewmodel

import com.example.fitnfocus.domain.CalendarEventData

sealed interface StudyUiEvent {
    data class ShowMessage(val message: String) : StudyUiEvent
    data object CloseAddDialog : StudyUiEvent
    data class OpenCalendarInsert(val event: CalendarEventData) : StudyUiEvent
}