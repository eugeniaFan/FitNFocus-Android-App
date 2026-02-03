package com.example.fitnfocus.data.repository

import com.example.fitnfocus.data.local.LearningGoalDao
import com.example.fitnfocus.domain.LearningGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.fitnfocus.data.mapper.LearningGoalMapper

/**
 * Repository for learning goals.
 * Manages CRUD operations and maps between domain models and database entities.
 */
class LearningGoalRepository(private val learningGoalDao: LearningGoalDao) {

    // Getting all reactive updated goals
    fun getActiveGoals(): Flow<List<LearningGoal>> =
        learningGoalDao.getActiveGoals()
            .map { entities ->
                entities.map { LearningGoalMapper.entityToDomain(it) }
            }

    suspend fun getGoalById(id: Int): LearningGoal? {
        return learningGoalDao.getGoalById(id)?.let { LearningGoalMapper.entityToDomain(it) }
    }

    suspend fun insertGoal(goal: LearningGoal): Long {
        return learningGoalDao.insertGoal(LearningGoalMapper.domainToEntity(goal))
    }

    suspend fun updateGoal(goal: LearningGoal) {
        learningGoalDao.updateGoal(LearningGoalMapper.domainToEntity(goal))
    }

    suspend fun deleteGoal(goal: LearningGoal) {
        learningGoalDao.deleteGoal(LearningGoalMapper.domainToEntity(goal))
    }

    suspend fun deleteAll() {
        learningGoalDao.deleteAll()
    }
}
