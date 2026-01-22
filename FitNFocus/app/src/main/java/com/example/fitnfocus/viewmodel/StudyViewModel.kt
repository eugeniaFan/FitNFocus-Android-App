package com.example.fitnfocus.viewmodel

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.fitnfocus.calendar.toCalendarEventDataNow
import com.example.fitnfocus.ui.goals.study.AddGoalUiState
import com.example.fitnfocus.ui.goals.study.EditGoalUiState
import com.example.fitnfocus.ui.goals.study.LearningNavigationState
import com.example.fitnfocus.ui.goals.study.SessionDialogUiState
import com.example.fitnfocus.ui.goals.study.StudyUiEvent
import com.example.fitnfocus.ui.goals.study.TopicItem
import com.example.fitnfocus.ui.goals.study.TopicStatus
import com.example.fitnfocus.ui.goals.study.sessions.util.formatGermanDate
import com.example.fitnfocus.ui.goals.study.sessions.util.parseGermanDate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel für den Lern-/Study-Bereich.
 * Verwaltet Sessions, Topics und Lernziele.
 *
 * HINWEIS: Verwendet LocalDate (API 26+). Für API < 26 funktionieren
 * einige Features nicht vollständig.
 */
@SuppressLint("NewApi") // API-Checks werden manuell durchgeführt
class StudyViewModel(
    private val sessionRepository: SessionRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val topicProgressRepository: TopicProgressRepository,
    private val setTopicCompletionUseCase: SetTopicCompletionUseCase
) : ViewModel() {

    // ==================== GRUPPIERTE UI STATES ====================

    // Add Goal State
    private val _addGoalState = MutableStateFlow(AddGoalUiState())
    val addGoalState = _addGoalState.asStateFlow()

    // Edit Goal State
    private val _editGoalState = MutableStateFlow(EditGoalUiState())
    val editGoalState = _editGoalState.asStateFlow()

    // Session Dialog State
    private val _sessionDialogState = MutableStateFlow(SessionDialogUiState())
    val sessionDialogState = _sessionDialogState.asStateFlow()

    // ==================== LERNZIEL HINZUFÜGEN ====================

    fun openAddGoalSheet() {
        _addGoalState.update { it.copy(showSheet = true) }
    }

    fun closeAddGoalSheet() {
        _addGoalState.value = AddGoalUiState()
    }

    fun onNewGoalModuleNameChange(value: String) {
        _addGoalState.update { it.copy(moduleName = value) }
    }

    fun onNewGoalExamDateTextChange(value: String) {
        _addGoalState.update { it.copy(examDateText = value) }
    }

    fun onNewGoalCurrentTopicChange(value: String) {
        _addGoalState.update { it.copy(currentTopic = value) }
    }

    fun addNewGoalTopic() {
        val topic = _addGoalState.value.currentTopic.trim()
        if (topic.isEmpty()) return
        if (_addGoalState.value.topics.any { it.equals(topic, ignoreCase = true) }) {
            _addGoalState.update { it.copy(currentTopic = "") }
            return
        }
        _addGoalState.update { it.copy(topics = it.topics + topic, currentTopic = "") }
    }

    fun removeNewGoalTopic(topic: String) {
        _addGoalState.update { it.copy(topics = it.topics - topic) }
    }

    fun saveNewLearningGoal() {
        viewModelScope.launch {
            val state = _addGoalState.value
            val module = state.moduleName.trim()
            if (module.isEmpty()) return@launch

            _addGoalState.update { it.copy(isSaving = true) }
            try {
                val examDate = parseGermanDate(state.examDateText)

                val goal = LearningGoal(
                    moduleName = module,
                    topics = state.topics,
                    examDate = examDate
                )

                learningGoalRepository.insertGoal(goal)
                closeAddGoalSheet()
                _learningNavState.value = LearningNavigationState.Overview
            } finally {
                _addGoalState.update { it.copy(isSaving = false) }
            }
        }
    }

    // ==================== FOKUS-BEREICH ====================

    private val _selectedFocusArea = MutableStateFlow(FocusArea.LEARNING)
    val selectedFocusArea = _selectedFocusArea.asStateFlow()

    fun selectFocusArea(area: FocusArea) {
        _selectedFocusArea.value = area
        _learningNavState.value = LearningNavigationState.Overview
    }

    // ==================== LERN-BEREICH NAVIGATION ====================

    private val _learningNavState = MutableStateFlow<LearningNavigationState>(LearningNavigationState.Overview)
    val learningNavState = _learningNavState.asStateFlow()

    // Topic-Fortschritt (Topic-Name -> erledigt) - basierend auf TopicProgress-Tabelle
    private val _topicProgress = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val topicProgress = _topicProgress.asStateFlow()

    // Topic-Status (Topic-Name -> Status) - basierend auf Sessions + TopicProgress
    private val _topicStatusMap = MutableStateFlow<Map<String, TopicStatus>>(emptyMap())
    val topicStatusMap = _topicStatusMap.asStateFlow()

    // Sessions für aktuelles Topic (im TopicDetail)
    private val _topicSessions = MutableStateFlow<List<StudySession>>(emptyList())
    val topicSessions = _topicSessions.asStateFlow()

    /**
     * Lädt den Status aller Topics basierend auf Sessions und TopicProgress aus der DB.
     */
    fun loadTopicStatuses(goalId: Int, topics: List<String>) {
        viewModelScope.launch {
            val statusMap = mutableMapOf<String, TopicStatus>()
            val progressMap = mutableMapOf<String, Boolean>()

            topics.forEach { topic ->
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

    // Job für das Laden von Topic-Sessions (um Race-Conditions zu vermeiden)
    private var loadTopicSessionsJob: kotlinx.coroutines.Job? = null

    /**
     * Lädt alle Sessions für ein bestimmtes Topic (sortiert nach Datum).
     * Cancelled vorherige Lade-Jobs um Race-Conditions zu vermeiden.
     */
    fun loadSessionsForTopic(topic: String, goalId: Int) {
        // Vorherigen Job canceln
        loadTopicSessionsJob?.cancel()

        loadTopicSessionsJob = viewModelScope.launch {
            sessionRepository.getSessionsForTopicFlow(topic, goalId).collect { sessions ->
                _topicSessions.value = sessions
            }
        }
    }

    fun navigateToGoalDetail(goal: LearningGoal) {
        _learningNavState.value = LearningNavigationState.GoalDetail(goalId = goal.id)
        loadTopicStatuses(goal.id, goal.topics)
    }

    fun navigateToTopicDetail(goal: LearningGoal, topic: String) {
        _learningNavState.value = LearningNavigationState.TopicDetail(goalId = goal.id, topic = topic)
        loadSessionsForTopic(topic, goal.id)
    }

    fun navigateBackToOverview() {
        _learningNavState.value = LearningNavigationState.Overview
        refreshAllProgress()
    }

    fun navigateToGoalDetailById(goalId: Int) {
        _learningNavState.value = LearningNavigationState.GoalDetail(goalId = goalId)
        viewModelScope.launch {
            val goal = learningGoalRepository.getGoalById(goalId)
            goal?.let { loadTopicStatuses(it.id, it.topics) }
        }
    }

    /**
     * Markiert ein Topic als erledigt oder nicht erledigt.
     * Verwendet SetTopicCompletionUseCase als Single Source of Truth.
     */
    fun toggleTopicProgress(goalId: Int, topicName: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                val wasChanged = setTopicCompletionUseCase(goalId, topicName, isCompleted)

                // Lokale State Maps aktualisieren
                _topicProgress.update { current ->
                    current.toMutableMap().apply { put(topicName, isCompleted) }
                }
                _topicStatusMap.update { current ->
                    current.toMutableMap().apply {
                        put(topicName, if (isCompleted) TopicStatus.COMPLETED else TopicStatus.IN_PROGRESS)
                    }
                }

                if (wasChanged) {
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

    /**
     * Aktualisiert den Status einer Session.
     */
    fun updateSessionStatus(sessionId: Int, status: SessionStatus) {
        viewModelScope.launch {
            sessionRepository.updateSessionStatus(sessionId, status)
            val navState = _learningNavState.value
            if (navState is LearningNavigationState.TopicDetail) {
                loadSessionsForTopic(navState.topic, navState.goalId)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val today = LocalDate.now()
                loadSessionsForDate(today)
            }
        }
    }

    /**
     * Aktualisiert die Notizen einer Session.
     * Wartet auf das DB-Update und aktualisiert dann die lokale Liste.
     */
    fun updateSessionNotes(sessionId: Int, notes: String) {
        viewModelScope.launch {
            // DB-Update ausführen
            sessionRepository.updateSessionNotes(sessionId, notes)

            // Lokale Liste direkt aktualisieren (ohne Flow neu zu starten)
            // Das verhindert das "Verschwinden" der Session
            _topicSessions.value = _topicSessions.value.map { session ->
                if (session.id == sessionId) {
                    session.copy(notes = notes)
                } else {
                    session
                }
            }
        }
    }

    /**
     * Markiert ein Topic als abgeschlossen.
     * Alias für toggleTopicProgress - verwendet SetTopicCompletionUseCase.
     */
    fun markTopicAsCompleted(goalId: Int, topic: String, isCompleted: Boolean) {
        toggleTopicProgress(goalId, topic, isCompleted)
        // Sessions für dieses Topic neu laden
        viewModelScope.launch {
            loadSessionsForTopic(topic, goalId)
        }
    }

    /**
     * Startet eine Session für ein bestimmtes Topic.
     */
    fun startSessionForTopic(goalId: Int, topic: String, moduleName: String) {
        _sessionDialogState.update {
            it.copy(
                newTopic = topic,
                selectedTopic = TopicItem(
                    name = topic,
                    goalId = goalId,
                    goalName = moduleName
                ),
                showAddDialog = true
            )
        }
    }

    // ==================== UI STATES ====================

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _todaySessions = MutableStateFlow<List<StudySession>>(emptyList())
    val todaySessions = _todaySessions.asStateFlow()

    private val _selectedSession = MutableStateFlow<StudySession?>(null)
    val selectedSession = _selectedSession.asStateFlow()

    private val _lastSavedSession = MutableStateFlow<StudySession?>(null)
    val lastSavedSession = _lastSavedSession.asStateFlow()

    // ==================== LERNZIELE & TOPICS ====================

    val learningGoals = learningGoalRepository.getActiveGoals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ==================== EVENTS ====================

    private val _uiEvents = MutableSharedFlow<StudyUiEvent>(extraBufferCapacity = 8)
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            learningGoals.collect { goals ->
                loadAllTopicStatuses(goals)
            }
        }
    }

    private fun loadAllTopicStatuses(goals: List<LearningGoal>) {
        viewModelScope.launch {
            val allStatusMap = mutableMapOf<String, TopicStatus>()
            val allProgressMap = mutableMapOf<String, Boolean>()

            goals.forEach { goal ->
                goal.topics.forEach { topic ->
                    val hasSessions = sessionRepository.hasSessionsForTopic(topic, goal.id)
                    val isCompleted = topicProgressRepository.isTopicCompleted(goal.id, topic)

                    allStatusMap[topic] = when {
                        isCompleted -> TopicStatus.COMPLETED
                        hasSessions -> TopicStatus.IN_PROGRESS
                        else -> TopicStatus.NOT_STARTED
                    }
                    allProgressMap[topic] = isCompleted
                }
            }

            _topicStatusMap.value = allStatusMap
            _topicProgress.value = allProgressMap
        }
    }

    fun refreshAllProgress() {
        viewModelScope.launch {
            val goals = learningGoals.value
            loadAllTopicStatuses(goals)
        }
    }

    // ==================== DIALOG ACTIONS ====================

    fun setShowAddDialog(value: Boolean) {
        if (value) {
            _sessionDialogState.update { it.copy(showAddDialog = true) }
        } else {
            _sessionDialogState.value = SessionDialogUiState()
        }
    }

    fun selectSession(session: StudySession?) {
        _selectedSession.value = session
    }

    // ==================== DATA OPERATIONS ====================

    fun loadSessionsForDate(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sessions = sessionRepository.getSessionsByDate(date)
                _todaySessions.value = sessions
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Fehler beim Laden der Sessions."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSession(session: StudySession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                sessionRepository.updateSession(session)

                // Today-Sessions aktualisieren
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    _todaySessions.value = sessionRepository.getSessionsByDate(session.date)
                }

                // Topic-Sessions aktualisieren (für die aktuelle Detail-Ansicht)
                val navState = _learningNavState.value
                if (navState is LearningNavigationState.TopicDetail) {
                    loadSessionsForTopic(navState.topic, navState.goalId)
                }

                _selectedSession.value = null
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Session aktualisiert."))
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Aktualisierung fehlgeschlagen."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSession(session: StudySession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                sessionRepository.deleteSession(session)
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Session gelöscht."))

                // Today-Sessions aktualisieren
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    _todaySessions.value = sessionRepository.getSessionsByDate(session.date)
                }

                // Topic-Sessions aktualisieren (für die aktuelle Detail-Ansicht)
                val navState = _learningNavState.value
                if (navState is LearningNavigationState.TopicDetail) {
                    loadSessionsForTopic(navState.topic, navState.goalId)
                }

                _selectedSession.value = null
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Löschen fehlgeschlagen."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Speichert eine neue Session.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveSession(durationMinutes: Int, addToCalendar: Boolean = false, startTimer: Boolean = false) {
        val state = _sessionDialogState.value
        val cleanedTopic = state.newTopic.trim()
        val today = LocalDate.now()

        if (cleanedTopic.isEmpty()) {
            _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Bitte ein Thema eingeben."))
            return
        }

        if (durationMinutes <= 0) {
            _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Bitte eine gültige Dauer eingeben."))
            return
        }

        val goalId = state.selectedTopic?.goalId

        val session = StudySession(
            id = 0,
            topic = cleanedTopic,
            durationMinutes = durationMinutes,
            date = today,
            goalId = goalId,
            status = SessionStatus.PLANNED,
            notes = ""
        )

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sessionId = sessionRepository.insertSession(session)


                _topicStatusMap.update { current ->
                    current.toMutableMap().apply {
                        put(cleanedTopic, TopicStatus.IN_PROGRESS)
                    }
                }

                val moduleName = if (goalId != null) {
                    learningGoalRepository.getGoalById(goalId)?.moduleName ?: "Lernen"
                } else {
                    "Lernen"
                }

                // Form zurücksetzen
                _sessionDialogState.value = SessionDialogUiState()
                _lastSavedSession.value = session.copy(id = sessionId.toInt())

                loadSessionsForDate(today)

                val navState = _learningNavState.value
                if (navState is LearningNavigationState.TopicDetail && navState.topic == cleanedTopic) {
                    loadSessionsForTopic(cleanedTopic, navState.goalId)
                }

                _uiEvents.tryEmit(StudyUiEvent.CloseAddDialog)

                if (startTimer && goalId != null) {
                    _learningNavState.value = LearningNavigationState.SessionTimer(
                        goalId = goalId,
                        topic = cleanedTopic,
                        moduleName = moduleName,
                        durationMinutes = durationMinutes,
                        sessionId = sessionId.toInt()
                    )
                } else {
                    _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Session gespeichert."))
                }

                if (addToCalendar) {
                    val eventData = session.toCalendarEventDataNow()
                    _uiEvents.emit(StudyUiEvent.OpenCalendarInsert(eventData))
                }
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Speichern fehlgeschlagen."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun navigateToTimer(session: StudySession) {
        viewModelScope.launch {
            val goalId = session.goalId
            val moduleName = if (goalId != null) {
                learningGoalRepository.getGoalById(goalId)?.moduleName ?: "Lernen"
            } else {
                "Lernen"
            }

            _learningNavState.value = LearningNavigationState.SessionTimer(
                goalId = goalId ?: 0,
                topic = session.topic,
                moduleName = moduleName,
                durationMinutes = session.durationMinutes,
                sessionId = session.id
            )
        }
    }

    /**
     * Wird aufgerufen wenn der Timer abgeschlossen wird.
     * Verwendet SetTopicCompletionUseCase für Topic-Completion.
     */
    fun onTimerCompleted(sessionId: Int, goalId: Int, topic: String, markTopicCompleted: Boolean, notes: String = "") {
        viewModelScope.launch {
            try {
                // Session auf COMPLETED setzen (falls noch nicht geschehen)
                sessionRepository.updateSessionStatus(sessionId, SessionStatus.COMPLETED)

                // Topic als abgeschlossen markieren via UseCase (nur wenn goalId gültig ist)
                if (markTopicCompleted && goalId > 0) {
                    setTopicCompletionUseCase(goalId, topic, true)

                    _topicProgress.update { current ->
                        current.toMutableMap().apply { put(topic, true) }
                    }
                    _topicStatusMap.update { current ->
                        current.toMutableMap().apply { put(topic, TopicStatus.COMPLETED) }
                    }
                }

                // Navigation zurück zum TopicDetail
                val navState = _learningNavState.value
                if (navState is LearningNavigationState.SessionTimer) {
                    _learningNavState.value = LearningNavigationState.TopicDetail(
                        goalId = navState.goalId,
                        topic = topic
                    )
                    loadSessionsForTopic(topic, goalId)
                }

                _uiEvents.tryEmit(StudyUiEvent.ShowMessage(
                    if (markTopicCompleted && goalId > 0) "Session & Thema abgeschlossen! 🎉" else "Session abgeschlossen!"
                ))
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Fehler beim Abschließen der Session."))
            }
        }
    }


    // ==================== LÖSCHEN (CONFIRMATION) ====================

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
                _learningNavState.value = LearningNavigationState.Overview
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Löschen fehlgeschlagen."))
            } finally {
                _goalPendingDelete.value = null
            }
        }
    }

    // ==================== LERNZIEL BEARBEITEN ====================

    fun openEditGoalSheet(goal: LearningGoal) {
        _editGoalState.value = EditGoalUiState(
            showSheet = true,
            goalId = goal.id,
            moduleName = goal.moduleName,
            examDateText = formatGermanDate(goal.examDate),
            topics = goal.topics,
            currentTopic = ""
        )
    }

    fun closeEditGoalSheet() {
        _editGoalState.value = EditGoalUiState()
    }

    fun onEditGoalModuleNameChange(value: String) {
        _editGoalState.update { it.copy(moduleName = value) }
    }

    fun onEditGoalExamDateTextChange(value: String) {
        _editGoalState.update { it.copy(examDateText = value) }
    }

    fun onEditGoalCurrentTopicChange(value: String) {
        _editGoalState.update { it.copy(currentTopic = value) }
    }

    fun addEditGoalTopic() {
        val topicName = _editGoalState.value.currentTopic.trim()
        if (topicName.isEmpty()) return
        if (_editGoalState.value.topics.any { it.equals(topicName, ignoreCase = true) }) {
            _editGoalState.update { it.copy(currentTopic = "") }
            return
        }
        _editGoalState.update { it.copy(topics = it.topics + topicName, currentTopic = "") }
    }

    fun removeEditGoalTopic(topicName: String) {
        _editGoalState.update { it.copy(topics = it.topics - topicName) }
    }

    fun saveEditedLearningGoal() {
        viewModelScope.launch {
            val state = _editGoalState.value
            val goalId = state.goalId
            val module = state.moduleName.trim()
            if (module.isEmpty()) return@launch

            _editGoalState.update { it.copy(isSaving = true) }
            try {
                val examDate = parseGermanDate(state.examDateText)

                val existingGoal = learningGoalRepository.getGoalById(goalId)
                if (existingGoal != null) {
                    val updatedGoal = existingGoal.copy(
                        moduleName = module,
                        topics = state.topics,
                        examDate = examDate
                    )
                    learningGoalRepository.updateGoal(updatedGoal)
                    _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Lernziel aktualisiert."))
                }

                closeEditGoalSheet()
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Aktualisierung fehlgeschlagen."))
            } finally {
                _editGoalState.update { it.copy(isSaving = false) }
            }
        }
    }
}