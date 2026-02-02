package com.example.fitnfocus.domain

import java.time.LocalDate

/**
 * Domain model for learning goals.
 * Represents a module or subject with associated topics and exam date.
 */
data class LearningGoal(
    val id: Int = 0,
    val moduleName: String,
    val topics: List<String> = emptyList(),
    val examDate: LocalDate? = null,
    val isCompleted: Boolean = false
)
