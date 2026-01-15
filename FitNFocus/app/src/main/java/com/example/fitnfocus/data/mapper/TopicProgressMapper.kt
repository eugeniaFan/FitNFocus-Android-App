package com.example.fitnfocus.data.mapper

import android.os.Build
import com.example.fitnfocus.data.local.TopicProgressEntity
import com.example.fitnfocus.domain.TopicProgress
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Mapper für TopicProgress ↔ TopicProgressEntity.
 * Zentralisiert die Konvertierung zwischen Domain und Data-Layer.
 */
object TopicProgressMapper {

    private val dateFormatter: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    } else {
        null
    }

    /**
     * Konvertiert ein TopicProgressEntity zu einem TopicProgress Domain-Modell.
     */
    fun entityToDomain(entity: TopicProgressEntity): TopicProgress {
        val completedAt: LocalDate? = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            entity.completedAt != null &&
            entity.completedAt.isNotBlank()
        ) {
            try {
                LocalDate.parse(entity.completedAt, dateFormatter)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        return TopicProgress(
            id = entity.id,
            goalId = entity.goalId,
            topicName = entity.topicName,
            isCompleted = entity.isCompleted,
            completedAt = completedAt
        )
    }

    /**
     * Konvertiert ein TopicProgress Domain-Modell zu einem TopicProgressEntity.
     */
    fun domainToEntity(progress: TopicProgress): TopicProgressEntity {
        val completedAtString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            progress.completedAt?.format(dateFormatter)
        } else {
            null
        }

        return TopicProgressEntity(
            id = progress.id,
            goalId = progress.goalId,
            topicName = progress.topicName,
            isCompleted = progress.isCompleted,
            completedAt = completedAtString
        )
    }

    /**
     * Extension-Funktion für bequeme Konvertierung.
     */
    fun TopicProgressEntity.toDomain(): TopicProgress = entityToDomain(this)

    /**
     * Extension-Funktion für bequeme Konvertierung.
     */
    fun TopicProgress.toEntity(): TopicProgressEntity = domainToEntity(this)
}

/**
 * Extension-Funktionen für Listen.
 */
fun List<TopicProgressEntity>.toDomainList(): List<TopicProgress> =
    map { TopicProgressMapper.entityToDomain(it) }

fun List<TopicProgress>.toEntityList(): List<TopicProgressEntity> =
    map { TopicProgressMapper.domainToEntity(it) }

