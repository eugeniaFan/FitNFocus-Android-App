package com.example.fitnfocus.ui.goals.study.controller

import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.domain.FocusArea
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.ui.goals.study.LearningNavigationState
import com.example.fitnfocus.ui.goals.study.controller.TopicStatusInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class StudyNavigationController(
    private val learningGoalRepository: LearningGoalRepository,
    private val topicStatusInteractor: TopicStatusInteractor,
    private val sessionController: SessionController,
    private val scope: CoroutineScope
) {
    private val _selectedFocusArea = MutableStateFlow(FocusArea.LEARNING)
    val selectedFocusArea: StateFlow<FocusArea> = _selectedFocusArea.asStateFlow()

    private val _navState =
        MutableStateFlow<LearningNavigationState>(LearningNavigationState.Overview)
    val navState: StateFlow<LearningNavigationState> = _navState.asStateFlow()

    fun selectFocusArea(area: FocusArea) {
        _selectedFocusArea.value = area
        _navState.value = LearningNavigationState.Overview
        sessionController.clearTopicDetail()
    }

    fun navigateToGoalDetail(goal: LearningGoal) {
        _navState.value = LearningNavigationState.GoalDetail(goalId = goal.id)
        topicStatusInteractor.loadForGoal(goal.id, goal.topics)
        sessionController.clearTopicDetail()
    }

    fun navigateToGoalDetailById(goalId: Int) {
        _navState.value = LearningNavigationState.GoalDetail(goalId = goalId)
        scope.launch {
            val goal = learningGoalRepository.getGoalById(goalId)
            if (goal != null) {
                topicStatusInteractor.loadForGoal(goal.id, goal.topics)
            }
        }
        sessionController.clearTopicDetail()
    }

    fun navigateToTopicDetail(goal: LearningGoal, topic: String) {
        _navState.value = LearningNavigationState.TopicDetail(goalId = goal.id, topic = topic)
        sessionController.bindTopicDetail(goal.id, topic)
    }

    fun navigateBackToOverview(onRefresh: () -> Unit) {
        _navState.value = LearningNavigationState.Overview
        sessionController.clearTopicDetail()
        onRefresh()
    }

    fun setNavState(state: LearningNavigationState) {
        _navState.value = state
    }
}