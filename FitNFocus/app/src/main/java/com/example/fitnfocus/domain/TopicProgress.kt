package com.example.fitnfocus.domain

import java.time.LocalDate

/**
 * Domain model for topic progress tracking.
 * Tracks completion status of individual topics independent of sessions.
 */
data class TopicProgress(
    val id: Int = 0,
    val goalId: Int,
    val topicName: String,
    val isCompleted: Boolean = false,
    val completedAt: LocalDate? = null
)
