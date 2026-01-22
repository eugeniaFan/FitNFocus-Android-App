package com.example.fitnfocus.ui.goals.study

import com.example.fitnfocus.domain.CalendarEventData

/**
 * Einmalige UI Events für das Study-Feature.
 * Diese Events werden vom ViewModel emittiert und von der UI konsumiert (z.B. für Toasts, Navigation).
 */
sealed interface StudyUiEvent {
    /** Zeigt eine Snackbar/Toast Nachricht an */
    data class ShowMessage(val message: String) : StudyUiEvent

    /** Schließt den Add-Dialog */
    data object CloseAddDialog : StudyUiEvent

    /** Öffnet die Kalender-App zum Einfügen eines Events */
    data class OpenCalendarInsert(val event: CalendarEventData) : StudyUiEvent
}