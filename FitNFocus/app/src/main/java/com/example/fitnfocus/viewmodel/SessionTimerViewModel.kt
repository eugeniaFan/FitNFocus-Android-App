package com.example.fitnfocus.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.usecase.SetTopicCompletionUseCase
import com.example.fitnfocus.ui.goals.study.timer.SessionTimerUiState
import com.example.fitnfocus.ui.goals.study.timer.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel für den Session-Timer.
 *
 * Verantwortlichkeiten:
 * - Timer-Logik (Start, Pause, Stop, Tick-Loop)
 * - Dialog-State Management
 * - Notizen-Text verwalten
 * - Persistente Updates (elapsedSeconds, status, notes)
 *
 * NICHT zuständig für:
 * - Focus-Übersicht
 * - Navigation-Status
 * - Welche Session als nächstes kommt

 */
@RequiresApi(Build.VERSION_CODES.O)
class SessionTimerViewModel(
    private val sessionRepository: SessionRepository,
    private val setTopicCompletionUseCase: SetTopicCompletionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionTimerUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    /**
     * Initialisiert den Timer mit Session-Daten.
     * Wird von FocusScreen aufgerufen wenn eine Session gestartet wird.
     */
    fun initializeSession(
        sessionId: Int,
        sessionTopic: String,
        moduleName: String,
        durationMinutes: Int,
        goalId: Int?
    ) {
        val totalSeconds = durationMinutes * 60
        _uiState.update {
            SessionTimerUiState(
                sessionId = sessionId,
                sessionTopic = sessionTopic,
                moduleName = moduleName,
                goalId = goalId,
                totalSeconds = totalSeconds,
                remainingSeconds = totalSeconds,
                timerState = TimerState.IDLE
            )
        }
    }

    /**
     * Startet den Timer.
     */
    fun startTimer() {
        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        startTickLoop()
    }

    /**
     * Pausiert den Timer.
     */
    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerState = TimerState.PAUSED) }
    }

    /**
     * Setzt den Timer fort nach Pause.
     */
    fun resumeTimer() {
        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        startTickLoop()
    }

    /**
     * Stoppt den Timer vorzeitig.
     * Zeigt den "Partial Completion" Dialog.
     */
    fun stopTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                timerState = TimerState.STOPPED,
                showSavePartialDialog = true
            )
        }
    }

    /**
     * User bestätigt "Partial Completion" im Dialog.
     * Speichert elapsedSeconds und setzt Status auf STOPPED.
     * @param onComplete Callback der aufgerufen wird wenn die DB-Operation abgeschlossen ist
     */
    fun confirmPartialSave(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val state = _uiState.value
            try {
                // elapsedSeconds in DB speichern
                sessionRepository.updateElapsedSeconds(state.sessionId, state.elapsedSeconds)

                // Status auf STOPPED setzen
                sessionRepository.updateSessionStatus(state.sessionId, SessionStatus.STOPPED)

                // Notizen anhängen wenn vorhanden (nicht überschreiben!)
                if (state.sessionNotes.isNotBlank()) {
                    sessionRepository.appendSessionNotes(state.sessionId, state.sessionNotes)
                }

                // Dialog schließen (FocusScreen reagiert auf timerState change)
                _uiState.update {
                    it.copy(showSavePartialDialog = false)
                }

                // Callback aufrufen nachdem alles gespeichert wurde
                onComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Fehler beim Speichern: ${e.message}")
                }
            }
        }
    }

    /**
     * User lehnt "Partial Completion" ab.
     * Session wird nicht gespeichert, Timer wird zurückgesetzt.
     * @param onDismissed Callback der aufgerufen wird nachdem der State zurückgesetzt wurde
     */
    fun dismissPartialSave(onDismissed: () -> Unit = {}) {
        timerJob?.cancel()
        _uiState.update { SessionTimerUiState() }
        onDismissed()
    }

    /**
     * Bricht den Timer komplett ab (ohne Speichern).
     */
    fun cancelTimer() {
        timerJob?.cancel()
        _uiState.update { SessionTimerUiState() }
    }

    /**
     * Timer ist bei 0 angekommen (FINISHED).
     * Zeigt die Completion Card.
     */
    private fun onTimerFinished() {
        _uiState.update {
            it.copy(
                timerState = TimerState.FINISHED,
                showCompletionCard = true
            )
        }
    }

    /**
     * User schließt Session ab (nach FINISHED).
     * Speichert als COMPLETED mit optional: Notizen, Topic-Completion.
     * @param onComplete Callback der aufgerufen wird wenn die DB-Operation abgeschlossen ist
     */
    fun completeSession(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val state = _uiState.value
            try {
                // Session-Status auf COMPLETED setzen
                sessionRepository.updateSessionStatus(state.sessionId, SessionStatus.COMPLETED)

                // elapsedSeconds = totalSeconds (vollständig)
                sessionRepository.updateElapsedSeconds(state.sessionId, state.totalSeconds)

                // Notizen anhängen wenn vorhanden (nicht überschreiben!)
                if (state.sessionNotes.isNotBlank()) {
                    sessionRepository.appendSessionNotes(state.sessionId, state.sessionNotes)
                }

                // Optional: Topic als abgeschlossen markieren via UseCase (Single Source of Truth)
                if (state.markTopicAsCompleted && state.goalId != null && state.goalId > 0) {
                    // UseCase kümmert sich um:
                    // 1. TopicProgress markieren
                    // 2. Alle offenen Sessions für dieses Topic abschließen
                    setTopicCompletionUseCase(state.goalId, state.sessionTopic, true)
                }

                // State zurücksetzen
                _uiState.update { SessionTimerUiState() }

                // Callback aufrufen nachdem alles gespeichert wurde
                onComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Fehler beim Abschließen: ${e.message}")
                }
            }
        }
    }

    /**
     * Aktualisiert Notizen-Text.
     */
    fun updateNotes(notes: String) {
        _uiState.update { it.copy(sessionNotes = notes) }
    }

    /**
     * Aktualisiert "Topic abgeschlossen" Checkbox.
     */
    fun updateMarkTopicCompleted(isCompleted: Boolean) {
        _uiState.update { it.copy(markTopicAsCompleted = isCompleted) }
    }

    /**
     * Löscht Fehlermeldung.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Tick-Loop für den Timer.
     * Läuft in einer Coroutine und decrementiert remainingSeconds jede Sekunde.
     */
    private fun startTickLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.timerState == TimerState.RUNNING) {
                delay(1000L)
                if (_uiState.value.timerState == TimerState.RUNNING) {
                    _uiState.update {
                        it.copy(remainingSeconds = it.remainingSeconds - 1)
                    }
                }
            }
            // Timer bei 0 angekommen
            if (_uiState.value.remainingSeconds <= 0) {
                onTimerFinished()
            }
        }
    }

    /**
     * Cleanup bei ViewModel-Destroy.
     */
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}