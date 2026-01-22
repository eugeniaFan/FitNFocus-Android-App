package com.example.fitnfocus.domain

import java.time.LocalDate

/**
 * Erst mal nicht in Nutzung.
 */
data class DailyActivity(
    val date: LocalDate,  // Primary Key (wird als epochDay in DB gespeichert)
    val steps: Int,
    val highMovementMinutes: Int,
)
