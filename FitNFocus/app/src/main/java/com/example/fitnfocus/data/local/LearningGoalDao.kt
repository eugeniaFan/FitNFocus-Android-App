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

    @Query("SELECT * FROM learning_goal ORDER BY examEpochDay ASC")
    fun getAllGoals(): Flow<List<LearningGoalEntity>>

    @Query("SELECT * FROM learning_goal WHERE id = :id")
    suspend fun getGoalById(id: Int): LearningGoalEntity?

    @Query("SELECT * FROM learning_goal WHERE isCompleted = 0 ORDER BY examEpochDay ASC")
    fun getActiveGoals(): Flow<List<LearningGoalEntity>>

    /**
     * Fügt ein Goal ein und gibt die generierte ID zurück.
     * Bei Konflikt (gleiche ID) wird ein Fehler geworfen.
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

    /**
     * Löscht alle Lernziele.
     */
    @Query("DELETE FROM learning_goal")
    suspend fun deleteAll()
}

