package com.example.fitnfocus.ui.goals.study.overview.components

import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.TopicProgressRepository
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.ui.goals.study.TopicStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TopicStatusInteractor (
    private val sessionRepository: SessionRepository,
    private val topicProgressRepository: TopicProgressRepository,
    private val scope: CoroutineScope
) {
    private val _topicProgress = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val topicProgress: StateFlow<Map<String, Boolean>> = _topicProgress.asStateFlow()

    private val _topicStatusMap = MutableStateFlow<Map<String, TopicStatus>>(emptyMap())
    val topicStatusMap: StateFlow<Map<String, TopicStatus>> = _topicStatusMap.asStateFlow()

    fun loadForGoal(goalId: Int, topics: List<String>) {
        scope.launch {
            val statusMap = mutableMapOf<String, TopicStatus>()
            val progressMap = mutableMapOf<String, Boolean>()

            for (topic in topics) {
                val hasSessions = sessionRepository.hasSessionsForTopic(topic, goalId)
                val isCompleted = topicProgressRepository.isTopicCompleted(goalId, topic)

                statusMap[topic] = when {
                    isCompleted -> TopicStatus.COMPLETED
                    hasSessions -> TopicStatus.IN_PROGRESS
                    else -> TopicStatus.NOT_STARTED
                }
                progressMap[topic] = isCompleted
            }

            _topicStatusMap.value = statusMap
            _topicProgress.value = progressMap
        }
    }

    fun loadForAllGoals(goals: List<LearningGoal>) {
        scope.launch {
            val statusMap = mutableMapOf<String, TopicStatus>()
            val progressMap = mutableMapOf<String, Boolean>()

            goals.forEach { goal ->
                goal.topics.forEach { topic ->
                    val hasSessions = sessionRepository.hasSessionsForTopic(topic, goal.id)
                    val isCompleted = topicProgressRepository.isTopicCompleted(goal.id, topic)

                    statusMap[topic] = when {
                        isCompleted -> TopicStatus.COMPLETED
                        hasSessions -> TopicStatus.IN_PROGRESS
                        else -> TopicStatus.NOT_STARTED
                    }
                    progressMap[topic] = isCompleted
                }
            }

            _topicStatusMap.value = statusMap
            _topicProgress.value = progressMap
        }
    }

    fun updateLocal(topicName: String, isCompleted: Boolean) {
        _topicProgress.update { it.toMutableMap().apply { put(topicName, isCompleted) } }
        _topicStatusMap.update {
            it.toMutableMap().apply {
                put(
                    topicName,
                    if (isCompleted) TopicStatus.COMPLETED else TopicStatus.IN_PROGRESS
                )
            }
        }
    }

    fun markInProgress(topicName: String) {
        _topicStatusMap.update { it.toMutableMap().apply { put(topicName, TopicStatus.IN_PROGRESS) } }
    }

}