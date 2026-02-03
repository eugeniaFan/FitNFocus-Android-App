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
 * Content for the learning area with navigation.
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
    onTimerStopped: () -> Unit,  // Stop/Cancel -> Navigate to Dashboard
    modifier: Modifier = Modifier,
) {
    when (navState) {
        is LearningNavigationState.Overview -> {
            // Overview of all learning goals
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
                // Detail view of a learning goal
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
                // Detail view of a topic (sessions for this topic)
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
            // Initialize timer when session changes
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

            // Timer screen for the session (refactored timer)
            SessionTimerScreen(
                uiState = timerUiState,
                onStartTimer = { timerViewModel.startTimer() },
                onPauseTimer = { timerViewModel.pauseTimer() },
                onResumeTimer = { timerViewModel.resumeTimer() },
                onStopTimer = { timerViewModel.stopTimer() },
                onConfirmPartialSave = {
                    timerViewModel.confirmPartialSave {
                        // After DB operation: Navigate to dashboard
                        onTimerStopped()
                    }
                },
                onDismissPartialSave = {
                    timerViewModel.dismissPartialSave {
                        // After reset: Navigate to dashboard
                        onTimerStopped()
                    }
                },
                onCompleteSession = {
                    timerViewModel.completeSession {
                        // After DB operation: Navigate to focus area
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
                    // Stop -> without saving --> Home area
                    timerViewModel.cancelTimer()
                    onTimerStopped()
                },
                modifier = modifier
            )
        }
    }
}