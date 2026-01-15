package com.example.fitnfocus.domain

import java.time.LocalDate

/**
 * Repräsentiert den Fortschritt eines Topics.
 * Unabhängig von Sessions - speichert ob ein Topic abgeschlossen wurde.
 */
data class TopicProgress(
    val id: Int = 0,
    val goalId: Int,
    val topicName: String,
    val isCompleted: Boolean = false,
    val completedAt: LocalDate? = null
)

