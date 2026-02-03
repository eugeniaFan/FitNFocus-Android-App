package com.example.fitnfocus.ui.goals.study.timer

/**
 * UI state for the session timer screen.
 * All fields describe the current state of the timer UI.
 */
data class SessionTimerUiState(
    // Session info (passed from FocusViewModel)
    val sessionId: Int = 0,
    val sessionTopic: String = "",
    val moduleName: String = "",
    val goalId: Int? = null,

    // Timer values
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val timerState: TimerState = TimerState.IDLE,

    // Dialog flags
    val showSavePartialDialog: Boolean = false,
    val showCompletionCard: Boolean = false,

    // Notes & checkbox
    val sessionNotes: String = "",
    val markTopicAsCompleted: Boolean = false,

    // Error handling
    val errorMessage: String? = null
) {
    // Computed property for progress (0.0 - 1.0)
    val progress: Float
        get() = if (totalSeconds > 0) {
            1f - (remainingSeconds.toFloat() / totalSeconds)
        } else 0f

    // Elapsed seconds (for partial completion)
    val elapsedSeconds: Int
        get() = totalSeconds - remainingSeconds
}
