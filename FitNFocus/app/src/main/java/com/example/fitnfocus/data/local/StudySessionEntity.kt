package com.example.fitnfocus.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity für eine Lernsession.
 * Eine Session ist ein konkretes "Event" - eine Lerneinheit für ein bestimmtes Topic.
 */
@Entity(
    tableName = "study_session",
    foreignKeys = [
        ForeignKey(
            entity = LearningGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.SET_NULL  // Session bleibt, aber goalId wird null
        )
    ],
    indices = [
        Index(value = ["goalId"]),
        Index(value = ["topic"]),
        Index(value = ["date"])
    ]
)
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,                 // Das Thema der Session
    val durationMinutes: Int,
    val date: String,                  // Format: dd.MM.yyyy
    val goalId: Int? = null,           // FK zu LearningGoal (nullable!)
    val status: String = "PLANNED",    // PLANNED, IN_PROGRESS, STOPPED, COMPLETED
    val notes: String = "",            // Notizen/Todos
    val elapsedSeconds: Int = 0        // Tatsächlich fokussierte Zeit (für Partial Completion)
)
