package com.example.fitnfocus.domain

import java.time.LocalDate

/**
 * Status of a study session.
 */
enum class SessionStatus {
    PLANNED,
    STOPPED,
    COMPLETED
}

/**
 * Domain model for study sessions.
 * Represents a concrete learning event for a specific topic.
 */
data class StudySession(
    val id: Int = 0,
    val topic: String,
    val durationMinutes: Int,
    val date: LocalDate,
    val goalId: Int? = null,
    val status: SessionStatus = SessionStatus.PLANNED,
    val notes: String = "",
    val elapsedSeconds: Int = 0
)