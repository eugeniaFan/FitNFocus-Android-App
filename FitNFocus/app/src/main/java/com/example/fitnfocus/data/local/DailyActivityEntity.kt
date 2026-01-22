package com.example.fitnfocus.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Erst mal nicht in Nutzung.
 * Hinweis: epochDay als PrimaryKey für konsistente Sortierung.
 */
@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey val epochDay: Long,  // Datum als epochDay (Tage seit 1970-01-01)
    val steps: Int,
    val highMovementMinutes: Int,
)
