package com.example.fitnfocus.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

fun StudySession.toCalendarEventData(): CalendarEventData {
    val date = LocalDate.parse(date)
    val startTime = LocalTime.now()
    val startDateTime = LocalDateTime.of(date, startTime)
    val endDateTime = startDateTime.plusMinutes(this.durationMinutes.toLong())

    val zone = ZoneId.systemDefault()
    val startMillis = startDateTime.atZone(zone).toInstant().toEpochMilli()
    val endMillis = endDateTime.atZone(zone).toInstant().toEpochMilli()

    return CalendarEventData(
        title = "Study: ${this.subject}",
        description = "Fit & Focus study session (${this.durationMinutes} min)",
        startMillis = startMillis,
        endMillis = endMillis
    )
}