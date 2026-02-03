package com.example.fitnfocus.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for topic progress tracking.
 * Manages completion status and metadata for individual learning topics.
 */
@Dao
interface TopicProgressDao {

    @Query("SELECT * FROM topic_progress ORDER BY completedAtEpochDay DESC")
    fun getAllProgress(): Flow<List<TopicProgressEntity>>

    @Query("SELECT * FROM topic_progress WHERE goalId = :goalId")
    fun getProgressForGoal(goalId: Int): Flow<List<TopicProgressEntity>>

    @Query("SELECT * FROM topic_progress WHERE goalId = :goalId AND topicName = :topicName")
    suspend fun getProgressForTopic(goalId: Int, topicName: String): TopicProgressEntity?

    @Query("SELECT isCompleted FROM topic_progress WHERE goalId = :goalId AND topicName = :topicName")
    suspend fun isTopicCompleted(goalId: Int, topicName: String): Boolean?

    @Query("SELECT * FROM topic_progress WHERE goalId = :goalId AND isCompleted = 1")
    suspend fun getCompletedTopicsForGoal(goalId: Int): List<TopicProgressEntity>

    @Query("SELECT COUNT(*) FROM topic_progress WHERE goalId = :goalId AND isCompleted = 1")
    suspend fun getCompletedTopicsCount(goalId: Int): Int

    @Query("SELECT * FROM topic_progress WHERE isCompleted = 1 ORDER BY completedAtEpochDay DESC")
    fun getAllCompletedTopics(): Flow<List<TopicProgressEntity>>

    @Query("SELECT * FROM topic_progress WHERE completedAtEpochDay = :epochDay AND isCompleted = 1")
    suspend fun getCompletedTopicsByEpochDay(epochDay: Long): List<TopicProgressEntity>

    /**
     * Inserts or updates topic progress.
     * Uses REPLACE strategy for upsert behavior based on unique index.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: TopicProgressEntity): Long

    @Update
    suspend fun updateProgress(progress: TopicProgressEntity)

    @Delete
    suspend fun deleteProgress(progress: TopicProgressEntity)

    @Query("DELETE FROM topic_progress WHERE goalId = :goalId")
    suspend fun deleteProgressForGoal(goalId: Int)

    @Query(
        """
        UPDATE topic_progress 
        SET isCompleted = :isCompleted, completedAtEpochDay = :completedAtEpochDay 
        WHERE goalId = :goalId 
        AND topicName = :topicName
        """
    )
    suspend fun updateCompletionStatus(
        goalId: Int,
        topicName: String,
        isCompleted: Boolean,
        completedAtEpochDay: Long?
    )
}
