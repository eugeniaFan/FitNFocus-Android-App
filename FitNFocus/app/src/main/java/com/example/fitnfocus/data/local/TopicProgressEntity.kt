package com.example.fitnfocus.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity für den Fortschritt eines Topics.
 * Unabhängig von Sessions - speichert den Abschluss-Status eines Topics.
 */
@Entity(
    tableName = "topic_progress",
    foreignKeys = [
        ForeignKey(
            entity = LearningGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE //Automatisches Löschen wenn Goal gelöscht wird
        )
    ],
    indices = [
        Index(value = ["goalId"]),
        Index(value = ["topicName", "goalId"], unique = true)
    ]
)
data class TopicProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goalId: Int,                       // FK zu LearningGoal
    val topicName: String,                 // Name des Topics
    val isCompleted: Boolean = false,      // Abgeschlossen ja/nein
    val completedAtEpochDay: Long? = null  // Datum des Abschlusses als epochDay
)

