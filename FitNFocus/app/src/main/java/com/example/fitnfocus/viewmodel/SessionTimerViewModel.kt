package com.example.fitnfocus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.LearningGoalRepository
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
 * ViewModel for the session timer.
 *
 * Responsibilities:
 * - Timer lifecycle (start, pause, stop, tick loop)
 * - Dialog state and notes editing
 * - Persisting session status, time, and notes
 */
class SessionTimerViewModel(
    private val sessionRepository: SessionRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val setTopicCompletionUseCase: SetTopicCompletionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionTimerUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    /**
     * Initializes timer with session data.
     * Called from FocusScreen when a session is started.
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
     * Loads a session from storage and initializes the timer state.
     * Called from SessionTimerRoute.
     */
    fun loadAndInitializeSession(sessionId: Int) {
        viewModelScope.launch {
            val session = sessionRepository.getSessionById(sessionId)
            session?.let {
                val moduleName = it.goalId?.let { goalId ->
                    learningGoalRepository.getGoalById(goalId)?.moduleName
                } ?: "Lernen"

                initializeSession(
                    sessionId = it.id,
                    sessionTopic = it.topic,
                    moduleName = moduleName,
                    durationMinutes = it.durationMinutes,
                    goalId = it.goalId
                )
            }
        }
    }

    /**
     * Starts the timer.
     */
    fun startTimer() {
        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        startTickLoop()
    }

    /**
     * Pauses the timer.
     */
    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerState = TimerState.PAUSED) }
    }

    /**
     * Resumes the timer after a pause.
     */
    fun resumeTimer() {
        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        startTickLoop()
    }

    /**
     * Stops the timer early and shows the partial completion dialog.
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
     * Confirms partial completion and persists elapsed time, status, and notes.
     */
    fun confirmPartialSave(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val state = _uiState.value
            try {
                sessionRepository.updateElapsedSeconds(state.sessionId, state.elapsedSeconds)
                sessionRepository.updateSessionStatus(state.sessionId, SessionStatus.STOPPED)

                // Append notes without overwriting existing ones.
                if (state.sessionNotes.isNotBlank()) {
                    sessionRepository.appendSessionNotes(state.sessionId, state.sessionNotes)
                }

                _uiState.update {
                    it.copy(showSavePartialDialog = false)
                }

                onComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Fehler beim Speichern: ${e.message}")
                }
            }
        }
    }

    /**
     * Cancels partial completion and resets the timer state.
     */
    fun dismissPartialSave(onDismissed: () -> Unit = {}) {
        timerJob?.cancel()
        _uiState.update { SessionTimerUiState() }
        onDismissed()
    }

    /**
     * Aborts the timer without saving.
     */
    fun cancelTimer() {
        timerJob?.cancel()
        _uiState.update { SessionTimerUiState() }
    }

    /**
     * Handles timer completion and shows the completion card.
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
     * Completes the session and optionally marks the topic as done.
     */
    fun completeSession(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val state = _uiState.value
            try {
                sessionRepository.updateSessionStatus(state.sessionId, SessionStatus.COMPLETED)
                sessionRepository.updateElapsedSeconds(state.sessionId, state.totalSeconds)

                // Append notes without overwriting existing ones.
                if (state.sessionNotes.isNotBlank()) {
                    sessionRepository.appendSessionNotes(state.sessionId, state.sessionNotes)
                }

                if (state.markTopicAsCompleted && state.goalId != null && state.goalId > 0) {
                    try {
                        setTopicCompletionUseCase(state.goalId, state.sessionTopic, true)
                    } catch (e: Exception) {

                        Log.e(
                            "SessionTimerViewModel",
                            "Failed to mark topic completed",
                            e
                        )
                        _uiState.update {
                            it.copy(errorMessage = "Thema konnte nicht als erledigt markiert werden. Die Session konnte beendet werden.")
                        }
                    }
                }
                // Success: reset UI state and call callback
                _uiState.update { SessionTimerUiState() }
                onComplete()
            } catch (e: Exception) {
                Log.e("SessionTimerViewModel", "Error completing session", e)
                _uiState.update {
                    it.copy(errorMessage = "Fehler beim Abschließen ser Session: ${e.message}")
                }
            }
        }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(sessionNotes = notes) }
    }

    fun updateMarkTopicCompleted(isCompleted: Boolean) {
        _uiState.update { it.copy(markTopicAsCompleted = isCompleted) }
    }

    /**
     * Tick loop for the timer running in coroutine.
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
            if (_uiState.value.remainingSeconds <= 0) {
                onTimerFinished()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}