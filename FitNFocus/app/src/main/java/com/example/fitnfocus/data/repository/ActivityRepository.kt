package com.example.fitnfocus.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitnfocus.data.local.ActivityDao
import com.example.fitnfocus.data.local.DailyActivityEntity
import com.example.fitnfocus.domain.DailyActivity
import java.time.LocalDate

/**
 * Erst mal nicht in Nutzung.
 */
class ActivityRepository(
    private val activityDao: ActivityDao
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getActivityByDate(date: LocalDate): DailyActivity? {
        return activityDao.getActivityByEpochDay(date.toEpochDay())?.toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertActivity(activity: DailyActivity) {
        activityDao.insertActivity(activity.toEntity())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateActivity(activity: DailyActivity) {
        activityDao.updateActivity(activity.toEntity())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteActivity(activity: DailyActivity) {
        activityDao.deleteActivity(activity.toEntity())
    }

    /**
     * Löscht alle Aktivitäten.
     */
    suspend fun deleteAll() {
        activityDao.deleteAll()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun DailyActivityEntity.toDomain(): DailyActivity {
    return DailyActivity(
        date = LocalDate.ofEpochDay(epochDay),
        steps = steps,
        highMovementMinutes = highMovementMinutes
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun DailyActivity.toEntity(): DailyActivityEntity {
    return DailyActivityEntity(
        epochDay = date.toEpochDay(),
        steps = steps,
        highMovementMinutes = highMovementMinutes
    )
}

