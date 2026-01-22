package com.example.fitnfocus.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity für eine Lernsession.
 * Eine Session(Event) ist eine Lerneinheit für ein bestimmtes Topic (Thema).
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
        Index(value = ["epochDay"])
    ]
)
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,                 // Das Thema der Session
    val durationMinutes: Int,          // Dauer der Session in Minuten
    val epochDay: Long,                // Datum als epochDay (Tage seit 1970-01-01)
    val goalId: Int? = null,           // FK zu LearningGoal (nullable!)
    val status: String = "PLANNED",    // PLANNED, IN_PROGRESS, STOPPED, COMPLETED
    val notes: String = "",            // Notizen/ Todos
    val elapsedSeconds: Int = 0        // Tatsächlich fokussierte Zeit (für Partial Completion)
)
