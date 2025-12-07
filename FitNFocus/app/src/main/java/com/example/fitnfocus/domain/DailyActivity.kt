package com.example.fitnfocus.domain

data class DailyActivity(
    val date: String, //Primary Key
    val steps: Int,
    val highMovementMinutes: Int,
)
