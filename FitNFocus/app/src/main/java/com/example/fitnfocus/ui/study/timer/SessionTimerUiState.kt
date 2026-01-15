package com.example.fitnfocus.ui.study.timer

/**
 * UI-State für den Session-Timer-Screen.
 * Alle Felder beschreiben den aktuellen Zustand der Timer-UI.
 */
data class SessionTimerUiState(
    // Session-Info (von FocusViewModel übergeben)
    val sessionId: Int = 0,
    val sessionTopic: String = "",
    val moduleName: String = "",
    val goalId: Int? = null,

    // Timer-Werte
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val timerState: TimerState = TimerState.IDLE,

    // Dialog Flags
    val showSavePartialDialog: Boolean = false,
    val showCompletionCard: Boolean = false,

    // Notizen & Checkbox
    val sessionNotes: String = "",
    val markTopicAsCompleted: Boolean = false,

    // Fehler-Handling
    val errorMessage: String? = null
) {
    // Computed Property für Fortschritt (0.0 - 1.0)
    val progress: Float
        get() = if (totalSeconds > 0) {
            1f - (remainingSeconds.toFloat() / totalSeconds)
        } else 0f

    // Elapsed Seconds (für Partial Completion)
    val elapsedSeconds: Int
        get() = totalSeconds - remainingSeconds
}

