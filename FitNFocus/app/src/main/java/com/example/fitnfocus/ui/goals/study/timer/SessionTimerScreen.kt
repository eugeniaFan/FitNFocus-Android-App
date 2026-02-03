package com.example.fitnfocus.ui.goals.study.timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnfocus.R

/**
 * Timer-Screen for Focus-Session with ice cube melting animation.
 *
 * @param uiState Current UI state from SessionTimerViewModel
 * @param onStartTimer Callback to start timer
 * @param onPauseTimer Callback to pause timer
 * @param onResumeTimer Callback to resume timer
 * @param onStopTimer Callback to stop early (partial completion)
 * @param onConfirmPartialSave Callback when user chooses "Save" in dialog
 * @param onDismissPartialSave Callback when user chooses "Discard" in dialog
 * @param onCompleteSession Callback when user completes session (after FINISHED) - includes DB save + navigation
 * @param onUpdateNotes Callback for notes changes
 * @param onUpdateMarkTopicCompleted Callback for checkbox changes
 * @param onAbort Callback to cancel without saving
 */
@Composable
fun SessionTimerScreen(
    uiState: SessionTimerUiState,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onConfirmPartialSave: () -> Unit,
    onDismissPartialSave: () -> Unit,
    onCompleteSession: () -> Unit,
    onUpdateNotes: (String) -> Unit,
    onUpdateMarkTopicCompleted: (Boolean) -> Unit,
    onAbort: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animations for ice cube (melting from top to bottom)
    val iceScale by animateFloatAsState(
        targetValue = if (uiState.timerState == TimerState.FINISHED) 0f else 1f - (uiState.progress * 0.7f),
        animationSpec = tween(durationMillis = 500),
        label = "iceScale"
    )

    val iceAlpha by animateFloatAsState(
        targetValue = if (uiState.timerState == TimerState.FINISHED) 0f else 1f - (uiState.progress * 0.3f),
        animationSpec = tween(durationMillis = 500),
        label = "iceAlpha"
    )

    val iceOffsetY by animateFloatAsState(
        targetValue = if (uiState.timerState == TimerState.FINISHED) 200f else uiState.progress * 100f,
        animationSpec = tween(durationMillis = 500),
        label = "iceOffsetY"
    )

    // Animation for collectible (appears when timer finishes)
    val coinScale by animateFloatAsState(
        targetValue = if (uiState.timerState == TimerState.FINISHED) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "coinScale"
    )

    val coinAlpha by animateFloatAsState(
        targetValue = if (uiState.timerState == TimerState.FINISHED) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "coinAlpha"
    )

    // Pulsing effect for coin
    val infiniteTransition = rememberInfiniteTransition(label = "coinPulse")
    val coinPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coinPulseAnim"
    )

    // Background gradient based on state
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            if (uiState.timerState == TimerState.FINISHED)
                Color(0xFFFFF8E1) // Warm yellow when finished
            else
                Color(0xFFE3F2FD), // Cool blue during timer
            if (uiState.timerState == TimerState.FINISHED)
                Color(0xFFFFECB3)
            else
                Color(0xFFBBDEFB)
        )
    )

    // Partial Save Dialog
    if (uiState.showSavePartialDialog) {
        PartialSaveDialog(
            elapsedSeconds = uiState.elapsedSeconds,
            notes = uiState.sessionNotes,
            onNotesChange = onUpdateNotes,
            onConfirm = onConfirmPartialSave,
            onDismiss = onDismissPartialSave
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .statusBarsPadding()
            .testTag("screen_session_timer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            SessionHeader(
                sessionTopic = uiState.sessionTopic,
                moduleName = uiState.moduleName
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Main animation area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Ice cube (melting)
                if (uiState.timerState != TimerState.FINISHED) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ice_cube),
                        contentDescription = "Schmelzender Eiswürfel",
                        modifier = Modifier
                            .size(220.dp)
                            .scale(iceScale)
                            .alpha(iceAlpha)
                            .offset(y = iceOffsetY.dp)
                    )

                    // Water droplet effect (visual)
                    if (uiState.progress > 0.2f && uiState.timerState == TimerState.RUNNING) {
                        WaterDroplets(progress = uiState.progress)
                    }
                }

                // Collectible (appears when finished)
                if (uiState.timerState == TimerState.FINISHED) {
                    FinishedReward(
                        coinScale = coinScale,
                        coinPulse = coinPulse,
                        coinAlpha = coinAlpha
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Timer display
            TimerDisplay(
                remainingSeconds = uiState.remainingSeconds,
                timerState = uiState.timerState
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Control buttons based on state
            TimerControls(
                timerState = uiState.timerState,
                sessionTopic = uiState.sessionTopic,
                sessionNotes = uiState.sessionNotes,
                markTopicAsCompleted = uiState.markTopicAsCompleted,
                onStartTimer = onStartTimer,
                onPauseTimer = onPauseTimer,
                onResumeTimer = onResumeTimer,
                onStopTimer = onStopTimer,
                onUpdateNotes = onUpdateNotes,
                onUpdateMarkTopicCompleted = onUpdateMarkTopicCompleted,
                onCompleteSession = onCompleteSession
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SessionHeader(
    sessionTopic: String,
    moduleName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = sessionTopic,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = moduleName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FinishedReward(
    coinScale: Float,
    coinPulse: Float,
    coinAlpha: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_collectible_coin),
            contentDescription = "Belohnungs-Münze",
            modifier = Modifier
                .size(180.dp)
                .scale(coinScale * coinPulse)
                .alpha(coinAlpha)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Geschafft!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF9800),
            modifier = Modifier
                .alpha(coinAlpha)
                .testTag("timer_finished_text")
        )
    }
}

@Composable
private fun TimerDisplay(
    remainingSeconds: Int,
    timerState: TimerState
) {
    Text(
        text = formatTime(remainingSeconds),
        style = MaterialTheme.typography.displayLarge.copy(
            fontSize = 72.sp,
            fontWeight = FontWeight.Light
        ),
        color = if (timerState == TimerState.FINISHED)
            Color(0xFF4CAF50)
        else
            MaterialTheme.colorScheme.onSurface
    )

    Text(
        text = when (timerState) {
            TimerState.IDLE -> "Bereit zum Starten"
            TimerState.RUNNING -> "Focus-Zeit läuft..."
            TimerState.PAUSED -> "Pausiert"
            TimerState.STOPPED -> "Gestoppt"
            TimerState.FINISHED -> "Session beendet!"
        },
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}