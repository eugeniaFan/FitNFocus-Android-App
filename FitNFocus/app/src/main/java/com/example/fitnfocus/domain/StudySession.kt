package com.example.fitnfocus.domain

data class StudySession(
    val id: Int,
    val subject: String,
    val durationMinutes: Int,
    val date: String
)