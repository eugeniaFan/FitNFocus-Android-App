package com.example.fitnfocus.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ActivityDao {
    @Query("SELECT * FROM daily_activity WHERE date = :date")
    suspend fun getActivityByDate(date: String): DailyActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: DailyActivityEntity)

    @Update
    suspend fun updateActivity(activity: DailyActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: DailyActivityEntity)

}