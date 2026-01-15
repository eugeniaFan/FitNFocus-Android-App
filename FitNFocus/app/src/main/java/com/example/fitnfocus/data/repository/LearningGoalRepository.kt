package com.example.fitnfocus.data.repository

import android.os.Build
import com.example.fitnfocus.data.local.LearningGoalDao
import com.example.fitnfocus.data.local.LearningGoalEntity
import com.example.fitnfocus.domain.LearningGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Repository für Lernziele.
 * Übersetzt zwischen Domain-Modellen und Room-Entities.
 */
class LearningGoalRepository(private val learningGoalDao: LearningGoalDao) {

    private val germanDateFormatter: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    } else {
        null
    }

    /**
     * Alle Lernziele als Flow.
     */
    fun getAllGoals(): Flow<List<LearningGoal>> = learningGoalDao.getAllGoals().map { entities ->
        entities.map { entityToDomain(it) }
    }

    /**
     * Nur aktive (nicht abgeschlossene) Lernziele.
     */
    fun getActiveGoals(): Flow<List<LearningGoal>> = learningGoalDao.getActiveGoals().map { entities ->
        entities.map { entityToDomain(it) }
    }

    /**
     * Einzelnes Lernziel nach ID laden.
     */
    suspend fun getGoalById(id: Int): LearningGoal? {
        return learningGoalDao.getGoalById(id)?.let { entityToDomain(it) }
    }

    /**
     * Neues Lernziel speichern.
     */
    suspend fun insertGoal(goal: LearningGoal): Long {
        return learningGoalDao.insertGoal(domainToEntity(goal))
    }

    /**
     * Lernziel aktualisieren.
     */
    suspend fun updateGoal(goal: LearningGoal) {
        learningGoalDao.updateGoal(domainToEntity(goal))
    }

    /**
     * Lernziel löschen.
     */
    suspend fun deleteGoal(goal: LearningGoal) {
        learningGoalDao.deleteGoal(domainToEntity(goal))
    }

    /**
     * Lernziel als abgeschlossen markieren.
     */
    suspend fun markGoalCompleted(id: Int, isCompleted: Boolean) {
        learningGoalDao.updateCompletionStatus(id, isCompleted)
    }

    // Mapping-Funktionen

    private fun entityToDomain(entity: LearningGoalEntity): LearningGoal {
        val topicsList: List<String> = if (entity.topics.isNotBlank()) {
            entity.topics.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            emptyList()
        }

        // examDate nur auf API 26+ als LocalDate parsen (Format dd.MM.yyyy)
        val examDate: LocalDate? = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            entity.examDate != null &&
            entity.examDate.isNotBlank()
        ) {
            try {
                LocalDate.parse(entity.examDate, germanDateFormatter)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        return LearningGoal(
            id = entity.id,
            moduleName = entity.moduleName,
            topics = topicsList,
            examDate = examDate,
            isCompleted = entity.isCompleted
        )
    }

    private fun domainToEntity(goal: LearningGoal): LearningGoalEntity {
        val examDateString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            goal.examDate?.format(germanDateFormatter)
        } else {
            null
        }

        return LearningGoalEntity(
            id = goal.id,
            moduleName = goal.moduleName,
            topics = goal.topics.joinToString(","),
            examDate = examDateString,
            isCompleted = goal.isCompleted
        )
    }
}
