package com.example.fitnfocus.domain

import java.time.LocalDate

/**
 * Lernziel-Datenmodell.
 * Repräsentiert ein Modul/Fach mit zugehörigen Themen und Prüfungstermin.
 */
data class LearningGoal(
    val id: Int = 0,
    val moduleName: String,
    val topics: List<String> = emptyList(),
    val examDate: LocalDate? = null,
    val isCompleted: Boolean = false
)
