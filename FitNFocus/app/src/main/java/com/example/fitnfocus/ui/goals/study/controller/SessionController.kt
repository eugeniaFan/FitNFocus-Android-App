package com.example.fitnfocus.ui.goals.study.controller

import com.example.fitnfocus.calendar.toCalendarEventDataNow
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import com.example.fitnfocus.ui.goals.study.GoalTopicKey
import com.example.fitnfocus.ui.goals.study.LearningNavigationState
import com.example.fitnfocus.ui.goals.study.SessionDialogUiState
import com.example.fitnfocus.ui.goals.study.StudyUiEvent
import com.example.fitnfocus.ui.goals.study.TopicItem
import com.example.fitnfocus.ui.goals.study.overview.components.TopicStatusInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class SessionController (
    private val sessionRepository: SessionRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val topicStatusInteractor: TopicStatusInteractor,
    private val scope: CoroutineScope,
    private val getNavState: () -> LearningNavigationState,
    private val uiEvents: MutableSharedFlow<StudyUiEvent>,
    private val setNavState: (LearningNavigationState) -> Unit
) {
    private val _dialogState = MutableStateFlow(SessionDialogUiState())
    val dialogState: StateFlow<SessionDialogUiState> = _dialogState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _todaySessions = MutableStateFlow<List<StudySession>>(emptyList())
    val todaySessions: StateFlow<List<StudySession>> = _todaySessions.asStateFlow()

    private val _selectedSession = MutableStateFlow<StudySession?>(null)
    val selectedSession: StateFlow<StudySession?> = _selectedSession.asStateFlow()

    private val _lastSavedSession = MutableStateFlow<StudySession?>(null)
    val lastSavedSession: StateFlow<StudySession?> = _lastSavedSession.asStateFlow()

    private val _selectedTopicKey = MutableStateFlow<GoalTopicKey?>(null)

    // automatisch aktualisierte Sessions für TopicDetail
    @OptIn(ExperimentalCoroutinesApi::class)
    val topicSessions: StateFlow<List<StudySession>> =
        _selectedTopicKey
            .flatMapLatest { key ->
                if (key == null) {
                    flowOf(emptyList())
                } else {
                    sessionRepository.getSessionsForTopicFlow(key.topic, key.goalId)
                }
            }
            .stateIn(scope, SharingStarted.Companion.WhileSubscribed(5_000), emptyList())

    fun setShowAddDialog(value: Boolean) {
        if (value) _dialogState.update { it.copy(showAddDialog = true) }
        else _dialogState.value = SessionDialogUiState()
    }

    fun startSessionForTopic(goalId: Int, topic: String, moduleName: String) {
        _dialogState.update {
            it.copy(
                newTopic = topic,
                selectedTopic = TopicItem(name = topic, goalId = goalId, goalName = moduleName),
                showAddDialog = true
            )
        }
    }

    fun selectSession(session: StudySession?) {
        _selectedSession.value = session
    }

    fun bindTopicDetail(goalId: Int, topic: String) {
        _selectedTopicKey.value = GoalTopicKey(goalId = goalId, topic = topic)
    }

    fun clearTopicDetail() {
        _selectedTopicKey.value = null
    }

    fun loadSessionsForDate(date: LocalDate) {
        scope.launch {
            _isLoading.value = true
            try {
                _todaySessions.value = sessionRepository.getSessionsByDate(date)
            } catch (e: Exception) {
                uiEvents.tryEmit(StudyUiEvent.ShowMessage(e.message ?: "Fehler beim Laden der Sessions."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSessionStatus(sessionId: Int, status: SessionStatus) {
        scope.launch {
            sessionRepository.updateSessionStatus(sessionId, status)
            loadSessionsForDate(LocalDate.now())
            // topicSessions aktualisiert sich automatisch über Flow
        }
    }

    fun updateSessionNotes(sessionId: Int, notes: String) {
        scope.launch {
            sessionRepository.updateSessionNotes(sessionId, notes)
            // Optional: lokales UI-Update, damit es nicht “verschwindet”
            // topicSessions kommt aus DB-Flow, daher normalerweise stabil.
        }
    }

    fun updateSession(session: StudySession) {
        scope.launch {
            _isLoading.value = true
            try {
                sessionRepository.updateSession(session)
                _todaySessions.value = sessionRepository.getSessionsByDate(session.date)
                _selectedSession.value = null
                uiEvents.tryEmit(StudyUiEvent.ShowMessage("Session aktualisiert."))
            } catch (e: Exception) {
                uiEvents.tryEmit(StudyUiEvent.ShowMessage(e.message?: "Aktualisierung fehlgeschlagen."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSession(session: StudySession) {
        scope.launch {
            _isLoading.value = true
            try {
                sessionRepository.deleteSession(session)
                uiEvents.tryEmit(StudyUiEvent.ShowMessage("Session gelöscht."))
                _todaySessions.value = sessionRepository.getSessionsByDate(session.date)
                _selectedSession.value = null
            } catch (e: Exception) {
                uiEvents.tryEmit(StudyUiEvent.ShowMessage(e.message?:"Löschen fehlgeschlagen."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveSession(durationMinutes: Int, addToCalendar: Boolean, startTimer: Boolean) {
        val s = _dialogState.value
        val cleanedTopic = s.newTopic.trim()
        val today = LocalDate.now()

        if (cleanedTopic.isEmpty()) {
            uiEvents.tryEmit(StudyUiEvent.ShowMessage("Bitte ein Thema eingeben."))
            return
        }
        if (durationMinutes <= 0) {
            uiEvents.tryEmit(StudyUiEvent.ShowMessage("Bitte eine gültige Dauer eingeben."))
            return
        }

        val goalId = s.selectedTopic?.goalId

        val session = StudySession(
            id = 0,
            topic = cleanedTopic,
            durationMinutes = durationMinutes,
            date = today,
            goalId = goalId,
            status = SessionStatus.PLANNED,
            notes = ""
        )

        scope.launch {
            _isLoading.value = true
            try {
                val sessionId = sessionRepository.insertSession(session).toInt()

                topicStatusInteractor.markInProgress(cleanedTopic)

                val moduleName = if (goalId != null) {
                    learningGoalRepository.getGoalById(goalId)?.moduleName ?: "Lernen"
                } else "Lernen"

                _dialogState.value = SessionDialogUiState()
                _lastSavedSession.value = session.copy(id = sessionId)

                loadSessionsForDate(today)
                uiEvents.tryEmit(StudyUiEvent.CloseAddDialog)

                if (startTimer && goalId != null) {
                    setNavState(
                        LearningNavigationState.SessionTimer(
                            goalId = goalId,
                            topic = cleanedTopic,
                            moduleName = moduleName,
                            durationMinutes = durationMinutes,
                            sessionId = sessionId
                        )
                    )
                } else {
                    uiEvents.tryEmit(StudyUiEvent.ShowMessage("Session gespeichert."))
                }

                if (addToCalendar) {
                    uiEvents.emit(StudyUiEvent.OpenCalendarInsert(session.toCalendarEventDataNow()))
                }
            } catch (e: Exception) {
                uiEvents.tryEmit(StudyUiEvent.ShowMessage(e.message?: "Speichern fehlgeschlagen."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun navigateToTimer(session: StudySession) {
        scope.launch {
            val goalId = session.goalId
            val moduleName = if (goalId != null) {
                learningGoalRepository.getGoalById(goalId)?.moduleName ?: "Lernen"
            } else "Lernen"

            setNavState(
                LearningNavigationState.SessionTimer(
                    goalId = goalId ?: 0,
                    topic = session.topic,
                    moduleName = moduleName,
                    durationMinutes = session.durationMinutes,
                    sessionId = session.id
                )
            )
        }
    }

    /**
     * Wird nach Timer-Completion aufgerufen.
     * WICHTIG: Keine DB-Updates hier
     * Nur UI refresh + Navigation.
     */
    fun onTimerCompletedReturnToTopic(goalId: Int, topic: String) {
        setNavState(LearningNavigationState.TopicDetail(goalId = goalId, topic = topic))
        bindTopicDetail(goalId, topic)
        loadSessionsForDate(LocalDate.now())
    }

    fun onTimerStoppedReturnToOverview() {
        setNavState(LearningNavigationState.Overview)
        clearTopicDetail()
        loadSessionsForDate(LocalDate.now())
    }
}