package com.example.fitnfocus.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitnfocus.data.local.LearningGoalDao
import com.example.fitnfocus.domain.LearningGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.fitnfocus.data.mapper.LearningGoalMapper


/**
 * Repository für Lernziele.
 * Übersetzt zwischen Domain-Modellen (LocalDate) und Room-Entities (epochDay).
 */
class LearningGoalRepository(private val learningGoalDao: LearningGoalDao) {

    /**
     * Nur aktive (nicht abgeschlossene) Lernziele.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getActiveGoals(): Flow<List<LearningGoal>> =
        learningGoalDao.getActiveGoals()
            .map { entities ->
                entities.map { LearningGoalMapper.entityToDomain(it) }
            }

    /**
     * Einzelnes Lernziel nach ID laden.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGoalById(id: Int): LearningGoal? {
        return learningGoalDao.getGoalById(id)?.let {  LearningGoalMapper.entityToDomain(it) }
    }

    /**
     * Neues Lernziel speichern.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertGoal(goal: LearningGoal): Long {
        return learningGoalDao.insertGoal(LearningGoalMapper.domainToEntity(goal))
    }

    /**
     * Lernziel aktualisieren.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateGoal(goal: LearningGoal) {
        learningGoalDao.updateGoal(LearningGoalMapper.domainToEntity(goal))
    }

    /**
     * Lernziel löschen.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteGoal(goal: LearningGoal) {
        learningGoalDao.deleteGoal(LearningGoalMapper.domainToEntity(goal))
    }

    /**
     * Lernziel als abgeschlossen markieren.
     */
    suspend fun markGoalCompleted(id: Int, isCompleted: Boolean) {
        learningGoalDao.updateCompletionStatus(id, isCompleted)
    }

    /**
     * Löscht alle Lernziele.
     */
    suspend fun deleteAll() {
        learningGoalDao.deleteAll()
    }
}
