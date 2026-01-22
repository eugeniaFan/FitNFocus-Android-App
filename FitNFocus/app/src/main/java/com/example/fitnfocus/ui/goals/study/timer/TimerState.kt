package com.example.fitnfocus.ui.goals.study.timer

/**
 * Status des Session-Timers (Presentation Layer).
 *
 * HINWEIS: Dies ist KEIN Domain-State, sondern UI-State.
 * - Domain SessionStatus (PLANNED, IN_PROGRESS, STOPPED, COMPLETED) beschreibt den
 *   persistierten Zustand einer Session in der Datenbank.
 * - TimerState beschreibt den aktuellen Zustand der Timer-UI:
 *   z.B. ob der Timer gerade läuft, pausiert ist, etc.
 *
 * Der Unterschied:
 * - Eine Session kann status=IN_PROGRESS haben, aber der Timer kann PAUSED sein
 * - FINISHED bedeutet "Timer bei 0 angekommen" (UI), nicht "in DB gespeichert"
 * - STOPPED bedeutet "User hat vorzeitig gestoppt" (UI Dialog offen)
 */
enum class TimerState {
    IDLE,       // Noch nicht gestartet (Timer bereit)
    RUNNING,    // Timer läuft aktiv
    PAUSED,     // Timer pausiert
    STOPPED,    // Vorzeitig gestoppt (Dialog für Partial Save)
    FINISHED    // Zeit abgelaufen (remainingSeconds == 0)
}

