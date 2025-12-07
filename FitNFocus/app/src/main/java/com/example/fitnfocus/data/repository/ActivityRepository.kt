package com.example.fitnfocus.data.repository

import com.example.fitnfocus.data.local.ActivityDao
import com.example.fitnfocus.data.local.DailyActivityEntity
import com.example.fitnfocus.domain.DailyActivity


class ActivityRepository(
    private val activityDao: ActivityDao
) {
     suspend fun getActivityByDate(date: String): DailyActivity? {
        return activityDao.getActivityByDate(date)?.toDomain()
    }

    suspend fun insertActivity(activity: DailyActivity) {
        activityDao.insertActivity(activity.toEntity())
    }

    suspend fun updateActivity(activity: DailyActivity) {
        activityDao.updateActivity(activity.toEntity()  )
    }

    suspend fun deleteActivity(activity: DailyActivity) {
        activityDao.deleteActivity(activity.toEntity())
    }
}

private fun DailyActivityEntity.toDomain(): DailyActivity {
    return DailyActivity(
        date = date,
        steps = steps,
        highMovementMinutes = highMovementMinutes
    )
}
private fun DailyActivity.toEntity(): DailyActivityEntity {
    return DailyActivityEntity(
        date = date,
        steps = steps,
        highMovementMinutes = highMovementMinutes
    )
}

