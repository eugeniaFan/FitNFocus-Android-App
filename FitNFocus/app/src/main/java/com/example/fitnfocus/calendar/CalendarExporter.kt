package com.example.fitnfocus.calendar

import com.example.fitnfocus.domain.CalendarEventData
import android.content.Intent


/**
 * Interface for calendar export functionality.
 * Implementations provide platform-specific calendar integration.
 */
interface CalendarExporter {
    fun buildInsertIntent(event: CalendarEventData): Intent
}
