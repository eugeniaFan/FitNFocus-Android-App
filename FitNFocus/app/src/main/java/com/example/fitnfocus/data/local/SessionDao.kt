package com.example.fitnfocus.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitnfocus.domain.SessionStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for study sessions.
 * Provides database operations for managing learning events with various status and filters.
 */
@Dao
interface SessionDao {
    @Query("SELECT * FROM study_session ORDER BY epochDay DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_session WHERE id = :id")
    suspend fun getSessionById(id: Int): StudySessionEntity?

    @Query("SELECT * FROM study_session WHERE epochDay = :epochDay ORDER BY id DESC")
    suspend fun getSessionsByEpochDay(epochDay: Long): List<StudySessionEntity>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_session WHERE epochDay = :epochDay")
    suspend fun getTotalMinutesByEpochDay(epochDay: Long): Int

    @Query("SELECT * FROM study_session WHERE topic = :topic ORDER BY epochDay DESC")
    suspend fun getSessionsByTopic(topic: String): List<StudySessionEntity>

    @Query(
        """
        SELECT * FROM study_session 
        WHERE topic = :topic AND goalId = :goalId ORDER BY epochDay DESC 
        """
    )
    fun getSessionsForTopicFlow(topic: String, goalId: Int): Flow<List<StudySessionEntity>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM study_session 
            WHERE topic = :topic AND goalId = :goalId 
        )
    """
    )
    suspend fun hasSessionsForTopic(topic: String, goalId: Int): Boolean

    @Query("SELECT DISTINCT topic FROM study_session")
    suspend fun getAllTopicsWithSessions(): List<String>

    @Query(
        """
        SELECT * FROM study_session 
        WHERE epochDay = :epochDay
        AND status = 'COMPLETED' 
        ORDER BY id DESC
        """
    )
    suspend fun getCompletedSessionsByEpochDay(epochDay: Long): List<StudySessionEntity>

    @Query(
        """
        SELECT * FROM study_session 
        WHERE status = 'PLANNED'    
        ORDER BY epochDay ASC, id ASC
    """
    )
    suspend fun getAllPlannedSessions(): List<StudySessionEntity>

    @Query("UPDATE study_session SET status = :status WHERE id = :id")
    suspend fun updateSessionStatus(id: Int, status: String)

    @Query("UPDATE study_session SET notes = :notes WHERE id = :id")
    suspend fun updateSessionNotes(id: Int, notes: String)

    @Query("SELECT notes FROM study_session WHERE id = :id")
    suspend fun getSessionNotes(id: Int): String?

    @Query("UPDATE study_session SET elapsedSeconds = :elapsedSeconds WHERE id = :id")
    suspend fun updateElapsedSeconds(id: Int, elapsedSeconds: Int)

    /**
     * Marks all open sessions for a topic as completed.
     * Used when a topic is marked as completed.
     */
    @Query(
        """
        UPDATE study_session 
        SET status = 'COMPLETED' 
        WHERE topic = :topic 
        AND goalId = :goalId
        AND status != :completedStatus
    """
    )
    suspend fun completeAllSessionsForTopic(
        topic: String, goalId: Int, completedStatus: SessionStatus = SessionStatus.COMPLETED
    )

    /**
     * Inserts a new session and returns the generated ID.
     * Throws an exception if a session with the same ID already exists.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSession(session: StudySessionEntity): Long

    @Update
    suspend fun updateSession(session: StudySessionEntity)

    @Delete
    suspend fun deleteSession(session: StudySessionEntity)

    @Query("DELETE FROM study_session WHERE goalId = :goalId")
    suspend fun deleteSessionsForGoal(goalId: Int)

    @Query("DELETE FROM study_session")
    suspend fun deleteAll()
}