package com.example.fitnfocus.domain

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun StudySession.toCalendarEventData(): CalendarEventData {
    val startTime = LocalTime.now()
    val startDateTime = LocalDateTime.of(this.date, startTime)
    val endDateTime = startDateTime.plusMinutes(this.durationMinutes.toLong())

    val zone = ZoneId.systemDefault()
    val startMillis = startDateTime.atZone(zone).toInstant().toEpochMilli()
    val endMillis = endDateTime.atZone(zone).toInstant().toEpochMilli()

    return CalendarEventData(
        title = "Study: ${this.topic}",
        description = "Fit & Focus study session (${this.durationMinutes} min)",
        startMillis = startMillis,
        endMillis = endMillis
    )
}