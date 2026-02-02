package com.example.fitnfocus.ui.goals.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.FocusArea
import com.example.fitnfocus.ui.goals.study.dialogs.AddLearningGoalBottomSheet
import com.example.fitnfocus.ui.goals.study.dialogs.EditLearningGoalBottomSheet
import com.example.fitnfocus.ui.goals.study.overview.components.FocusAreaSelector
import com.example.fitnfocus.viewmodel.SessionTimerViewModel
import com.example.fitnfocus.viewmodel.StudyViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.ui.goals.study.sessions.dialogs.AddSessionDialog
import com.example.fitnfocus.ui.goals.study.sessions.dialogs.EditSessionDialog
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    viewModel: StudyViewModel,
    onSessionStopped: () -> Unit = {},
    onSessionCompleted: () -> Unit = {},
    timerViewModel: SessionTimerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val timerUiState by timerViewModel.uiState.collectAsState()

    val selectedFocusArea by viewModel.selectedFocusArea.collectAsState()

    val learningNavState by viewModel.learningNavState.collectAsState()
    val topicProgress by viewModel.topicProgress.collectAsState()
    val topicStatusMap by viewModel.topicStatusMap.collectAsState()
    val topicSessions by viewModel.topicSessions.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val selectedSession by viewModel.selectedSession.collectAsState()

    val learningGoals by viewModel.learningGoals.collectAsState()

    val today = LocalDate.now()

    LaunchedEffect(today) {
        if (today != null) {
            viewModel.loadSessionsForDate(today)
        }
    }

    LaunchedEffect(learningNavState) {
        if (learningNavState is LearningNavigationState.Overview) {
            viewModel.refreshAllProgress()
        }
    }

    val addGoalState by viewModel.addGoalState.collectAsState()
    val goalPendingDelete by viewModel.goalPendingDelete.collectAsState()
    val editGoalState by viewModel.editGoalState.collectAsState()
    val sessionDialogState by viewModel.sessionDialogState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus-Bereich") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            if (learningNavState !is LearningNavigationState.SessionTimer) {
                FocusAreaSelector(
                    selectedArea = selectedFocusArea,
                    onAreaSelected = { viewModel.selectFocusArea(it) },
                    modifier = Modifier.height(84.dp)
                )
            }
            when {
                selectedFocusArea.isAvailable -> {
                    StudyNavigationHost(
                        navState = learningNavState,
                        learningGoals = learningGoals,
                        topicProgress = topicProgress,
                        topicStatusMap = topicStatusMap,
                        topicSessions = topicSessions,
                        isLoading = isLoading,
                        timerUiState = timerUiState,
                        timerViewModel = timerViewModel,
                        onGoalClick = { viewModel.navigateToGoalDetail(it) },
                        onBackToOverview = { viewModel.navigateBackToOverview() },
                        onBackToGoalDetail = { goalId -> viewModel.navigateToGoalDetailById(goalId) },
                        onTopicClick = { goal, topic ->
                            viewModel.navigateToTopicDetail(
                                goal,
                                topic
                            )
                        },
                        onTopicToggle = { goalId, topic, completed ->
                            viewModel.toggleTopicProgress(
                                goalId,
                                topic,
                                completed
                            )
                        },
                        onAddSessionForTopic = { goal, topic ->
                            viewModel.startSessionForTopic(
                                goal.id,
                                topic,
                                goal.moduleName
                            )
                        },
                        onSessionClick = { viewModel.selectSession(it) },
                        onAddGoalClick = { viewModel.openAddGoalSheet() },
                        onDeleteGoalClick = { viewModel.requestDeleteGoal(it) },
                        onEditGoalClick = { viewModel.openEditGoalSheet(it) },
                        onUpdateSessionStatus = { sessionId, status ->
                            viewModel.updateSessionStatus(
                                sessionId,
                                status
                            )
                        },
                        onUpdateSessionNotes = { sessionId, notes ->
                            viewModel.updateSessionNotes(
                                sessionId,
                                notes
                            )
                        },
                        onMarkTopicCompleted = { goalId, topic, isCompleted ->
                            viewModel.markTopicAsCompleted(
                                goalId,
                                topic,
                                isCompleted
                            )
                        },
                        onTimerCompleted = { sessionId, goalId, topic, markTopicCompleted, notes ->
                            viewModel.onTimerCompleted(
                                sessionId,
                                goalId,
                                topic,
                                markTopicCompleted,
                                notes
                            )
                            onSessionCompleted()
                        },
                        onTimerStopped = {
                            onSessionStopped()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                else -> {
                    ComingSoonContent(
                        focusArea = selectedFocusArea,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (sessionDialogState.showAddDialog) {
        AddSessionDialog(
            topic = sessionDialogState.newTopic,
            isLoading = isLoading,
            onDismiss = { viewModel.setShowAddDialog(false) },
            onSave = { durationMinutes, addToCalendar ->
                viewModel.saveSession(durationMinutes, addToCalendar, startTimer = false)
            }
        )
    }

    if (selectedSession != null) {
        EditSessionDialog(
            session = selectedSession!!,
            isLoading = isLoading,
            onDismiss = { viewModel.selectSession(null) },
            onUpdate = { updated -> viewModel.updateSession(updated) },
            onDelete = { toDelete -> viewModel.deleteSession(toDelete) }
        )
    }

    if (addGoalState.showSheet) {
        AddLearningGoalBottomSheet(
            moduleName = addGoalState.moduleName,
            examDateText = addGoalState.examDateText,
            topics = addGoalState.topics,
            currentTopic = addGoalState.currentTopic,
            isSaving = addGoalState.isSaving,
            onModuleNameChange = viewModel::onNewGoalModuleNameChange,
            onExamDateTextChange = viewModel::onNewGoalExamDateTextChange,
            onCurrentTopicChange = viewModel::onNewGoalCurrentTopicChange,
            onAddTopic = viewModel::addNewGoalTopic,
            onRemoveTopic = viewModel::removeNewGoalTopic,
            onDismiss = viewModel::closeAddGoalSheet,
            onSave = viewModel::saveNewLearningGoal
        )
    }

    if (goalPendingDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelDeleteGoal() },
            title = { Text("Lernziel löschen?") },
            text = {
                Text("Möchtest du \"${goalPendingDelete!!.moduleName}\" wirklich löschen? Dies kann nicht rückgängig gemacht werden.")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteGoal() }) {
                    Text("Löschen")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDeleteGoal() }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    if (editGoalState.showSheet) {
        EditLearningGoalBottomSheet(
            moduleName = editGoalState.moduleName,
            examDateText = editGoalState.examDateText,
            topics = editGoalState.topics,
            currentTopic = editGoalState.currentTopic,
            isSaving = editGoalState.isSaving,
            onModuleNameChange = viewModel::onEditGoalModuleNameChange,
            onExamDateTextChange = viewModel::onEditGoalExamDateTextChange,
            onCurrentTopicChange = viewModel::onEditGoalCurrentTopicChange,
            onAddTopic = viewModel::addEditGoalTopic,
            onRemoveTopic = viewModel::removeEditGoalTopic,
            onDismiss = viewModel::closeEditGoalSheet,
            onSave = viewModel::saveEditedLearningGoal
        )
    }
}


/**
 * Placeholder for future content.
 */
@Composable
private fun ComingSoonContent(
    focusArea: FocusArea,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.Construction,
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .height(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )

        Text(
            text = "Bereich \"${focusArea.displayName}\"",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "ist noch im Aufbau",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Dieser Bereich wird bald verfügbar sein.\nSchau später wieder vorbei!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(16.dp))
    }
}
