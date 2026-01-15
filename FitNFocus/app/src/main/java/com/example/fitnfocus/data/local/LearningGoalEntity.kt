package com.example.fitnfocus.data.local

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "learning_goal")
data class LearningGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val moduleName: String,
    val topics: String,           // Komma-separiert: "Thema1,Thema2"
    val isCompleted: Boolean = false,
    val examDate: String? = null,
)
