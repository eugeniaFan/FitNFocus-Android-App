package com.example.fitnfocus.data.repository

import android.os.Build
import com.example.fitnfocus.data.local.TopicProgressDao
import com.example.fitnfocus.data.local.TopicProgressEntity
import com.example.fitnfocus.data.mapper.TopicProgressMapper
import com.example.fitnfocus.data.mapper.toDomainList
import com.example.fitnfocus.domain.TopicProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Repository für Topic-Fortschritt.
 * Verwaltet den Abschluss-Status von Topics unabhängig von Sessions.
 */
class TopicProgressRepository(
    private val topicProgressDao: TopicProgressDao
) {
    private val dateFormatter: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    } else {
        null
    }

    /**
     * Alle Fortschritte als Flow.
     */
    fun getAllProgress(): Flow<List<TopicProgress>> {
        return topicProgressDao.getAllProgress().map { it.toDomainList() }
    }

    /**
     * Fortschritt für ein bestimmtes Goal als Flow.
     */
    fun getProgressForGoal(goalId: Int): Flow<List<TopicProgress>> {
        return topicProgressDao.getProgressForGoal(goalId).map { it.toDomainList() }
    }

    /**
     * Fortschritt für ein bestimmtes Topic eines Goals.
     */
    suspend fun getProgressForTopic(goalId: Int, topicName: String): TopicProgress? {
        return topicProgressDao.getProgressForTopic(goalId, topicName)
            ?.let { TopicProgressMapper.entityToDomain(it) }
    }

    /**
     * Prüft ob ein Topic abgeschlossen ist.
     */
    suspend fun isTopicCompleted(goalId: Int, topicName: String): Boolean {
        return topicProgressDao.isTopicCompleted(goalId, topicName) ?: false
    }

    /**
     * Alle abgeschlossenen Topics eines Goals.
     */
    suspend fun getCompletedTopicsForGoal(goalId: Int): List<TopicProgress> {
        return topicProgressDao.getCompletedTopicsForGoal(goalId).toDomainList()
    }

    /**
     * Anzahl abgeschlossener Topics für ein Goal.
     */
    suspend fun getCompletedTopicsCount(goalId: Int): Int {
        return topicProgressDao.getCompletedTopicsCount(goalId)
    }

    /**
     * Alle abgeschlossenen Topics (für Sammelfiguren-Feature).
     */
    fun getAllCompletedTopics(): Flow<List<TopicProgress>> {
        return topicProgressDao.getAllCompletedTopics().map { it.toDomainList() }
    }

    /**
     * Abgeschlossene Topics an einem bestimmten Datum.
     */
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCompletedTopicsByDate(date: LocalDate): List<TopicProgress> {
        val dateString = formatDate(date)
        return topicProgressDao.getCompletedTopicsByDate(dateString).toDomainList()
    }

    /**
     * Markiert ein Topic als abgeschlossen.
     */
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    suspend fun markTopicCompleted(goalId: Int, topicName: String, isCompleted: Boolean) {
        val existingProgress = topicProgressDao.getProgressForTopic(goalId, topicName)
        val completedAt = if (isCompleted) formatDate(LocalDate.now()) else null

        if (existingProgress != null) {
            // Update existing
            topicProgressDao.updateCompletionStatus(goalId, topicName, isCompleted, completedAt)
        } else {
            // Insert new
            val newProgress = TopicProgressEntity(
                goalId = goalId,
                topicName = topicName,
                isCompleted = isCompleted,
                completedAt = completedAt
            )
            topicProgressDao.insertProgress(newProgress)
        }
    }

    /**
     * Erstellt einen neuen Topic-Fortschritt.
     */
    suspend fun insertProgress(progress: TopicProgress): Long {
        return topicProgressDao.insertProgress(TopicProgressMapper.domainToEntity(progress))
    }

    /**
     * Aktualisiert einen Topic-Fortschritt.
     */
    suspend fun updateProgress(progress: TopicProgress) {
        topicProgressDao.updateProgress(TopicProgressMapper.domainToEntity(progress))
    }

    /**
     * Löscht einen Topic-Fortschritt.
     */
    suspend fun deleteProgress(progress: TopicProgress) {
        topicProgressDao.deleteProgress(TopicProgressMapper.domainToEntity(progress))
    }

    /**
     * Löscht alle Fortschritte für ein Goal.
     */
    suspend fun deleteProgressForGoal(goalId: Int) {
        topicProgressDao.deleteProgressForGoal(goalId)
    }

    /**
     * Hilfsfunktion zum Formatieren eines LocalDate.
     */
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private fun formatDate(date: LocalDate): String {
        return if (dateFormatter != null) {
            date.format(dateFormatter)
        } else {
            "${date.dayOfMonth}.${date.monthValue}.${date.year}"
        }
    }
}

