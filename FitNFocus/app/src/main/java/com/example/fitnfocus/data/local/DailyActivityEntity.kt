package com.example.fitnfocus.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey val date: String,
    val steps: Int,
    val highMovementMinutes: Int,
)
