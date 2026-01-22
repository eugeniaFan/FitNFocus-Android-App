package com.example.fitnfocus.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitnfocus.data.local.TopicProgressDao
import com.example.fitnfocus.data.mapper.TopicProgressMapper
import com.example.fitnfocus.domain.TopicProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Repository für Topic-Fortschritt.
 * Verwaltet den Abschluss-Status von Topics unabhängig von Sessions.
 * Domain nutzt LocalDate, Entity nutzt epochDay.
 */
class TopicProgressRepository(
    private val topicProgressDao: TopicProgressDao
) {

    /**
     * Prüft ob ein Topic abgeschlossen ist.
     */
    suspend fun isTopicCompleted(goalId: Int, topicName: String): Boolean {
        return topicProgressDao.isTopicCompleted(goalId, topicName) ?: false
    }


    /**
     * Alle abgeschlossenen Topics (für Sammelfiguren-Feature).
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllCompletedTopics(): Flow<List<TopicProgress>> {
        return topicProgressDao.getAllCompletedTopics().map { entities ->
            entities.map { TopicProgressMapper.entityToDomain(it) }
        }
    }


    /**
     * Markiert ein Topic als abgeschlossen.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun markTopicCompleted(
        goalId: Int,
        topicName: String,
        isCompleted: Boolean
    ) {

        val existingEntity = topicProgressDao.getProgressForTopic(goalId, topicName)

        if (existingEntity != null) {
            // Update bestehenden Domain-Zustand
            val existingDomain = TopicProgressMapper.entityToDomain(existingEntity)

            val completedAtStable = when {
                // Wenn es schon ein Datum gibt: immer behalten
                existingDomain.completedAt != null -> existingDomain.completedAt

                // Wenn es noch kein Datum gibt, aber jetzt completed wird: heute setzen
                isCompleted -> LocalDate.now()

                // sonst (noch kein Datum, und wird nicht completed): bleibt null
                else -> null
            }

            val domainProgress = existingDomain.copy(
                isCompleted = isCompleted,
                completedAt = completedAtStable
            )
            updateProgress(domainProgress)
        } else {
            // Neuen Domain-Zustand erzeugen
            val newDomain = TopicProgress(
                id = 0, // wird von Room generiert
                goalId = goalId,
                topicName = topicName,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) LocalDate.now() else null
            )
            insertProgress(newDomain)
        }
    }

    /**
     * Erstellt einen neuen Topic-Fortschritt.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertProgress(progress: TopicProgress): Long {
        return topicProgressDao.insertProgress(TopicProgressMapper.domainToEntity(progress))
    }

    /**
     * Aktualisiert einen Topic-Fortschritt.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateProgress(progress: TopicProgress) {
        topicProgressDao.updateProgress(TopicProgressMapper.domainToEntity(progress))
    }


    /**
     * Löscht alle Fortschritte für ein Goal.
     */
    suspend fun deleteProgressForGoal(goalId: Int) {
        topicProgressDao.deleteProgressForGoal(goalId)
    }
}

