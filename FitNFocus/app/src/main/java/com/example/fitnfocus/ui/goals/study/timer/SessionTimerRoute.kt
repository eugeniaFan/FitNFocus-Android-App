package com.example.fitnfocus.ui.goals.study.timer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.viewmodel.SessionTimerViewModel

/**
 * Route für den SessionTimerScreen.
 * Lädt Session-Daten und initialisiert das ViewModel.
 *
 * @param sessionId Die ID der zu startenden Session
 * @param onSessionCompleted Callback wenn Session erfolgreich beendet → Focus-Bereich
 * @param onSessionStopped Callback wenn Session gestoppt/abgebrochen → Home
 */
@Composable
fun SessionTimerRoute(
    sessionId: Int,
    onSessionCompleted: () -> Unit,
    onSessionStopped: () -> Unit,
    timerViewModel: SessionTimerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by timerViewModel.uiState.collectAsState()

    // Session-Daten laden und ViewModel initialisieren (EINMALIG beim Start)
    LaunchedEffect(sessionId) {
        if (uiState.sessionId != sessionId) {
            timerViewModel.loadAndInitializeSession(sessionId)
        }
    }

    // Zeige leeren Screen während Session geladen wird (verhindert Flicker)
    if (uiState.sessionId == 0) {
        Box(Modifier.fillMaxSize())
        return
    }

    // SessionTimerScreen rendern
    SessionTimerScreen(
        uiState = uiState,
        onStartTimer = { timerViewModel.startTimer() },
        onPauseTimer = { timerViewModel.pauseTimer() },
        onResumeTimer = { timerViewModel.resumeTimer() },
        onStopTimer = { timerViewModel.stopTimer() },
        onConfirmPartialSave = {
            timerViewModel.confirmPartialSave {
                // Nach DB-Operation: Navigation zum Home
                onSessionStopped()
            }
        },
        onDismissPartialSave = {
            timerViewModel.dismissPartialSave {
                // Nach Reset: Navigation zum Home
                onSessionStopped()
            }
        },
        onCompleteSession = {
            timerViewModel.completeSession {
                // Nach DB-Operation: Navigation zum Focus-Bereich
                onSessionCompleted()
            }
        },
        onUpdateNotes = { timerViewModel.updateNotes(it) },
        onUpdateMarkTopicCompleted = { timerViewModel.updateMarkTopicCompleted(it) },
        onAbort = {
            // Stop/Cancel ohne Speichern → Home
            timerViewModel.cancelTimer()
            onSessionStopped()
        }
    )
}

