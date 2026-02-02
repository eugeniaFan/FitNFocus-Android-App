package com.example.fitnfocus.calendar

import com.example.fitnfocus.domain.CalendarEventData
import com.example.fitnfocus.domain.StudySession
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Mapper from StudySession to CalendarEventData.
 * Converts domain session to calendar event format with time and duration.
 */
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
 * Convenience overload using current local time as start time.
 */
fun StudySession.toCalendarEventDataNow(
    zoneId: ZoneId = ZoneId.systemDefault()
): CalendarEventData = toCalendarEventData(
    startTime = LocalTime.now(), zoneId = zoneId
)

