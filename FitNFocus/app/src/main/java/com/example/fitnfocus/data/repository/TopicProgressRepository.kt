package com.example.fitnfocus.data.repository

import android.util.Log
import com.example.fitnfocus.data.local.LearningGoalDao
import com.example.fitnfocus.data.local.TopicProgressDao
import com.example.fitnfocus.data.mapper.TopicProgressMapper
import com.example.fitnfocus.domain.TopicProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Repository class for managing topic progress data.
 *
 * This class provides an API for the app to interact with the topic progress data,
 * including marking topics as completed, retrieving all completed topics, and
 * checking if a specific topic is completed.
 *
 * @property topicProgressDao Data access object for topic progress data.
 * @property learningGoalDao Data access object for learning goal data.
 */
class TopicProgressRepository(
    private val topicProgressDao: TopicProgressDao,
    private val learningGoalDao: LearningGoalDao
) {

    /**
     * Checks if a specific topic is completed under a given goal.
     *
     * @param goalId The ID of the goal.
     * @param topicName The name of the topic.
     * @return True if the topic is completed, false otherwise.
     */
    suspend fun isTopicCompleted(goalId: Int, topicName: String): Boolean {
        return topicProgressDao.isTopicCompleted(goalId, topicName) ?: false
    }

    /**
     * Retrieves all completed topics.
     *
     * @return A flow emitting a list of all completed topics.
     */
    fun getAllCompletedTopics(): Flow<List<TopicProgress>> {
        return topicProgressDao.getAllCompletedTopics().map { entities ->
            entities.map { TopicProgressMapper.entityToDomain(it) }
        }
    }

    /**
     * Marks a topic as completed or not completed.
     *
     * @param goalId The ID of the goal.
     * @param topicName The name of the topic.
     * @param isCompleted True to mark the topic as completed, false to mark it as not completed.
     */
    // TODO Verify whether this flow is the right source for topic completion updates.
    suspend fun markTopicCompleted(
        goalId: Int,
        topicName: String,
        isCompleted: Boolean
    ) {
        // Guard against orphaned progress updates when the goal was deleted.
        val goalExists = learningGoalDao.getGoalById(goalId) != null
        if (!goalExists) {
            Log.e("TopicProgressRepo", "Attempted to mark progress for a non-existent goalId: $goalId")
            return
        }

        val existingEntity = topicProgressDao.getProgressForTopic(goalId, topicName)

        if (existingEntity != null) {
            val existingDomain = TopicProgressMapper.entityToDomain(existingEntity)

            val completedAtStable = when {
                existingDomain.completedAt != null -> existingDomain.completedAt
                isCompleted -> LocalDate.now()
                else -> null
            }

            val domainProgress = existingDomain.copy(
                isCompleted = isCompleted,
                completedAt = completedAtStable
            )
            updateProgress(domainProgress)
        } else {
            val newDomain = TopicProgress(
                id = 0,
                goalId = goalId,
                topicName = topicName,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) LocalDate.now() else null
            )
            insertProgress(newDomain)
        }
    }

    private suspend fun insertProgress(progress: TopicProgress): Long {
        return topicProgressDao.insertProgress(TopicProgressMapper.domainToEntity(progress))
    }

    private suspend fun updateProgress(progress: TopicProgress) {
        topicProgressDao.updateProgress(TopicProgressMapper.domainToEntity(progress))
    }

    /**
     * Deletes progress for a specific goal.
     *
     * @param goalId The ID of the goal whose progress should be deleted.
     */
    suspend fun deleteProgressForGoal(goalId: Int) {
        topicProgressDao.deleteProgressForGoal(goalId)
    }
}