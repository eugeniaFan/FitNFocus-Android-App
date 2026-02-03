package com.example.fitnfocus.domain.usecase

import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.TopicProgressRepository

/**
 * Use case for managing topic completion status.
 * Centralizes topic completion logic including status persistence and session updates.
 */
class SetTopicCompletionUseCase(
    private val topicProgressRepository: TopicProgressRepository,
    private val sessionRepository: SessionRepository
) {
    /**
     * Sets the completion status of a topic.
     * When marking as completed, automatically closes all open sessions for the topic.
     *
     * @return true if status was changed, false if already in desired state
     */
    suspend operator fun invoke(
        goalId: Int,
        topicName: String,
        isCompleted: Boolean
    ): Boolean {
        val currentStatus = topicProgressRepository.isTopicCompleted(goalId, topicName)
        if (currentStatus == isCompleted) {
            return false
        }

        topicProgressRepository.markTopicCompleted(goalId, topicName, isCompleted)

        if (isCompleted) {
            sessionRepository.completeAllSessionsForTopic(topicName, goalId)
        }

        return true
    }

    suspend fun isCompleted(goalId: Int, topicName: String): Boolean {
        return topicProgressRepository.isTopicCompleted(goalId, topicName)
    }
}
