package com.example.fitnfocus.data.local

import androidx.room.PrimaryKey
import androidx.room.Entity

/**
 * Room entity for learning goals.
 * Stores exam date as epochDay for consistent date handling.
 */
@Entity(tableName = "learning_goal")
data class LearningGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val moduleName: String,
    val topics: String,
    val isCompleted: Boolean = false,
    val examEpochDay: Long? = null
)