package com.example.fitnfocus.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for study sessions.
 * Represents concrete learning events for specific topics with optional goal association.
 */
@Entity(
    tableName = "study_session",
    foreignKeys = [
        ForeignKey(
            entity = LearningGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.SET_NULL
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
    val topic: String,
    val durationMinutes: Int,
    val epochDay: Long,
    val goalId: Int? = null,
    val status: String = "PLANNED",
    val notes: String = "",
    val elapsedSeconds: Int = 0
)
