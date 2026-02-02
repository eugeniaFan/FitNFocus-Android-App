package com.example.fitnfocus.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for topic progress tracking.
 * Tracks completion status of individual topics independent of sessions.
 */
@Entity(
    tableName = "topic_progress",
    foreignKeys = [
        ForeignKey(
            entity = LearningGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["goalId"]),
        Index(value = ["topicName", "goalId"], unique = true)
    ]
)
data class TopicProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goalId: Int,
    val topicName: String,
    val isCompleted: Boolean = false,
    val completedAtEpochDay: Long? = null
)

