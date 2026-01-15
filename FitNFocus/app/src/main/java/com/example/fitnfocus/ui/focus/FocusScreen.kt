package com.example.fitnfocus.ui.focus

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.R
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.ui.study.timer.SessionTimerScreen
import com.example.fitnfocus.ui.study.timer.SessionTimerViewModel
import com.example.fitnfocus.viewmodel.FocusViewModel


/**
 * Focus-Screen: Zeigt aktive/geplante Sessions und gesammelte Münzen.
 * @param autoStartSessionId Wenn gesetzt, wird der Timer für diese Session direkt gestartet.
 * @param onNavigateToCollection Callback zur Sammlung-Navigation
 * @param onSessionCompleted Callback wenn Session erfolgreich beendet wird → Focus-Bereich
 * @param onSessionStopped Callback wenn Session gestoppt/abgebrochen wird → Dashboard
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FocusScreen(
    autoStartSessionId: Int? = null,
    onNavigateToCollection: () -> Unit = {},
    onSessionCompleted: () -> Unit = {},
    onSessionStopped: () -> Unit = {},  // Stop/Cancel → Dashboard
    viewModel: FocusViewModel = viewModel(factory = AppViewModelProvider.Factory),
    timerViewModel: SessionTimerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val completedSessionsCount by viewModel.completedSessionsCount.collectAsState()
    val activeTimerSession by viewModel.activeTimerSession.collectAsState()
    val timerUiState by timerViewModel.uiState.collectAsState()

    val uiState = FocusUiState(
        coinsTotal = completedSessionsCount,
        hasActiveTimer = activeTimerSession != null
    )

    LaunchedEffect(Unit) {
        viewModel.loadTodaySessions()
    }

    // Automatisch Timer starten wenn sessionId übergeben wurde
    LaunchedEffect(autoStartSessionId) {
        if (autoStartSessionId != null && autoStartSessionId > 0) {
            viewModel.startTimerForSessionId(autoStartSessionId)
        }
    }

    // Initialisiere SessionTimerViewModel wenn activeTimerSession sich ändert
    LaunchedEffect(activeTimerSession) {
        val session = activeTimerSession
        if (session != null && timerUiState.sessionId != session.id) {
            timerViewModel.initializeSession(
                sessionId = session.id,
                sessionTopic = session.topic,
                moduleName = viewModel.getModuleNameForSession(session),
                durationMinutes = session.durationMinutes,
                goalId = session.goalId
            )
        }
    }

    // Timer-Screen
    if (uiState.hasActiveTimer) {
        // Safety-Check (Race Condition vermeiden)
        val session = activeTimerSession
        if (session == null) {
            // Fallback: falls hasActiveTimer true wäre, session aber null ist
            FocusContent(
                state = uiState.copy(hasActiveTimer = false),
                onNavigateToCollection = onNavigateToCollection
            )
            return
        }

        SessionTimerScreen(
            uiState = timerUiState,
            onStartTimer = { timerViewModel.startTimer() },
            onPauseTimer = { timerViewModel.pauseTimer() },
            onResumeTimer = { timerViewModel.resumeTimer() },
            onStopTimer = { timerViewModel.stopTimer() },
            onConfirmPartialSave = {
                timerViewModel.confirmPartialSave {
                    // Nach DB-Operation: Navigation zum Dashboard
                    viewModel.cancelTimer()
                    onSessionStopped()
                }
            },
            onDismissPartialSave = { timerViewModel.dismissPartialSave() },
            onCompleteSession = {
                timerViewModel.completeSession {
                    // Nach DB-Operation: Navigation zum Focus-Bereich
                    viewModel.cancelTimer()
                    viewModel.loadTodaySessions()
                    onSessionCompleted()
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
                viewModel.cancelTimer()
                onSessionStopped()
            }
        )
        return
    }
    FocusContent(
        state = uiState,
        onNavigateToCollection = onNavigateToCollection
    )
}

@Composable
private fun FocusContent(
    state: FocusUiState,
    onNavigateToCollection: () -> Unit,
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F1E8), Color(0xFFE6DAC8))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Focus",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Normal,

            )
            Spacer(modifier = Modifier.height(12.dp))
            // Deine Erfolge
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8639E0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Deine Erfolge",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBFBFC)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Münzen gesamt",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE5E5EC)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_collectible_coin),
                            contentDescription = "Münze",
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = state.coinsTotal.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB300)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Deine Sammlung
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8639E0)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Deine Sammlung",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBFBFC)
                    )

                    Text(
                        text = "Finde deinen Fokus-Typ. Das sind feste Archetypen zur Orientierung.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE5E5EC)
                    )

                    // Preview Grid (4 fixe Typen)
                    val types = FocusTypes.staticTypes()
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp,
                        )) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FocusTypePreviewCard(types[0], Modifier.weight(1f))
                            FocusTypePreviewCard(types[1], Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FocusTypePreviewCard(types[2], Modifier.weight(1f))
                            FocusTypePreviewCard(types[3], Modifier.weight(1f))
                        }
                    }

                    Button(
                        onClick = onNavigateToCollection,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC107),
                        )
                    ) {
                        Text(
                            "Zur Sammlung",
                            color = Color(0xFF34087A)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
private fun FocusTypePreviewCard(
    type: FocusTypeUi,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFFFFF)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(type.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(2.dp))
            Text(
                type.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
