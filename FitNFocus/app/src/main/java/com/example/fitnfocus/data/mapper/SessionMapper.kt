package com.example.fitnfocus.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitnfocus.data.local.StudySessionEntity
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import java.time.LocalDate

/**
 * Mapper für StudySession ↔ StudySessionEntity.
 */
object SessionMapper {

    /**
     * Konvertiert ein StudySessionEntity zu einem StudySession Domain-Modell.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun entityToDomain(entity: StudySessionEntity): StudySession {
        val date = LocalDate.ofEpochDay(entity.epochDay)

        // Backward-Compatibility zu wahren, falls alte DB-Werte existieren:
        val parsedStatus = try {
            SessionStatus.valueOf(entity.status)
        } catch (_: Exception) {
            SessionStatus.PLANNED
        }

        return StudySession(
            id = entity.id,
            topic = entity.topic,
            durationMinutes = entity.durationMinutes,
            date = date,
            goalId = entity.goalId,
            status = parsedStatus,
            notes = entity.notes,
            elapsedSeconds = entity.elapsedSeconds
        )
    }

    /**
     * Konvertiert ein StudySession Domain-Modell zu einem StudySessionEntity.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun domainToEntity(session: StudySession): StudySessionEntity {
        return StudySessionEntity(
            id = session.id,
            topic = session.topic,
            durationMinutes = session.durationMinutes,
            epochDay = session.date.toEpochDay(),
            goalId = session.goalId,
            status = session.status.name,
            notes = session.notes,
            elapsedSeconds = session.elapsedSeconds
        )
    }
}
