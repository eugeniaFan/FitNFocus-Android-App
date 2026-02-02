package com.example.fitnfocus.calendar

import android.content.Intent
import android.provider.CalendarContract
import com.example.fitnfocus.domain.CalendarEventData

/**
 * Calendar exporter implementation using Android intents.
 * Integrates with system calendar app via ACTION_INSERT intent.
 */
class IntentCalendarExporter : CalendarExporter {
    override fun buildInsertIntent(event: CalendarEventData): Intent {
        return Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endMillis)
        }
    }
}
