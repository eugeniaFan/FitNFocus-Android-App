package com.example.fitnfocus.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_session")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val durationMinutes: Int,
    val date: String
)
