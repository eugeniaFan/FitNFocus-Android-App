package com.example.fitnfocus.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitnfocus.data.local.TopicProgressEntity
import com.example.fitnfocus.domain.TopicProgress
import java.time.LocalDate

/**
 * Mapper für TopicProgress ↔ TopicProgressEntity.
 * Zentralisiert die Konvertierung zwischen Domain und Data-Layer.
 */
object TopicProgressMapper {

    /**
     * Konvertiert ein TopicProgressEntity zu einem TopicProgress Domain-Modell.
     */
    @RequiresApi(Build.VERSION_CODES.O)
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

    /**
     * Konvertiert ein TopicProgress Domain-Modell zu einem TopicProgressEntity.
     */
    @RequiresApi(Build.VERSION_CODES.O)
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