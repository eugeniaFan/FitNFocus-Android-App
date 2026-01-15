package com.example.fitnfocus.ui.study

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.FocusArea
import com.example.fitnfocus.ui.study.components.AddLearningGoalBottomSheet
import com.example.fitnfocus.ui.study.components.EditLearningGoalBottomSheet
import com.example.fitnfocus.ui.study.components.FocusAreaSelector
import com.example.fitnfocus.ui.study.components.LearningGoalDetail
import com.example.fitnfocus.ui.study.components.LearningGoalsOverview
import com.example.fitnfocus.ui.study.timer.SessionTimerScreen
import com.example.fitnfocus.ui.study.timer.SessionTimerUiState
import com.example.fitnfocus.ui.study.timer.SessionTimerViewModel
import com.example.fitnfocus.viewmodel.LearningNavigationState
import com.example.fitnfocus.viewmodel.StudyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.ui.study.components.FitNFocusColors

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi") // API-Check erfolgt im ViewModel
@Composable
fun StudyScreen(
    onBack: () -> Unit,
    viewModel: StudyViewModel,
    snackbarHostState: SnackbarHostState,
    onSessionStopped: () -> Unit = {},    // Stop/Cancel → Dashboard
    onSessionCompleted: () -> Unit = {},  // Completed → Focus-Bereich
    timerViewModel: SessionTimerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Timer UI State
    val timerUiState by timerViewModel.uiState.collectAsState()

    // Fokus-Bereich
    val selectedFocusArea by viewModel.selectedFocusArea.collectAsState()

    // Lern-Navigation
    val learningNavState by viewModel.learningNavState.collectAsState()
    val topicProgress by viewModel.topicProgress.collectAsState()
    val topicStatusMap by viewModel.topicStatusMap.collectAsState()
    val topicSessions by viewModel.topicSessions.collectAsState()

    // Formular-Daten
    val topic by viewModel.topic.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val selectedTopic by viewModel.selectedTopic.collectAsState()
    val isManualInput by viewModel.isManualInput.collectAsState()

    // UI States
    val sessions by viewModel.todaySessions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedSession by viewModel.selectedSession.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()

    // Lernziele & Topics
    val learningGoals by viewModel.learningGoals.collectAsState()
    val availableTopics by viewModel.availableTopics.collectAsState()

    // Datum für heute (als LocalDate)
    val today = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        java.time.LocalDate.now()
    } else {
        null
    }

    // Lade Sessions und Fortschritt beim Anzeigen
    LaunchedEffect(today) {
        if (today != null) {
            viewModel.loadSessionsForDate(today)
        }
    }

    // Aktualisiere Fortschritt wenn sich die Navigation zur Übersicht ändert
    LaunchedEffect(learningNavState) {
        if (learningNavState is LearningNavigationState.Overview) {
            viewModel.refreshAllProgress()
        }
    }

    // Add-Lernziel BottomSheet
    val showAddGoalSheet by viewModel.showAddGoalSheet.collectAsState()
    val newGoalModuleName by viewModel.newGoalModuleName.collectAsState()
    val newGoalExamDateText by viewModel.newGoalExamDateText.collectAsState()
    val newGoalTopics by viewModel.newGoalTopics.collectAsState()
    val newGoalCurrentTopic by viewModel.newGoalCurrentTopic.collectAsState()
    val isSavingGoal by viewModel.isSavingGoal.collectAsState()

    // Delete-Dialog
    val goalPendingDelete by viewModel.goalPendingDelete.collectAsState()

    // Edit-Lernziel BottomSheet
    val showEditGoalSheet by viewModel.showEditGoalSheet.collectAsState()
    val editGoalModuleName by viewModel.editGoalModuleName.collectAsState()
    val editGoalExamDateText by viewModel.editGoalExamDateText.collectAsState()
    val editGoalTopics by viewModel.editGoalTopics.collectAsState()
    val editGoalCurrentTopic by viewModel.editGoalCurrentTopic.collectAsState()
    val isSavingEditGoal by viewModel.isSavingEditGoal.collectAsState()

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
                //snackbarHost = { SnackbarHost(snackbarHostState) }
                )

        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            //verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==================== FOKUS-BEREICH SELECTOR ====================
            // Nur anzeigen wenn nicht im Timer-Modus
            if (learningNavState !is LearningNavigationState.SessionTimer) {
                FocusAreaSelector(
                    selectedArea = selectedFocusArea,
                    onAreaSelected = { viewModel.selectFocusArea(it) },
                    modifier = Modifier.height(84.dp)
                )
            }

            // ==================== CONTENT BASIEREND AUF FOKUS-BEREICH ====================
            when {
                selectedFocusArea.isAvailable -> {
                    // Lernen-Bereich mit Navigation
                    LearningContent(
                        navState = learningNavState,
                        learningGoals = learningGoals,
                        topicProgress = topicProgress,
                        topicStatusMap = topicStatusMap,
                        topicSessions = topicSessions,
                        sessions = sessions,
                        isLoading = isLoading,
                        timerUiState = timerUiState,
                        timerViewModel = timerViewModel,
                        onGoalClick = { viewModel.navigateToGoalDetail(it) },
                        onBackToOverview = { viewModel.navigateBackToOverview() },
                        onBackToGoalDetail = { goalId -> viewModel.navigateToGoalDetailById(goalId) },
                        onTopicClick = { goal, topic -> viewModel.navigateToTopicDetail(goal, topic) },
                        onTopicToggle = { goalId, topic, completed -> viewModel.toggleTopicProgress(goalId, topic, completed) },
                        onStartSession = { topic -> viewModel.startSessionForTopic(topic) },
                        onAddClick = { viewModel.setShowAddDialog(true) },
                        onSessionClick = { viewModel.selectSession(it) },
                        onAddGoalClick = { viewModel.openAddGoalSheet() },
                        onDeleteGoalClick = { viewModel.requestDeleteGoal(it) },
                        onEditGoalClick = { viewModel.openEditGoalSheet(it) },
                        onUpdateSessionStatus = { sessionId, status -> viewModel.updateSessionStatus(sessionId, status) },
                        onUpdateSessionNotes = { sessionId, notes -> viewModel.updateSessionNotes(sessionId, notes) },
                        onMarkTopicCompleted = { goalId, topic, isCompleted -> viewModel.markTopicAsCompleted(goalId, topic, isCompleted) },
                        onTimerCompleted = { sessionId, goalId, topic, markTopicCompleted, notes ->
                            viewModel.onTimerCompleted(sessionId, goalId, topic, markTopicCompleted, notes)
                            onSessionCompleted()
                        },
                        onTimerStopped = {
                            viewModel.navigateBackToOverview()
                            onSessionStopped()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                else -> {
                    // Anderer Bereich (noch nicht verfügbar)
                    ComingSoonContent(
                        focusArea = selectedFocusArea,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // -------- Add Dialog mit Topic-Auswahl --------
    if (showAddDialog) {
        AddSessionDialog(
            subject = topic,
            duration = duration,
            isLoading = isLoading,
            availableTopics = availableTopics,
            learningGoals = learningGoals,
            selectedTopic = selectedTopic,
            isManualInput = isManualInput,
            onSubjectChange = viewModel::onTopicChange,
            onDurationChange = viewModel::onDurationChange,
            onTopicSelected = viewModel::selectTopic,
            onManualInputToggle = viewModel::setManualInput,
            onDismiss = { viewModel.setShowAddDialog(false) },
            onSave = { addToCalendar, goalIdForNewTopic ->
                viewModel.saveSession(addToCalendar, goalIdForNewTopic)
            }
        )
    }

    // -------- Edit Dialog --------
    if (selectedSession != null) {
        EditSessionDialog(
            session = selectedSession!!,
            isLoading = isLoading,
            onDismiss = { viewModel.selectSession(null) },
            onUpdate = { updated -> viewModel.updateSession(updated) },
            onDelete = { toDelete -> viewModel.deleteSession(toDelete) }
        )
    }

    // BottomSheet: neues Lernziel hinzufügen
    if (showAddGoalSheet) {
        AddLearningGoalBottomSheet(
            moduleName = newGoalModuleName,
            examDateText = newGoalExamDateText,
            topics = newGoalTopics,
            currentTopic = newGoalCurrentTopic,
            isSaving = isSavingGoal,
            onModuleNameChange = viewModel::onNewGoalModuleNameChange,
            onExamDateTextChange = viewModel::onNewGoalExamDateTextChange,
            onCurrentTopicChange = viewModel::onNewGoalCurrentTopicChange,
            onAddTopic = viewModel::addNewGoalTopic,
            onRemoveTopic = viewModel::removeNewGoalTopic,
            onDismiss = viewModel::closeAddGoalSheet,
            onSave = viewModel::saveNewLearningGoal
        )
    }

    // -------- Delete Dialog --------
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

    // BottomSheet: Lernziel bearbeiten
    if (showEditGoalSheet) {
        EditLearningGoalBottomSheet(
            moduleName = editGoalModuleName,
            examDateText = editGoalExamDateText,
            topics = editGoalTopics,
            currentTopic = editGoalCurrentTopic,
            isSaving = isSavingEditGoal,
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
 * Inhalt für den Lernen-Bereich mit Navigation.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun LearningContent(
    navState: LearningNavigationState,
    learningGoals: List<com.example.fitnfocus.domain.LearningGoal>,
    topicProgress: Map<String, Boolean>,
    topicStatusMap: Map<String, com.example.fitnfocus.viewmodel.TopicStatus>,
    topicSessions: List<com.example.fitnfocus.domain.StudySession>,
    sessions: List<com.example.fitnfocus.domain.StudySession>,
    isLoading: Boolean,
    timerUiState: SessionTimerUiState,
    timerViewModel: SessionTimerViewModel,
    onGoalClick: (com.example.fitnfocus.domain.LearningGoal) -> Unit,
    onBackToOverview: () -> Unit,
    onBackToGoalDetail: (Int) -> Unit,
    onTopicClick: (com.example.fitnfocus.domain.LearningGoal, String) -> Unit,
    onTopicToggle: (Int, String, Boolean) -> Unit,
    onStartSession: (String) -> Unit,
    onAddClick: () -> Unit,
    onSessionClick: (com.example.fitnfocus.domain.StudySession) -> Unit,
    onAddGoalClick: () -> Unit,
    onDeleteGoalClick: (com.example.fitnfocus.domain.LearningGoal) -> Unit,
    onEditGoalClick: (com.example.fitnfocus.domain.LearningGoal) -> Unit,
    onUpdateSessionStatus: (Int, com.example.fitnfocus.domain.SessionStatus) -> Unit,
    onUpdateSessionNotes: (Int, String) -> Unit,
    onMarkTopicCompleted: (Int, String, Boolean) -> Unit,
    onTimerCompleted: (Int, Int, String, Boolean, String) -> Unit,  // sessionId, goalId, topic, markTopicCompleted, notes
    onTimerStopped: () -> Unit,  // Stop/Cancel → Dashboard
    modifier: Modifier = Modifier
) {
    when (navState) {
        is LearningNavigationState.Overview -> {
            // Übersicht aller Lernziele
            LearningGoalsOverview(
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
                LearningGoalDetail(
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
                // isTopicCompleted aus topicProgress Map
                val isTopicCompleted = topicProgress[navState.topic] == true

                TopicSessionsContent(
                    goal = goal,
                    topic = navState.topic,
                    sessions = topicSessions,
                    isLoading = isLoading,
                    isTopicCompleted = isTopicCompleted,
                    onBackClick = { onBackToGoalDetail(navState.goalId) },
                    onAddClick = { onStartSession(navState.topic) },
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
                        onTimerCompleted(navState.sessionId, navState.goalId, navState.topic, timerUiState.markTopicAsCompleted, timerUiState.sessionNotes)
                    }
                },
                onUpdateNotes = { timerViewModel.updateNotes(it) },
                onUpdateMarkTopicCompleted = { timerViewModel.updateMarkTopicCompleted(it) },
                onSessionCompleted = {
                    // Dieser Callback wird jetzt nicht mehr benötigt da alles in onCompleteSession passiert
                },
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

/**
 * Sessions für ein bestimmtes Topic mit Datumsgruppierung.
 */
@Composable
private fun TopicSessionsContent(
    goal: com.example.fitnfocus.domain.LearningGoal,
    topic: String,
    sessions: List<com.example.fitnfocus.domain.StudySession>,
    isLoading: Boolean,
    isTopicCompleted: Boolean,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onSessionClick: (com.example.fitnfocus.domain.StudySession) -> Unit,
    onUpdateSessionStatus: (Int, com.example.fitnfocus.domain.SessionStatus) -> Unit,
    onUpdateSessionNotes: (Int, String) -> Unit,
    onMarkTopicCompleted: (Int, String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    // Gruppiere Sessions nach Datum
    val sessionsByDate = sessions.groupBy { it.date }

    Spacer(modifier = Modifier.height(28.dp))
    Column(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück"
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = topic,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = goal.moduleName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onAddClick) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Session hinzufügen",
                    tint = FitNFocusColors.PurplePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Topic-Status Card
        if (isTopicCompleted) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Thema abgeschlossen!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "Übersicht aller Sessions",
            style = MaterialTheme.typography.titleMedium
        )

//        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (sessions.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Noch keine Sessions erstellt. Probier es dochh mal aus. Klicke dazu einfach auf das Plus-Symbol.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sessions nach Datum gruppiert anzeigen
                sessionsByDate.forEach { (date, dateSessions) ->
                    item {
                        Text(
                            text = formatDateHeader(date),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(dateSessions.size) { index ->
                        val session = dateSessions[index]
                        EnhancedSessionCard(
                            session = session,
                            onClick = { onSessionClick(session) },
                            onStatusChange = { newStatus -> onUpdateSessionStatus(session.id, newStatus) },
                            onNotesChange = { notes -> onUpdateSessionNotes(session.id, notes) },
                            onMarkTopicCompleted = { isCompleted -> onMarkTopicCompleted(goal.id, topic, isCompleted) },
                            showTopicCompletionButton = !isTopicCompleted
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formatiert das Datum für die Anzeige als Header.
 */
@SuppressLint("NewApi")
private fun formatDateHeader(date: java.time.LocalDate): String {
    return try {
        val today = java.time.LocalDate.now()
        val yesterday = today.minusDays(1)

        when (date) {
            today -> "Heute"
            yesterday -> "Gestern"
            else -> {
                val formatter = java.time.format.DateTimeFormatter.ofPattern("dd. MMMM yyyy", java.util.Locale.GERMAN)
                date.format(formatter)
            }
        }
    } catch (e: Exception) {
        date.toString()
    }
}

/**
 * Erweiterte Session-Card mit Status, Notes und Topic-Completion.
 */
@Composable
private fun EnhancedSessionCard(
    session: com.example.fitnfocus.domain.StudySession,
    onClick: () -> Unit,
    onStatusChange: (com.example.fitnfocus.domain.SessionStatus) -> Unit,
    onNotesChange: (String) -> Unit,
    onMarkTopicCompleted: (Boolean) -> Unit,
    showTopicCompletionButton: Boolean,
    modifier: Modifier = Modifier
) {
    var showNotesDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showStatusMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FitNFocusColors.PurpleContainer.copy(alpha = 0.70f)
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${session.durationMinutes} min",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )

                    // Status-Anzeige
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        val statusColor = when (session.status) {
                            com.example.fitnfocus.domain.SessionStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                            com.example.fitnfocus.domain.SessionStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
                            com.example.fitnfocus.domain.SessionStatus.PLANNED -> MaterialTheme.colorScheme.onSurfaceVariant
                            com.example.fitnfocus.domain.SessionStatus.STOPPED -> MaterialTheme.colorScheme.error
                        }
                        val statusText = when (session.status) {
                            com.example.fitnfocus.domain.SessionStatus.COMPLETED -> "Abgeschlossen"
                            com.example.fitnfocus.domain.SessionStatus.IN_PROGRESS -> "In Bearbeitung"
                            com.example.fitnfocus.domain.SessionStatus.PLANNED -> "Geplant"
                            com.example.fitnfocus.domain.SessionStatus.STOPPED -> "Gestoppt"
                        }

                        Surface(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Status ändern Button
                Box {
                    IconButton(onClick = { showStatusMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Status ändern")
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Geplant") },
                            onClick = {
                                onStatusChange(com.example.fitnfocus.domain.SessionStatus.PLANNED)
                                showStatusMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Schedule, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("In Bearbeitung") },
                            onClick = {
                                onStatusChange(com.example.fitnfocus.domain.SessionStatus.IN_PROGRESS)
                                showStatusMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.PlayArrow, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Abgeschlossen") },
                            onClick = {
                                onStatusChange(com.example.fitnfocus.domain.SessionStatus.COMPLETED)
                                showStatusMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Check, null) }
                        )
                    }
                }
            }

            // Notizen anzeigen/bearbeiten
            if (session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = session.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,  // Mehr Zeilen anzeigen für angehängte Notizen
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Notiz hinzufügen/bearbeiten Button
                OutlinedButton(
                    onClick = { showNotesDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, FitNFocusColors.OrangeAccent)
                ) {
                    Icon(Icons.Default.Notes, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (session.notes.isBlank()) "Notiz" else "Bearbeiten", style = MaterialTheme.typography.labelSmall)
                }

                // Topic abschließen Button
                if (showTopicCompletionButton && session.status == com.example.fitnfocus.domain.SessionStatus.COMPLETED) {
                    Button(
                        onClick = { onMarkTopicCompleted(true) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thema fertig", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }

    // Notizen Dialog
    if (showNotesDialog) {
        var notesText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(session.notes) }

        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text("Notizen / Todos") },
            text = {
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Was wurde erledigt? Todos...") },
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onNotesChange(notesText)
                    showNotesDialog = false
                }) {
                    Text("Speichern")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

/**
 * Platzhalter-Inhalt für Bereiche, die noch nicht verfügbar sind.
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
