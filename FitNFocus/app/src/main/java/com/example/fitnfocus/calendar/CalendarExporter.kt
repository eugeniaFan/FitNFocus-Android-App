package com.example.fitnfocus.calendar

import com.example.fitnfocus.domain.CalendarEventData

interface CalendarExporter {
    fun buildInsertIntent(event: CalendarEventData): android.content.Intent
}