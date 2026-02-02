package com.example.fitnfocus.ui.goals.study.controller

import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.ui.goals.study.AddGoalUiState
import com.example.fitnfocus.ui.goals.study.LearningNavigationState
import com.example.fitnfocus.ui.goals.study.sessions.util.parseGermanDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddGoalController (
    private val learningGoalRepository: LearningGoalRepository,
    private val scope: CoroutineScope,
    private val navTo: (LearningNavigationState) -> Unit
) {
    private val _state = MutableStateFlow(AddGoalUiState())
    val state: StateFlow<AddGoalUiState> = _state.asStateFlow()

    fun open() {
        _state.update { it.copy(showSheet = true) }
    }

    fun close() {
        _state.value = AddGoalUiState()
    }

    fun onModuleNameChange(value: String) {
        _state.update { it.copy(moduleName = value) }
    }

    fun onExamDateTextChange(value: String) {
        _state.update { it.copy(examDateText = value) }
    }

    fun onCurrentTopicChange(value: String) {
        _state.update { it.copy(currentTopic = value) }
    }

    fun addTopic() {
        val topic = _state.value.currentTopic.trim()
        if (topic.isEmpty()) return

        if (_state.value.topics.any { it.equals(topic, ignoreCase = true) }) {
            _state.update { it.copy(currentTopic = "") }
            return
        }

        _state.update { it.copy(topics = it.topics + topic, currentTopic = "") }
    }

    fun removeTopic(topic: String) {
        _state.update { it.copy(topics = it.topics - topic) }
    }

    fun save() {
        scope.launch {
            val s = _state.value
            val module = s.moduleName.trim()
            if (module.isEmpty()) return@launch // TODO Überarbeiten

            _state.update { it.copy(isSaving = true) }
            try {
                val examDate = parseGermanDate(s.examDateText)

                val goal = LearningGoal(
                    moduleName = module,
                    topics = s.topics,
                    examDate = examDate
                )

                learningGoalRepository.insertGoal(goal)
                close()
                navTo(LearningNavigationState.Overview)
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }
}