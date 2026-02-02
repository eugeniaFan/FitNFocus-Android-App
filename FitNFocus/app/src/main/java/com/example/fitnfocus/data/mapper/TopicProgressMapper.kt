package com.example.fitnfocus.data.mapper

import com.example.fitnfocus.data.local.TopicProgressEntity
import com.example.fitnfocus.domain.TopicProgress
import java.time.LocalDate

/**
 * Mapper between TopicProgress domain model and TopicProgressEntity.
 * Handles conversion of LocalDate to epochDay for database storage.
 */
object TopicProgressMapper {

    fun entityToDomain(entity: TopicProgressEntity): TopicProgress {
        val completedAt: LocalDate? = entity.completedAtEpochDay?.let {
            LocalDate.ofEpochDay(it)
        }

        return TopicProgress(
            id = entity.id,
            goalId = entity.goalId,
            topicName = entity.topicName,
            isCompleted = entity.isCompleted,
            completedAt = completedAt
        )
    }

    fun domainToEntity(progress: TopicProgress): TopicProgressEntity {
        return TopicProgressEntity(
            id = progress.id,
            goalId = progress.goalId,
            topicName = progress.topicName,
            isCompleted = progress.isCompleted,
            completedAtEpochDay = progress.completedAt?.toEpochDay()
        )
    }
}