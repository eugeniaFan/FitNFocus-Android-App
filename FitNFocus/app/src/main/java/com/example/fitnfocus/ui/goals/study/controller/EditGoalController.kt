package com.example.fitnfocus.ui.goals.study.controller

import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.ui.goals.study.EditGoalUiState
import com.example.fitnfocus.ui.goals.study.StudyUiEvent
import com.example.fitnfocus.ui.goals.study.sessions.util.formatGermanDate
import com.example.fitnfocus.ui.goals.study.sessions.util.parseGermanDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditGoalController (
    private val learningGoalRepository: LearningGoalRepository,
    private val scope: CoroutineScope,
    private val uiEvents: MutableSharedFlow<StudyUiEvent>
) {
    private val _state = MutableStateFlow(EditGoalUiState())
    val state: StateFlow<EditGoalUiState> = _state.asStateFlow()

    fun open(goal: LearningGoal) {
        _state.value = EditGoalUiState(
            showSheet = true,
            goalId = goal.id,
            moduleName = goal.moduleName,
            examDateText = formatGermanDate(goal.examDate),
            topics = goal.topics,
            currentTopic = ""
        )
    }

    fun close() {
        _state.value = EditGoalUiState()
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
        val topicName = _state.value.currentTopic.trim()
        if (topicName.isEmpty()) return
        if (_state.value.topics.any { it.equals(topicName, ignoreCase = true) }) {
            _state.update { it.copy(currentTopic = "") }
            return
        }
        _state.update { it.copy(topics = it.topics + topicName, currentTopic = "") }
    }

    fun removeTopic(topicName: String) {
        _state.update { it.copy(topics = it.topics - topicName) }
    }

    fun save() {
        scope.launch {
            val s = _state.value
            val module = s.moduleName.trim()
            if (module.isEmpty()) {
                uiEvents.tryEmit(StudyUiEvent.ShowMessage("Bitte gib einen Modulnamen ein."))
            } else {
                _state.update { it.copy(isSaving = true) }
            }

            try {
                val examDate = parseGermanDate(s.examDateText)
                val existingGoal = learningGoalRepository.getGoalById(s.goalId)

                if (existingGoal != null) {
                    val updated = existingGoal.copy(
                        moduleName = module,
                        topics = s.topics,
                        examDate = examDate
                    )
                    learningGoalRepository.updateGoal(updated)
                    uiEvents.tryEmit(StudyUiEvent.ShowMessage("Lernziel aktualisiert."))
                }

                close()
            } catch (e: Exception) {
                uiEvents.tryEmit(StudyUiEvent.ShowMessage(e.message ?: "Aktualisierung fehlgeschlagen."))
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }
}