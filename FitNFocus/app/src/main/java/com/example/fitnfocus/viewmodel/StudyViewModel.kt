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
import com.example.fitnfocus.domain.toCalendarEventData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Repräsentiert ein Topic zur Auswahl im Dropdown.
 */
data class TopicItem(
    val name: String,
    val goalId: Int?,           // null = manuell hinzugefügt
    val goalName: String? = null // Name des Moduls für Anzeige
)

/**
 * Status eines Topics basierend auf Sessions und TopicProgress.
 */
enum class TopicStatus {
    NOT_STARTED,   // Keine Session erstellt
    IN_PROGRESS,   // Session erstellt, aber nicht abgeschlossen
    COMPLETED      // Topic als abgeschlossen markiert (in TopicProgress)
}

/**
 * Navigation-State für den Lern-Bereich.
 */
sealed class LearningNavigationState {
    data object Overview : LearningNavigationState()
    data class GoalDetail(val goalId: Int) : LearningNavigationState()
    data class TopicDetail(val goalId: Int, val topic: String) : LearningNavigationState()
    data class SessionTimer(
        val goalId: Int,
        val topic: String,
        val moduleName: String,
        val durationMinutes: Int,
        val sessionId: Int
    ) : LearningNavigationState()
}

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
    private val topicProgressRepository: TopicProgressRepository
) : ViewModel() {

    private val dateFormatter: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    } else {
        null
    }

    // ==================== LERNZIEL HINZUFÜGEN (BOTTOM SHEET) ====================

    private val _showAddGoalSheet = MutableStateFlow(false)
    val showAddGoalSheet = _showAddGoalSheet.asStateFlow()

    private val _newGoalModuleName = MutableStateFlow("")
    val newGoalModuleName = _newGoalModuleName.asStateFlow()

    private val _newGoalExamDateText = MutableStateFlow("") // Format: dd.MM.yyyy
    val newGoalExamDateText = _newGoalExamDateText.asStateFlow()

    private val _newGoalTopics = MutableStateFlow<List<String>>(emptyList())
    val newGoalTopics = _newGoalTopics.asStateFlow()

    private val _newGoalCurrentTopic = MutableStateFlow("")
    val newGoalCurrentTopic = _newGoalCurrentTopic.asStateFlow()

    private val _isSavingGoal = MutableStateFlow(false)
    val isSavingGoal = _isSavingGoal.asStateFlow()

    fun openAddGoalSheet() {
        _showAddGoalSheet.value = true
    }

    fun closeAddGoalSheet() {
        _showAddGoalSheet.value = false
        _newGoalModuleName.value = ""
        _newGoalExamDateText.value = ""
        _newGoalTopics.value = emptyList()
        _newGoalCurrentTopic.value = ""
    }

    fun onNewGoalModuleNameChange(value: String) {
        _newGoalModuleName.value = value
    }

    fun onNewGoalExamDateTextChange(value: String) {
        _newGoalExamDateText.value = value
    }

    fun onNewGoalCurrentTopicChange(value: String) {
        _newGoalCurrentTopic.value = value
    }

    fun addNewGoalTopic() {
        val topic = _newGoalCurrentTopic.value.trim()
        if (topic.isEmpty()) return
        if (_newGoalTopics.value.any { it.equals(topic, ignoreCase = true) }) {
            _newGoalCurrentTopic.value = ""
            return
        }

        _newGoalTopics.value = _newGoalTopics.value + topic
        _newGoalCurrentTopic.value = ""
    }

    fun removeNewGoalTopic(topic: String) {
        _newGoalTopics.value = _newGoalTopics.value - topic
    }

    fun saveNewLearningGoal() {
        viewModelScope.launch {
            val module = _newGoalModuleName.value.trim()
            if (module.isEmpty()) return@launch

            _isSavingGoal.value = true
            try {
                val rawGerman = _newGoalExamDateText.value.trim().ifBlank { null }

                val examDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    rawGerman?.let {
                        runCatching {
                            LocalDate.parse(it, dateFormatter)
                        }.getOrNull()
                    }
                } else {
                    null
                }

                val goal = LearningGoal(
                    moduleName = module,
                    topics = _newGoalTopics.value,
                    examDate = examDate
                )

                learningGoalRepository.insertGoal(goal)

                closeAddGoalSheet()
                _learningNavState.value = LearningNavigationState.Overview
            } finally {
                _isSavingGoal.value = false
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
                val hasSessions = sessionRepository.hasSessionsForTopic(topic)
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
    fun loadSessionsForTopic(topic: String) {
        // Vorherigen Job canceln
        loadTopicSessionsJob?.cancel()

        loadTopicSessionsJob = viewModelScope.launch {
            sessionRepository.getSessionsForTopicFlow(topic).collect { sessions ->
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
        loadSessionsForTopic(topic)
    }

    fun navigateBackToOverview() {
        _learningNavState.value = LearningNavigationState.Overview
        refreshAllProgress()
    }

    fun navigateBackToGoalDetail(goal: LearningGoal) {
        _learningNavState.value = LearningNavigationState.GoalDetail(goalId = goal.id)
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
     * Speichert in TopicProgress-Tabelle und schließt bei Bedarf alle Sessions ab.
     */
    fun toggleTopicProgress(goalId: Int, topicName: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                topicProgressRepository.markTopicCompleted(goalId, topicName, isCompleted)

                // Wenn Topic abgeschlossen wird: alle offenen Sessions auch abschließen
                if (isCompleted) {
                    sessionRepository.completeAllSessionsForTopic(topicName)
                }

                _topicProgress.update { current ->
                    current.toMutableMap().apply {
                        put(topicName, isCompleted)
                    }
                }
                _topicStatusMap.update { current ->
                    current.toMutableMap().apply {
                        put(topicName, if (isCompleted) TopicStatus.COMPLETED else TopicStatus.IN_PROGRESS)
                    }
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
                loadSessionsForTopic(navState.topic)
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
     * Speichert in TopicProgress-Tabelle und schließt alle offenen Sessions ab.
     */
    fun markTopicAsCompleted(goalId: Int, topic: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                // Prüfen ob Topic bereits den gewünschten Status hat
                val currentStatus = topicProgressRepository.isTopicCompleted(goalId, topic)
                if (currentStatus == isCompleted) {
                    // Status ist bereits korrekt, nichts zu tun
                    return@launch
                }

                topicProgressRepository.markTopicCompleted(goalId, topic, isCompleted)

                // Wenn Topic abgeschlossen wird: alle offenen Sessions auch abschließen
                if (isCompleted) {
                    sessionRepository.completeAllSessionsForTopic(topic)
                }

                _topicProgress.update { current ->
                    current.toMutableMap().apply {
                        put(topic, isCompleted)
                    }
                }
                _topicStatusMap.update { current ->
                    current.toMutableMap().apply {
                        put(topic, if (isCompleted) TopicStatus.COMPLETED else TopicStatus.IN_PROGRESS)
                    }
                }

                loadSessionsForTopic(topic)
                _uiEvents.tryEmit(
                    if (isCompleted) StudyUiEvent.ShowMessage("Thema als abgeschlossen markiert!")
                    else StudyUiEvent.ShowMessage("Thema-Status zurückgesetzt.")
                )
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Fehler beim Aktualisieren des Thema-Status."))
            }
        }
    }

    /**
     * Startet eine Session für ein bestimmtes Topic.
     */
    fun startSessionForTopic(topic: String) {
        _topic.value = topic
        _selectedTopic.value = TopicItem(name = topic, goalId = null)
        setShowAddDialog(true)
    }

    // ==================== UI INPUTS ====================

    private val _topic = MutableStateFlow("")
    val topic = _topic.asStateFlow()

    private val _duration = MutableStateFlow("")
    val duration = _duration.asStateFlow()

    // Ausgewähltes Topic (aus Dropdown oder manuell)
    private val _selectedTopic = MutableStateFlow<TopicItem?>(null)
    val selectedTopic = _selectedTopic.asStateFlow()

    // Manueller Eingabemodus vs. Dropdown
    private val _isManualInput = MutableStateFlow(false)
    val isManualInput = _isManualInput.asStateFlow()

    // ==================== UI STATES ====================

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog = _showAddDialog.asStateFlow()

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

    private val _availableTopics = MutableStateFlow<List<TopicItem>>(emptyList())
    val availableTopics = _availableTopics.asStateFlow()

    // ==================== EVENTS ====================

    private val _uiEvents = MutableSharedFlow<StudyUiEvent>(extraBufferCapacity = 8)
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            learningGoals.collect { goals ->
                updateAvailableTopics(goals)
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
                    val hasSessions = sessionRepository.hasSessionsForTopic(topic)
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

    private fun updateAvailableTopics(goals: List<LearningGoal>) {
        val topics = mutableListOf<TopicItem>()

        goals.forEach { goal ->
            topics.add(TopicItem(
                name = goal.moduleName,
                goalId = goal.id,
                goalName = null
            ))

            goal.topics.forEach { topicName ->
                topics.add(TopicItem(
                    name = topicName,
                    goalId = goal.id,
                    goalName = goal.moduleName
                ))
            }
        }

        _availableTopics.value = topics
    }

    // ==================== DIALOG ACTIONS ====================

    fun setShowAddDialog(value: Boolean) {
        _showAddDialog.value = value
        if (!value) {
            resetForm()
        }
    }

    fun selectSession(session: StudySession?) {
        _selectedSession.value = session
    }

    // ==================== FORM INPUTS ====================

    fun onTopicChange(newValue: String) {
        _topic.value = newValue
    }

    fun onDurationChange(newValue: String) {
        _duration.value = newValue
    }

    fun selectTopic(topicItem: TopicItem) {
        _selectedTopic.value = topicItem
        _topic.value = topicItem.name
        _isManualInput.value = false
    }

    fun toggleManualInput() {
        _isManualInput.value = !_isManualInput.value
        if (_isManualInput.value) {
            _selectedTopic.value = null
        }
    }

    fun setManualInput(enabled: Boolean) {
        _isManualInput.value = enabled
        if (enabled) {
            _selectedTopic.value = null
        }
    }

    private fun resetForm() {
        _topic.value = ""
        _duration.value = ""
        _selectedTopic.value = null
        _isManualInput.value = false
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
                _todaySessions.value = sessionRepository.getSessionsByDate(session.date)
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
                _todaySessions.value = sessionRepository.getSessionsByDate(session.date)
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
    fun saveSession(addToCalendar: Boolean = false, goalIdForNewTopic: Int? = null, startTimer: Boolean = false) {
        val cleanedTopic = _topic.value.trim()
        val minutes = _duration.value.toIntOrNull()
        val today = LocalDate.now()

        if (cleanedTopic.isEmpty()) {
            _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Bitte ein Thema eingeben."))
            return
        }

        if (minutes == null || minutes <= 0) {
            _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Bitte eine gültige Dauer eingeben."))
            return
        }

        val goalId = _selectedTopic.value?.goalId ?: goalIdForNewTopic

        val session = StudySession(
            id = 0,
            topic = cleanedTopic,
            durationMinutes = minutes,
            date = today,
            goalId = goalId,
            status = SessionStatus.PLANNED,
            notes = ""
        )

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sessionId = sessionRepository.insertSessionAndGetId(session)

                if (_isManualInput.value && goalIdForNewTopic != null) {
                    addTopicToGoal(goalIdForNewTopic, cleanedTopic)
                }

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

                resetForm()
                _lastSavedSession.value = session.copy(id = sessionId.toInt())

                loadSessionsForDate(today)

                val navState = _learningNavState.value
                if (navState is LearningNavigationState.TopicDetail && navState.topic == cleanedTopic) {
                    loadSessionsForTopic(cleanedTopic)
                }

                _uiEvents.tryEmit(StudyUiEvent.CloseAddDialog)

                if (startTimer && goalId != null) {
                    _learningNavState.value = LearningNavigationState.SessionTimer(
                        goalId = goalId,
                        topic = cleanedTopic,
                        moduleName = moduleName,
                        durationMinutes = minutes,
                        sessionId = sessionId.toInt()
                    )
                } else {
                    _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Session gespeichert."))
                }

                if (addToCalendar) {
                    val eventData = session.toCalendarEventData()
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
     * Hinweis: Notizen werden bereits im SessionTimerViewModel gespeichert (appendSessionNotes),
     * daher hier NICHT nochmal speichern um Überschreiben zu vermeiden.
     */
    fun onTimerCompleted(sessionId: Int, goalId: Int, topic: String, markTopicCompleted: Boolean, notes: String = "") {
        viewModelScope.launch {
            try {
                // Session auf COMPLETED setzen (falls noch nicht geschehen)
                sessionRepository.updateSessionStatus(sessionId, SessionStatus.COMPLETED)

                // WICHTIG: Notizen werden NICHT hier gespeichert!
                // Sie wurden bereits im SessionTimerViewModel mit appendSessionNotes() angehängt.

                // Topic als abgeschlossen markieren (nur wenn goalId gültig ist)
                if (markTopicCompleted && goalId > 0) {
                    // Prüfen ob Topic bereits abgeschlossen ist
                    val alreadyCompleted = topicProgressRepository.isTopicCompleted(goalId, topic)

                    if (!alreadyCompleted) {
                        topicProgressRepository.markTopicCompleted(goalId, topic, true)
                    }

                    // Alle offenen Sessions für dieses Topic abschließen
                    sessionRepository.completeAllSessionsForTopic(topic)

                    _topicProgress.update { current ->
                        current.toMutableMap().apply {
                            put(topic, true)
                        }
                    }
                    _topicStatusMap.update { current ->
                        current.toMutableMap().apply {
                            put(topic, TopicStatus.COMPLETED)
                        }
                    }
                }

                // Navigation zurück zum TopicDetail
                val navState = _learningNavState.value
                if (navState is LearningNavigationState.SessionTimer) {
                    _learningNavState.value = LearningNavigationState.TopicDetail(
                        goalId = navState.goalId,
                        topic = topic
                    )
                    loadSessionsForTopic(topic)
                }

                _uiEvents.tryEmit(StudyUiEvent.ShowMessage(
                    if (markTopicCompleted && goalId > 0) "Session & Thema abgeschlossen! 🎉" else "Session abgeschlossen!"
                ))
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Fehler beim Abschließen der Session."))
            }
        }
    }

    private suspend fun addTopicToGoal(goalId: Int, newTopic: String) {
        try {
            val goal = learningGoalRepository.getGoalById(goalId) ?: return

            if (newTopic !in goal.topics) {
                val updatedGoal = goal.copy(
                    topics = goal.topics + newTopic
                )
                learningGoalRepository.updateGoal(updatedGoal)
            }
        } catch (e: Exception) {
            // Ignorieren
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
                // Auch TopicProgress löschen
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

    // ==================== LERNZIEL BEARBEITEN (EDIT BOTTOM SHEET) ====================

    private val _showEditGoalSheet = MutableStateFlow(false)
    val showEditGoalSheet = _showEditGoalSheet.asStateFlow()

    private val _editGoalId = MutableStateFlow<Int?>(null)
    val editGoalId = _editGoalId.asStateFlow()

    private val _editGoalModuleName = MutableStateFlow("")
    val editGoalModuleName = _editGoalModuleName.asStateFlow()

    private val _editGoalExamDateText = MutableStateFlow("")
    val editGoalExamDateText = _editGoalExamDateText.asStateFlow()

    private val _editGoalTopics = MutableStateFlow<List<String>>(emptyList())
    val editGoalTopics = _editGoalTopics.asStateFlow()

    private val _editGoalCurrentTopic = MutableStateFlow("")
    val editGoalCurrentTopic = _editGoalCurrentTopic.asStateFlow()

    private val _isSavingEditGoal = MutableStateFlow(false)
    val isSavingEditGoal = _isSavingEditGoal.asStateFlow()

    fun openEditGoalSheet(goal: LearningGoal) {
        _editGoalId.value = goal.id
        _editGoalModuleName.value = goal.moduleName
        _editGoalTopics.value = goal.topics

        _editGoalExamDateText.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && goal.examDate != null) {
            goal.examDate.format(dateFormatter)
        } else {
            ""
        }

        _editGoalCurrentTopic.value = ""
        _showEditGoalSheet.value = true
    }

    fun closeEditGoalSheet() {
        _showEditGoalSheet.value = false
        _editGoalId.value = null
        _editGoalModuleName.value = ""
        _editGoalExamDateText.value = ""
        _editGoalTopics.value = emptyList()
        _editGoalCurrentTopic.value = ""
    }

    fun onEditGoalModuleNameChange(value: String) {
        _editGoalModuleName.value = value
    }

    fun onEditGoalExamDateTextChange(value: String) {
        _editGoalExamDateText.value = value
    }

    fun onEditGoalCurrentTopicChange(value: String) {
        _editGoalCurrentTopic.value = value
    }

    fun addEditGoalTopic() {
        val topicName = _editGoalCurrentTopic.value.trim()
        if (topicName.isEmpty()) return
        if (_editGoalTopics.value.any { it.equals(topicName, ignoreCase = true) }) {
            _editGoalCurrentTopic.value = ""
            return
        }

        _editGoalTopics.value = _editGoalTopics.value + topicName
        _editGoalCurrentTopic.value = ""
    }

    fun removeEditGoalTopic(topicName: String) {
        _editGoalTopics.value = _editGoalTopics.value - topicName
    }

    fun saveEditedLearningGoal() {
        viewModelScope.launch {
            val goalId = _editGoalId.value ?: return@launch
            val module = _editGoalModuleName.value.trim()
            if (module.isEmpty()) return@launch

            _isSavingEditGoal.value = true
            try {
                val rawGerman = _editGoalExamDateText.value.trim().ifBlank { null }

                val examDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    rawGerman?.let {
                        runCatching {
                            LocalDate.parse(it, dateFormatter)
                        }.getOrNull()
                    }
                } else {
                    null
                }

                val existingGoal = learningGoalRepository.getGoalById(goalId)
                if (existingGoal != null) {
                    val updatedGoal = existingGoal.copy(
                        moduleName = module,
                        topics = _editGoalTopics.value,
                        examDate = examDate
                    )
                    learningGoalRepository.updateGoal(updatedGoal)
                    _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Lernziel aktualisiert."))
                }

                closeEditGoalSheet()
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Aktualisierung fehlgeschlagen."))
            } finally {
                _isSavingEditGoal.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestAddLastSavedToCalendar() {
        val session = _lastSavedSession.value ?: run {
            _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Keine Session zum Hinzufügen."))
            return
        }
        val eventData = session.toCalendarEventData()
        _uiEvents.tryEmit(StudyUiEvent.OpenCalendarInsert(eventData))
    }
}