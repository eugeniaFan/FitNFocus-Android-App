package com.example.fitnfocus.data.repository

import com.example.fitnfocus.data.local.StudyDao
import com.example.fitnfocus.data.local.StudySessionEntity
import com.example.fitnfocus.domain.StudySession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class StudyRepository(
    private val studyDao: StudyDao
) {
    suspend fun getStudyById(id: Int): StudySession? {
        return studyDao.getStudySessionsById(id)?.toDomain()
    }
    suspend fun getStudySessionsByDate(date: String): List<StudySession> {
        return studyDao.getStudySessionsByDate(date).map { it.toDomain() }
    }
    fun getAllStudySessions(): Flow<List<StudySession>> {
        return studyDao.getAllStudySessions().map { list ->
            list.map { it.toDomain() }
        }
    }
    suspend fun getTotalMinutesByDate(date: String): Int {
        return studyDao.getTotalMinutesByDate(date)
    }

    suspend fun insertStudySession(session: StudySession) {
        studyDao.insertStudySession(
            session.toEntity()
        )
    }
    suspend fun updateStudySession(session: StudySession) {
        studyDao.updateStudySession(session.toEntity())
    }
    suspend fun deleteStudySession(session: StudySession) {
        studyDao.deleteStudySession(session.toEntity())
    }
}

// Diese Funktion wandelt das Domain Modell in das Room Entity um
private fun StudySession.toEntity(): StudySessionEntity {
    return StudySessionEntity(
        id = id,
        subject = subject,
        durationMinutes = durationMinutes,
        date = date
    )
}

// Diese Funktion wandelt das Room Entity in das Domain-Modell um
private fun StudySessionEntity.toDomain(): StudySession {
    return StudySession(
        id = id,
        subject = subject,
        durationMinutes = durationMinutes,
        date = date
    )
}
