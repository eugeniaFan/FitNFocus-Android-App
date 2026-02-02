package com.example.fitnfocus.domain

/**
 * Data container for calendar export functionality.
 * Holds event information for system calendar integration.
 */
data class CalendarEventData(
    val title: String,
    val description: String? = null,
    val startMillis: Long,
    val endMillis: Long
)