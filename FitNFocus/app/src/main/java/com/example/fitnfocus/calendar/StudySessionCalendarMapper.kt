package com.example.fitnfocus.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitnfocus.domain.CalendarEventData
import com.example.fitnfocus.domain.StudySession
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Mapping von StudySession (Domain) -> CalendarEventData (für CalendarExporter).
 *
 * Hinweis zur Semantik:
 * - startTime: Uhrzeit am Tag der Session
 * - endTime ergibt sich aus durationMinutes
 */
@RequiresApi(Build.VERSION_CODES.O)
fun StudySession.toCalendarEventData(
    startTime: LocalTime,
    zoneId: ZoneId = ZoneId.systemDefault(),
    titlePrefix: String = "Study: ",
    descriptionPrefix: String = "Fit & Focus study session"
): CalendarEventData {
    val startDateTime = LocalDateTime.of(this.date, startTime)
    val endDateTime = startDateTime.plusMinutes(this.durationMinutes.toLong())

    val startMillis = startDateTime.atZone(zoneId).toInstant().toEpochMilli()
    val endMillis = endDateTime.atZone(zoneId).toInstant().toEpochMilli()

    return CalendarEventData(
        title = "$titlePrefix${this.topic}",
        description = "$descriptionPrefix (${this.durationMinutes} min)",
        startMillis = startMillis,
        endMillis = endMillis
    )
}

/**
 * Convenience-Overload: Startzeit ist "jetzt".
 * Export startet sofort.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun StudySession.toCalendarEventDataNow(
    zoneId: ZoneId = ZoneId.systemDefault()
): CalendarEventData = toCalendarEventData(
    startTime = LocalTime.now(),
    zoneId = zoneId
)

