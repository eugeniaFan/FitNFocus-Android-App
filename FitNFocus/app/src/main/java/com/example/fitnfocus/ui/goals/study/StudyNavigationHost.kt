package com.example.fitnfocus.ui.goals.study

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import com.example.fitnfocus.ui.goals.study.detail.GoalDetailScreen
import com.example.fitnfocus.ui.goals.study.overview.GoalsOverviewScreen
import com.example.fitnfocus.ui.goals.study.sessions.TopicSessionsScreen
import com.example.fitnfocus.ui.goals.study.timer.SessionTimerScreen
import com.example.fitnfocus.ui.goals.study.timer.SessionTimerUiState
import com.example.fitnfocus.viewmodel.SessionTimerViewModel

/**
 * Inhalt für den Lernen-Bereich mit Navigation.
 */
@Composable
fun StudyNavigationHost(
    navState: LearningNavigationState,
    learningGoals: List<LearningGoal>,
    topicProgress: Map<String, Boolean>,
    topicStatusMap: Map<String, TopicStatus>,
    topicSessions: List<StudySession>,
    isLoading: Boolean,
    timerUiState: SessionTimerUiState,
    timerViewModel: SessionTimerViewModel,
    onGoalClick: (LearningGoal) -> Unit,
    onBackToOverview: () -> Unit,
    onBackToGoalDetail: (Int) -> Unit,
    onTopicClick: (LearningGoal, String) -> Unit,
    onTopicToggle: (Int, String, Boolean) -> Unit,
    onAddSessionForTopic: (LearningGoal, String) -> Unit,
    onSessionClick: (StudySession) -> Unit,
    onAddGoalClick: () -> Unit,
    onDeleteGoalClick: (LearningGoal) -> Unit,
    onEditGoalClick: (LearningGoal) -> Unit,
    onUpdateSessionStatus: (Int, SessionStatus) -> Unit,
    onUpdateSessionNotes: (Int, String) -> Unit,
    onMarkTopicCompleted: (Int, String, Boolean) -> Unit,
    onTimerCompleted: (Int, Int, String, Boolean, String) -> Unit,  // sessionId, goalId, topic, markTopicCompleted, notes
    onTimerStopped: () -> Unit,  // Stop/Cancel → Dashboard
    modifier: Modifier = Modifier,
) {
    when (navState) {
        is LearningNavigationState.Overview -> {
            // Übersicht aller Lernziele
            GoalsOverviewScreen(
                learningGoals = learningGoals,
                topicProgress = topicProgress,
                onGoalClick = onGoalClick,
                onDeleteGoal = onDeleteGoalClick,
                onAddGoalClick = onAddGoalClick,
                modifier = modifier
            )
        }

        is LearningNavigationState.GoalDetail -> {
            val goal = learningGoals.firstOrNull { it.id == navState.goalId }
            if (goal != null) {
                // Detail eines Lernziels
                GoalDetailScreen(
                    goal = goal,
                    topicProgress = topicProgress,
                    topicStatusMap = topicStatusMap,
                    onBackClick = onBackToOverview,
                    onEditClick = onEditGoalClick,
                    onTopicClick = { topic: String -> onTopicClick(goal, topic) },
                    onTopicToggle = onTopicToggle,

                    modifier = modifier
                )
            } else {
                Text("Lernziel nicht gefunden.")
            }
        }

        is LearningNavigationState.TopicDetail -> {
            val goal = learningGoals.firstOrNull { it.id == navState.goalId }
            if (goal != null) {
                // Detail eines Topics (Sessions für dieses Topic)
                val isTopicCompleted = topicProgress[navState.topic] == true

                TopicSessionsScreen(
                    goal = goal,
                    topic = navState.topic,
                    sessions = topicSessions,
                    isLoading = isLoading,
                    isTopicCompleted = isTopicCompleted,
                    onBackClick = { onBackToGoalDetail(navState.goalId) },
                    onAddClick = { onAddSessionForTopic(goal, navState.topic) },
                    onSessionClick = onSessionClick,
                    onUpdateSessionStatus = onUpdateSessionStatus,
                    onUpdateSessionNotes = onUpdateSessionNotes,
                    onMarkTopicCompleted = { goalId, topic, isCompleted ->
                        onMarkTopicCompleted(goalId, topic, isCompleted)
                    },
                    modifier = modifier
                )
            } else {
                Text("Lernziel nicht gefunden.")
            }
        }

        is LearningNavigationState.SessionTimer -> {
            // Initialisiere Timer wenn Session sich ändert
            LaunchedEffect(navState.sessionId) {
                if (timerUiState.sessionId != navState.sessionId) {
                    timerViewModel.initializeSession(
                        sessionId = navState.sessionId,
                        sessionTopic = navState.topic,
                        moduleName = navState.moduleName,
                        durationMinutes = navState.durationMinutes,
                        goalId = navState.goalId
                    )
                }
            }

            // Timer-Screen für die Session (neuer refactored Timer)
            SessionTimerScreen(
                uiState = timerUiState,
                onStartTimer = { timerViewModel.startTimer() },
                onPauseTimer = { timerViewModel.pauseTimer() },
                onResumeTimer = { timerViewModel.resumeTimer() },
                onStopTimer = { timerViewModel.stopTimer() },
                onConfirmPartialSave = {
                    timerViewModel.confirmPartialSave {
                        // Nach DB-Operation: Navigation zum Dashboard
                        onTimerStopped()
                    }
                },
                onDismissPartialSave = {
                    timerViewModel.dismissPartialSave {
                        // Nach Reset: Navigation zum Dashboard
                        onTimerStopped()
                    }
                },
                onCompleteSession = {
                    timerViewModel.completeSession {
                        // Nach DB-Operation: Navigation zum Focus-Bereich
                        onTimerCompleted(
                            navState.sessionId,
                            navState.goalId,
                            navState.topic,
                            timerUiState.markTopicAsCompleted,
                            timerUiState.sessionNotes
                        )
                    }
                },
                onUpdateNotes = { timerViewModel.updateNotes(it) },
                onUpdateMarkTopicCompleted = { timerViewModel.updateMarkTopicCompleted(it) },
                onAbort = {
                    // Stop/Cancel ohne Speichern → Dashboard
                    timerViewModel.cancelTimer()
                    onTimerStopped()
                },
                modifier = modifier
            )
        }
    }
}