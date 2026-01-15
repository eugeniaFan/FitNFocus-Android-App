package com.example.fitnfocus.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object für Lernziele.
 */
@Dao
interface LearningGoalDao {

    @Query("SELECT * FROM learning_goal ORDER BY examDate ASC")
    fun getAllGoals(): Flow<List<LearningGoalEntity>>

    @Query("SELECT * FROM learning_goal WHERE id = :id")
    suspend fun getGoalById(id: Int): LearningGoalEntity?

    @Query("SELECT * FROM learning_goal WHERE isCompleted = 0 ORDER BY examDate ASC")
    fun getActiveGoals(): Flow<List<LearningGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: LearningGoalEntity): Long

    @Update
    suspend fun updateGoal(goal: LearningGoalEntity)

    @Delete
    suspend fun deleteGoal(goal: LearningGoalEntity)

    @Query("DELETE FROM learning_goal WHERE id = :id")
    suspend fun deleteGoalById(id: Int)

    @Query("UPDATE learning_goal SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Int, isCompleted: Boolean)
}

