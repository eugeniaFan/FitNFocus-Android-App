package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.TopicProgressRepository
import com.example.fitnfocus.domain.FocusArea
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import com.example.fitnfocus.domain.usecase.SetTopicCompletionUseCase
import com.example.fitnfocus.ui.goals.study.controller.AddGoalController
import com.example.fitnfocus.ui.goals.study.controller.EditGoalController
import com.example.fitnfocus.ui.goals.study.LearningNavigationState
import com.example.fitnfocus.ui.goals.study.controller.SessionController
import com.example.fitnfocus.ui.goals.study.controller.StudyNavigationController
import com.example.fitnfocus.ui.goals.study.StudyUiEvent
import com.example.fitnfocus.ui.goals.study.overview.components.TopicStatusInteractor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for the study area.
 * Manages sessions, topics, and learning goals.
 */

class StudyViewModel(
    private val sessionRepository: SessionRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val topicProgressRepository: TopicProgressRepository,
    private val setTopicCompletionUseCase: SetTopicCompletionUseCase
) : ViewModel() {

    private val _uiEvents = MutableSharedFlow<StudyUiEvent>(extraBufferCapacity = 8)
    val uiEvents = _uiEvents.asSharedFlow()

    val learningGoals = learningGoalRepository.getActiveGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val topicStatusInteractor = TopicStatusInteractor(
        sessionRepository = sessionRepository,
        topicProgressRepository = topicProgressRepository,
        scope = viewModelScope
    )
    private lateinit var navigationController: StudyNavigationController

    private val sessionController: SessionController by lazy {
        SessionController(
            sessionRepository = sessionRepository,
            learningGoalRepository = learningGoalRepository,
            topicStatusInteractor = topicStatusInteractor,
            scope = viewModelScope,
            uiEvents = _uiEvents,
            getNavState = { navigationController.navState.value },
            setNavState = { navigationController.setNavState(it) }
        )
    }

    val addGoalController = AddGoalController(
        learningGoalRepository = learningGoalRepository,
        scope = viewModelScope,
        navTo = { state -> navigationController.setNavState(state) }
    )

    val editGoalController = EditGoalController(
        learningGoalRepository = learningGoalRepository,
        scope = viewModelScope,
        uiEvents = _uiEvents
    )

    init {
        navigationController = StudyNavigationController(
            learningGoalRepository = learningGoalRepository,
            topicStatusInteractor = topicStatusInteractor,
            sessionController = sessionController,
            scope = viewModelScope
        )

        viewModelScope.launch {
            learningGoals.collect { goals ->
                topicStatusInteractor.loadForAllGoals(goals)
            }
        }

        sessionController.loadSessionsForDate(LocalDate.now())
    }

    val addGoalState = addGoalController.state
    val editGoalState = editGoalController.state

    val selectedFocusArea = navigationController.selectedFocusArea
    val learningNavState = navigationController.navState

    val topicProgress = topicStatusInteractor.topicProgress
    val topicStatusMap = topicStatusInteractor.topicStatusMap

    val sessionDialogState = sessionController.dialogState
    val isLoading = sessionController.isLoading
    val todaySessions = sessionController.todaySessions
    val selectedSession = sessionController.selectedSession
    val topicSessions = sessionController.topicSessions

    fun selectFocusArea(area: FocusArea) = navigationController.selectFocusArea(area)
    fun navigateToGoalDetail(goal: LearningGoal) = navigationController.navigateToGoalDetail(goal)
    fun navigateToTopicDetail(goal: LearningGoal, topic: String) =
        navigationController.navigateToTopicDetail(goal, topic)

    fun navigateBackToOverview() =
        navigationController.navigateBackToOverview { topicStatusInteractor.loadForAllGoals(learningGoals.value) }

    fun navigateToGoalDetailById(goalId: Int) = navigationController.navigateToGoalDetailById(goalId)

    fun setShowAddDialog(value: Boolean) = sessionController.setShowAddDialog(value)
    fun startSessionForTopic(goalId: Int, topic: String, moduleName: String) =
        sessionController.startSessionForTopic(goalId, topic, moduleName)

    fun selectSession(session: StudySession?) = sessionController.selectSession(session)
    fun loadSessionsForDate(date: LocalDate) = sessionController.loadSessionsForDate(date)

    fun updateSessionStatus(sessionId: Int, status: SessionStatus) =
        sessionController.updateSessionStatus(sessionId, status)

    fun updateSessionNotes(sessionId: Int, notes: String) =
        sessionController.updateSessionNotes(sessionId, notes)

    fun updateSession(session: StudySession) = sessionController.updateSession(session)
    fun deleteSession(session: StudySession) = sessionController.deleteSession(session)

    fun saveSession(durationMinutes: Int, addToCalendar: Boolean, startTimer: Boolean) =
        sessionController.saveSession(durationMinutes, addToCalendar, startTimer)

    fun openAddGoalSheet() = addGoalController.open()
    fun closeAddGoalSheet() = addGoalController.close()
    fun onNewGoalModuleNameChange(value: String) = addGoalController.onModuleNameChange(value)
    fun onNewGoalExamDateTextChange(value: String) = addGoalController.onExamDateTextChange(value)
    fun onNewGoalCurrentTopicChange(value: String) = addGoalController.onCurrentTopicChange(value)
    fun addNewGoalTopic() = addGoalController.addTopic()
    fun removeNewGoalTopic(topic: String) = addGoalController.removeTopic(topic)
    fun saveNewLearningGoal() = addGoalController.save()

    fun openEditGoalSheet(goal: LearningGoal) = editGoalController.open(goal)
    fun closeEditGoalSheet() = editGoalController.close()
    fun onEditGoalModuleNameChange(value: String) = editGoalController.onModuleNameChange(value)
    fun onEditGoalExamDateTextChange(value: String) = editGoalController.onExamDateTextChange(value)
    fun onEditGoalCurrentTopicChange(value: String) = editGoalController.onCurrentTopicChange(value)
    fun addEditGoalTopic() = editGoalController.addTopic()
    fun removeEditGoalTopic(topic: String) = editGoalController.removeTopic(topic)
    fun saveEditedLearningGoal() = editGoalController.save()

    fun refreshAllProgress() = topicStatusInteractor.loadForAllGoals(learningGoals.value)

    fun markTopicAsCompleted(goalId: Int, topic: String, isCompleted: Boolean) {
        toggleTopicProgress(goalId, topic, isCompleted)
    }

    fun toggleTopicProgress(goalId: Int, topicName: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                val changed = setTopicCompletionUseCase(goalId, topicName, isCompleted)
                topicStatusInteractor.updateLocal(topicName, isCompleted)
                if (changed) {
                    _uiEvents.tryEmit(
                        if (isCompleted) StudyUiEvent.ShowMessage("Thema als abgeschlossen markiert!")
                        else StudyUiEvent.ShowMessage("Thema-Status zurückgesetzt.")
                    )
                }
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Fehler beim Aktualisieren des Thema-Status."))
            }
        }
    }

    fun onTimerCompleted(sessionId: Int, goalId: Int, topic: String, markTopicCompleted: Boolean, notes: String) {
        sessionController.onTimerCompletedReturnToTopic(goalId, topic)
        topicStatusInteractor.loadForAllGoals(learningGoals.value)
    }

    private val _goalPendingDelete = MutableStateFlow<LearningGoal?>(null)
    val goalPendingDelete = _goalPendingDelete.asStateFlow()

    fun requestDeleteGoal(goal: LearningGoal) {
        _goalPendingDelete.value = goal
    }

    fun cancelDeleteGoal() {
        _goalPendingDelete.value = null
    }

    fun confirmDeleteGoal() {
        val goal = _goalPendingDelete.value ?: return
        viewModelScope.launch {
            try {
                sessionRepository.deleteSessionsForGoal(goal.id)
                topicProgressRepository.deleteProgressForGoal(goal.id)
                learningGoalRepository.deleteGoal(goal)
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Lernziel gelöscht."))
                navigationController.setNavState(LearningNavigationState.Overview)
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Löschen fehlgeschlagen."))
            } finally {
                _goalPendingDelete.value = null
            }
        }
    }
}