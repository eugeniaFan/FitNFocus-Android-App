package com.example.fitnfocus.data.mapper

import com.example.fitnfocus.data.local.StudySessionEntity
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import java.time.LocalDate

/**
 * Mapper between StudySession domain model and StudySessionEntity.
 * Handles conversion of LocalDate to epochDay and status enum parsing.
 */
object SessionMapper {

    fun entityToDomain(entity: StudySessionEntity): StudySession {
        val date = LocalDate.ofEpochDay(entity.epochDay)

        // Preserve backward compatibility for legacy database values
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
