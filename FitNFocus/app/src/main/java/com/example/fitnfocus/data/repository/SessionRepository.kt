package com.example.fitnfocus.data.repository

import com.example.fitnfocus.data.local.SessionDao
import com.example.fitnfocus.data.mapper.SessionMapper
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Repository for study sessions.
 * Manages learning events with status tracking, notes, and time recording.
 */
class SessionRepository(
    private val sessionDao: SessionDao
) {

    suspend fun getSessionById(id: Int): StudySession? {
        return sessionDao.getSessionById(id)
            ?.let { SessionMapper.entityToDomain(it) }
    }

    suspend fun getSessionsByDate(date: LocalDate): List<StudySession> {
        return sessionDao
            .getSessionsByEpochDay(date.toEpochDay())
            .map { entity -> SessionMapper.entityToDomain(entity) }
    }

    fun getAllSessions(): Flow<List<StudySession>> {
        return sessionDao.getAllSessions()
            .map { entities -> entities.map { entity -> SessionMapper.entityToDomain(entity) } }
    }

    fun getSessionsForTopicFlow(topic: String, goalId: Int): Flow<List<StudySession>> {
        return sessionDao.getSessionsForTopicFlow(topic, goalId)
            .map { entities -> entities.map { entity -> SessionMapper.entityToDomain(entity) } }
    }

    suspend fun hasSessionsForTopic(topic: String, goalId: Int): Boolean {
        return sessionDao.hasSessionsForTopic(topic, goalId)
    }

    suspend fun updateSessionStatus(sessionId: Int, status: SessionStatus) {
        sessionDao.updateSessionStatus(sessionId, status.name)
    }

    suspend fun updateSessionNotes(sessionId: Int, notes: String) {
        sessionDao.updateSessionNotes(sessionId, notes)
    }

    /**
     * Appends new notes to existing notes with line break separator.
     */
    suspend fun appendSessionNotes(sessionId: Int, newNotes: String) {
        val existingNotes = sessionDao.getSessionNotes(sessionId).orEmpty()
        val combinedNotes = if (existingNotes.isNotBlank()) {
            "$existingNotes\n\n$newNotes"
        } else {
            newNotes
        }
        sessionDao.updateSessionNotes(sessionId, combinedNotes)
    }

    suspend fun updateElapsedSeconds(sessionId: Int, elapsedSeconds: Int) {
        sessionDao.updateElapsedSeconds(sessionId, elapsedSeconds)
    }

    /**
     * Marks all open sessions for a topic as completed.
     * Used when a topic is marked as complete.
     */
    suspend fun completeAllSessionsForTopic(topic: String, goalId: Int) {
        sessionDao.completeAllSessionsForTopic(topic, goalId)
    }

    suspend fun deleteAll() {
        sessionDao.deleteAll()
    }

    suspend fun insertSession(session: StudySession): Long {
        return sessionDao.insertSession(SessionMapper.domainToEntity(session))
    }

    suspend fun updateSession(session: StudySession) {
        sessionDao.updateSession(SessionMapper.domainToEntity(session))
    }

    suspend fun deleteSession(session: StudySession) {
        sessionDao.deleteSession(SessionMapper.domainToEntity(session))
    }

    suspend fun deleteSessionsForGoal(goalId: Int) {
        sessionDao.deleteSessionsForGoal(goalId)
    }
}
