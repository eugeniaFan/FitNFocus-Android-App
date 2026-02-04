package com.example.fitnfocus.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for learning goals.
 * Provides database operations for managing learning goals with their progress and exam dates.
 */
@Dao
interface LearningGoalDao {

    @Query("SELECT * FROM learning_goal ORDER BY examEpochDay ASC")
    fun getAllGoals(): Flow<List<LearningGoalEntity>>

    @Query("SELECT * FROM learning_goal WHERE id = :id")
    suspend fun getGoalById(id: Int): LearningGoalEntity?

    @Query("SELECT * FROM learning_goal WHERE isCompleted = 0 ORDER BY examEpochDay ASC")
    fun getActiveGoals(): Flow<List<LearningGoalEntity>>

    /**
     * Inserts a new learning goal and returns the generated ID.
     * Throws an exception if a goal with the same ID already exists.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGoal(goal: LearningGoalEntity): Long

    @Update
    suspend fun updateGoal(goal: LearningGoalEntity)

    @Delete
    suspend fun deleteGoal(goal: LearningGoalEntity)

    @Query("DELETE FROM learning_goal WHERE id = :id")
    suspend fun deleteGoalById(id: Int)

    @Query("UPDATE learning_goal SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Int, isCompleted: Boolean)

    @Query("DELETE FROM learning_goal")
    suspend fun deleteAll()
}
