package com.example.fitnfocus.domain

data class CalendarEventData(
    val title: String,
    val description: String? = null,
    val startMillis: Long,
    val endMillis: Long
)
