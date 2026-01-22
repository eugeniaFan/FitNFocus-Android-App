package com.example.fitnfocus.domain.usecase

import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.TopicProgressRepository

/**
 * UseCase für das Setzen des Completion-Status eines Topics.
 *
 * Single Source of Truth für Topic-Completion-Logik:
 * - Speichert den Completion-Status in der TopicProgress-Tabelle
 * - Wenn isCompleted=true: schließt alle offenen Sessions für das Topic ab
 *
 * Wird sowohl von StudyViewModel als auch SessionTimerViewModel verwendet.
 */
class SetTopicCompletionUseCase(
    private val topicProgressRepository: TopicProgressRepository,
    private val sessionRepository: SessionRepository
) {
    /**
     * Setzt den Completion-Status eines Topics.
     *
     * @param goalId ID des Lernziels
     * @param topicName Name des Topics
     * @param isCompleted true = abgeschlossen, false = nicht abgeschlossen
     * @return true wenn der Status geändert wurde, false wenn er bereits gleich war
     */
    suspend operator fun invoke(
        goalId: Int,
        topicName: String,
        isCompleted: Boolean
    ): Boolean {
        // Prüfen ob Topic bereits den gewünschten Status hat
        val currentStatus = topicProgressRepository.isTopicCompleted(goalId, topicName)
        if (currentStatus == isCompleted) {
            // Status ist bereits korrekt, nichts zu tun
            return false
        }

        // Status in TopicProgress-Tabelle speichern
        topicProgressRepository.markTopicCompleted(goalId, topicName, isCompleted)

        // Wenn Topic abgeschlossen wird: alle offenen Sessions auch abschließen
        if (isCompleted) {
            sessionRepository.completeAllSessionsForTopic(topicName, goalId)
        }

        return true
    }

    /**
     * Prüft ob ein Topic bereits abgeschlossen ist.
     */
    suspend fun isCompleted(goalId: Int, topicName: String): Boolean {
        return topicProgressRepository.isTopicCompleted(goalId, topicName)
    }
}

