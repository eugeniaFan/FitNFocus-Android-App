package com.example.fitnfocus.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitnfocus.data.local.StudySessionEntity
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Mapper für StudySession ↔ StudySessionEntity.
 * Zentralisiert die Konvertierung zwischen Domain und Data-Layer.
 *
 * HINWEIS: Dieser Mapper benötigt API 26+ (LocalDate).
 * Für ältere APIs müsste eine alternative Implementierung verwendet werden.
 */
object SessionMapper {

    private val dateFormatter: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    } else {
        null
    }

    /**
     * Konvertiert ein StudySessionEntity zu einem StudySession Domain-Modell.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun entityToDomain(entity: StudySessionEntity): StudySession {
        val parsedDate: LocalDate = try {
            LocalDate.parse(entity.date, dateFormatter)
        } catch (_: Exception) {
            LocalDate.now()
        }

        val parsedStatus = try {
            SessionStatus.valueOf(entity.status)
        } catch (_: Exception) {
            SessionStatus.PLANNED
        }

        return StudySession(
            id = entity.id,
            topic = entity.topic,
            durationMinutes = entity.durationMinutes,
            date = parsedDate,
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
        val dateString = session.date.format(dateFormatter)

        return StudySessionEntity(
            id = session.id,
            topic = session.topic,
            durationMinutes = session.durationMinutes,
            date = dateString,
            goalId = session.goalId,
            status = session.status.name,
            notes = session.notes,
            elapsedSeconds = session.elapsedSeconds
        )
    }

    /**
     * Extension-Funktion für bequeme Konvertierung.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun StudySessionEntity.toDomain(): StudySession = entityToDomain(this)

    /**
     * Extension-Funktion für bequeme Konvertierung.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun StudySession.toEntity(): StudySessionEntity = domainToEntity(this)
}

/**
 * Extension-Funktionen für Listen.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun List<StudySessionEntity>.toDomainList(): List<StudySession> =
    map { SessionMapper.entityToDomain(it) }

@RequiresApi(Build.VERSION_CODES.O)
fun List<StudySession>.toEntityList(): List<StudySessionEntity> =
    map { SessionMapper.domainToEntity(it) }

