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
 * Route for SessionTimerScreen.
 * Loads session data and initializes the ViewModel.
 *
 * @param sessionId ID of session to start
 * @param onSessionCompleted Callback when session successfully completed -> Focus area
 * @param onSessionStopped Callback when session stopped/aborted -> Home
 */
@Composable
fun SessionTimerRoute(
    sessionId: Int,
    onSessionCompleted: () -> Unit,
    onSessionStopped: () -> Unit,
    timerViewModel: SessionTimerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by timerViewModel.uiState.collectAsState()

    // Load session data and initialize ViewModel (ONCE at start)
    LaunchedEffect(sessionId) {
        if (uiState.sessionId != sessionId) {
            timerViewModel.loadAndInitializeSession(sessionId)
        }
    }

    // Show empty screen while session is loading (prevents flicker)
    if (uiState.sessionId == 0) {
        Box(Modifier.fillMaxSize())
        return
    }

    // Render SessionTimerScreen
    SessionTimerScreen(
        uiState = uiState,
        onStartTimer = { timerViewModel.startTimer() },
        onPauseTimer = { timerViewModel.pauseTimer() },
        onResumeTimer = { timerViewModel.resumeTimer() },
        onStopTimer = { timerViewModel.stopTimer() },
        onConfirmPartialSave = {
            timerViewModel.confirmPartialSave {
                // After DB operation: Navigate to Home
                onSessionStopped()
            }
        },
        onDismissPartialSave = {
            timerViewModel.dismissPartialSave {
                // After reset: Navigate to Home
                onSessionStopped()
            }
        },
        onCompleteSession = {
            timerViewModel.completeSession {
                // After DB operation: Navigate to Focus area
                onSessionCompleted()
            }
        },
        onUpdateNotes = { timerViewModel.updateNotes(it) },
        onUpdateMarkTopicCompleted = { timerViewModel.updateMarkTopicCompleted(it) },
        onAbort = {
            // Stop/Cancel without saving -> Home
            timerViewModel.cancelTimer()
            onSessionStopped()
        }
    )
}
