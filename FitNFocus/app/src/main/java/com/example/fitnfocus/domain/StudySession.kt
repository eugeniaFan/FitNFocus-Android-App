package com.example.fitnfocus.domain

import java.time.LocalDate

/**
 * Status einer Lernsession.
 */
enum class SessionStatus {
    PLANNED,      // Geplant
    IN_PROGRESS,  // In Bearbeitung,
    STOPPED,      // Gestoppt
    COMPLETED     // Abgeschlossen
}

/**
 * Repräsentiert eine Lernsession.
 * Eine Session ist ein "Event" - eine konkrete Lerneinheit für ein Topic.
 */
data class StudySession(
    val id: Int = 0,
    val topic: String,                 // Das Thema der Session (früher "subject")
    val durationMinutes: Int,
    val date: LocalDate,               // Datum der Session
    val goalId: Int? = null,           // Verknüpfung zum Lernziel
    val status: SessionStatus = SessionStatus.PLANNED,
    val notes: String = "",            // Notizen/Todos
    val elapsedSeconds: Int = 0        // Tatsächlich fokussierte Zeit (für Partial Completion)
)