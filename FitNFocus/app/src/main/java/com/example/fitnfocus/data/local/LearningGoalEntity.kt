package com.example.fitnfocus.data.local

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "learning_goal")
data class LearningGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val moduleName: String,
    val topics: String,                // Themen der Lernziele
    val isCompleted: Boolean = false,
    val examEpochDay: Long? = null,    // Prüfungsdatum als epochDay (Tage seit 1970-01-01)
)
