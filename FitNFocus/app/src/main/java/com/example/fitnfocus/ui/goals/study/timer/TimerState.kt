package com.example.fitnfocus.ui.goals.study.timer

/**
 * State of the session timer (Presentation Layer).
 *
 * NOTE:
 * - TimerState describes the current state of the timer UI:
 *
 * The difference:
 * - A session can have status=IN_PROGRESS, but the timer can be PAUSED
 * - FINISHED means "timer reached 0" (UI), not "saved to DB"
 * - STOPPED means "user stopped early" (UI dialog open)
 */
enum class TimerState {
    IDLE,       // Not started yet (timer ready)
    RUNNING,    // Timer actively running
    PAUSED,     // Timer paused
    STOPPED,    // Stopped early (dialog for partial save)
    FINISHED    // Time expired (remainingSeconds == 0)
}
