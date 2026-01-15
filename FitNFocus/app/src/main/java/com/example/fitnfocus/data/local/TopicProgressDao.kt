package com.example.fitnfocus.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object für Topic-Fortschritt.
 */
@Dao
interface TopicProgressDao {

    /**
     * Alle Fortschritte als Flow.
     */
    @Query("SELECT * FROM topic_progress ORDER BY completedAt DESC")
    fun getAllProgress(): Flow<List<TopicProgressEntity>>

    /**
     * Fortschritt für ein bestimmtes Goal.
     */
    @Query("SELECT * FROM topic_progress WHERE goalId = :goalId")
    fun getProgressForGoal(goalId: Int): Flow<List<TopicProgressEntity>>

    /**
     * Fortschritt für ein bestimmtes Topic eines Goals.
     */
    @Query("SELECT * FROM topic_progress WHERE goalId = :goalId AND topicName = :topicName")
    suspend fun getProgressForTopic(goalId: Int, topicName: String): TopicProgressEntity?

    /**
     * Prüft ob ein Topic abgeschlossen ist.
     */
    @Query("SELECT isCompleted FROM topic_progress WHERE goalId = :goalId AND topicName = :topicName")
    suspend fun isTopicCompleted(goalId: Int, topicName: String): Boolean?

    /**
     * Alle abgeschlossenen Topics eines Goals.
     */
    @Query("SELECT * FROM topic_progress WHERE goalId = :goalId AND isCompleted = 1")
    suspend fun getCompletedTopicsForGoal(goalId: Int): List<TopicProgressEntity>

    /**
     * Anzahl abgeschlossener Topics für ein Goal.
     */
    @Query("SELECT COUNT(*) FROM topic_progress WHERE goalId = :goalId AND isCompleted = 1")
    suspend fun getCompletedTopicsCount(goalId: Int): Int

    /**
     * Alle abgeschlossenen Topics (für Sammelfiguren-Feature).
     */
    @Query("SELECT * FROM topic_progress WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getAllCompletedTopics(): Flow<List<TopicProgressEntity>>

    /**
     * Abgeschlossene Topics an einem bestimmten Datum.
     */
    @Query("SELECT * FROM topic_progress WHERE completedAt = :date AND isCompleted = 1")
    suspend fun getCompletedTopicsByDate(date: String): List<TopicProgressEntity>

    /**
     * Fügt einen neuen Fortschritt ein oder ersetzt existierenden.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: TopicProgressEntity): Long

    /**
     * Aktualisiert einen Fortschritt.
     */
    @Update
    suspend fun updateProgress(progress: TopicProgressEntity)

    /**
     * Löscht einen Fortschritt.
     */
    @Delete
    suspend fun deleteProgress(progress: TopicProgressEntity)

    /**
     * Löscht alle Fortschritte für ein Goal.
     */
    @Query("DELETE FROM topic_progress WHERE goalId = :goalId")
    suspend fun deleteProgressForGoal(goalId: Int)

    /**
     * Setzt den Abschluss-Status für ein Topic.
     */
    @Query("UPDATE topic_progress SET isCompleted = :isCompleted, completedAt = :completedAt WHERE goalId = :goalId AND topicName = :topicName")
    suspend fun updateCompletionStatus(goalId: Int, topicName: String, isCompleted: Boolean, completedAt: String?)
}

