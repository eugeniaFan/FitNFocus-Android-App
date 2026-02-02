package com.example.fitnfocus.calendar

import com.example.fitnfocus.domain.CalendarEventData

/**
 * Interface for calendar export functionality.
 * Implementations provide platform-specific calendar integration.
 */
interface CalendarExporter {
    fun buildInsertIntent(event: CalendarEventData): android.content.Intent
}
