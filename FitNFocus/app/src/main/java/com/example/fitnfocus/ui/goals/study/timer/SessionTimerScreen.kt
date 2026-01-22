package com.example.fitnfocus.ui.goals.study.timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnfocus.R

/**
 * Timer-Screen für eine Focus-Session mit Eiswürfel-Schmelz-Animation.
 *
 * Refactored: Verwendet jetzt SessionTimerViewModel statt lokalen State.
 *
 * @param uiState Der aktuelle UI-State vom SessionTimerViewModel
 * @param onStartTimer Callback zum Starten des Timers
 * @param onPauseTimer Callback zum Pausieren des Timers
 * @param onResumeTimer Callback zum Fortsetzen des Timers
 * @param onStopTimer Callback zum vorzeitigen Stoppen (Partial Completion)
 * @param onConfirmPartialSave Callback wenn User im Dialog "Speichern" wählt
 * @param onDismissPartialSave Callback wenn User im Dialog "Verwerfen" wählt
 * @param onCompleteSession Callback wenn User Session abschließt (nach FINISHED)
 * @param onUpdateNotes Callback für Notizen-Änderungen
 * @param onUpdateMarkTopicCompleted Callback für Checkbox-Änderungen
 * @param onSessionCompleted Callback zur Navigation (von FocusScreen)
 * @param onAbort Callback zum Abbrechen ohne Speichern
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
    onSessionCompleted: () -> Unit,
    onAbort: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animations für Eiswürfel (Schmelzen von oben nach unten)
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

    // Animation für die Sammelfigur (erscheint wenn Timer fertig)
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

    // Pulsierender Effekt für die Münze
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

    // Hintergrund-Gradient basierend auf Zustand
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            if (uiState.timerState == TimerState.FINISHED)
                Color(0xFFFFF8E1) // Warmes Gelb wenn fertig
            else
                Color(0xFFE3F2FD), // Kühles Blau während Timer
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
            onConfirm = {
                onConfirmPartialSave()
                onSessionCompleted()
            },
            onDismiss = {
                // Nur onDismissPartialSave aufrufen - dieser handhabt bereits
                // das Zurücksetzen und die Navigation via Callback
                onDismissPartialSave()
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.sessionTopic,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = uiState.moduleName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Haupt-Animation Bereich
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Eiswürfel (schmelzt)
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

                    // Wassertropfen-Effekt (optisch)
                    if (uiState.progress > 0.2f && uiState.timerState == TimerState.RUNNING) {
                        WaterDroplets(progress = uiState.progress)
                    }
                }

                // Sammelfigur (erscheint wenn fertig)
                if (uiState.timerState == TimerState.FINISHED) {
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
                            modifier = Modifier.alpha(coinAlpha)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Timer-Anzeige
            Text(
                text = formatTime(uiState.remainingSeconds),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light
                ),
                color = if (uiState.timerState == TimerState.FINISHED)
                    Color(0xFF4CAF50)
                else
                    MaterialTheme.colorScheme.onSurface
            )

            // Status-Text
            Text(
                text = when (uiState.timerState) {
                    TimerState.IDLE -> "Bereit zum Starten"
                    TimerState.RUNNING -> "Focus-Zeit läuft..."
                    TimerState.PAUSED -> "Pausiert"
                    TimerState.STOPPED -> "Gestoppt"
                    TimerState.FINISHED -> "Session beendet!"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Control-Buttons basierend auf Status
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
                onCompleteSession = {
                    onCompleteSession()
                    onSessionCompleted()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Control-Buttons je nach Timer-Status.
 */
@Composable
private fun TimerControls(
    timerState: TimerState,
    sessionTopic: String,
    sessionNotes: String,
    markTopicAsCompleted: Boolean,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResumeTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onUpdateNotes: (String) -> Unit,
    onUpdateMarkTopicCompleted: (Boolean) -> Unit,
    onCompleteSession: () -> Unit
) {
    when (timerState) {
        TimerState.IDLE -> {
            // Play-Button zum Starten
            FilledIconButton(
                onClick = onStartTimer,
                modifier = Modifier.size(80.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Starten",
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tippe zum Starten",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TimerState.RUNNING -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Pause-Button
                FilledIconButton(
                    onClick = onPauseTimer,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        Icons.Default.Pause,
                        contentDescription = "Pausieren",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Stop-Button (Session vorzeitig beenden -> Partial Completion)
                FilledIconButton(
                    onClick = onStopTimer,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = "Beenden",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        TimerState.PAUSED -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Fortsetzen-Button
                FilledIconButton(
                    onClick = onResumeTimer,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Fortsetzen",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Stop-Button (Session vorzeitig beenden -> Partial Completion)
                FilledIconButton(
                    onClick = onStopTimer,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = "Beenden",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        TimerState.FINISHED -> {
            // Session abschließen UI mit Notiz-Eingabe
            CompletionCard(
                sessionTopic = sessionTopic,
                sessionNotes = sessionNotes,
                markTopicAsCompleted = markTopicAsCompleted,
                onUpdateNotes = onUpdateNotes,
                onUpdateMarkTopicCompleted = onUpdateMarkTopicCompleted,
                onCompleteSession = onCompleteSession
            )
        }

        TimerState.STOPPED -> {
            // Dialog wird oben angezeigt
        }
    }
}

/**
 * Card für Session-Abschluss (nach FINISHED).
 */
@Composable
private fun CompletionCard(
    sessionTopic: String,
    sessionNotes: String,
    markTopicAsCompleted: Boolean,
    onUpdateNotes: (String) -> Unit,
    onUpdateMarkTopicCompleted: (Boolean) -> Unit,
    onCompleteSession: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Notiz-Eingabe
            OutlinedTextField(
                value = sessionNotes,
                onValueChange = onUpdateNotes,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notizen (optional)") },
                placeholder = { Text("Was hast du gelernt? Todos...") },
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox für "Thema abgeschlossen"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Checkbox(
                    checked = markTopicAsCompleted,
                    onCheckedChange = onUpdateMarkTopicCompleted
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Thema abgeschlossen",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (markTopicAsCompleted)
                            "\"$sessionTopic\" wird als fertig markiert"
                        else
                            "Thema bleibt offen für weitere Sessions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCompleteSession,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Session abschließen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Dialog für "Partial Completion" (vorzeitiges Stoppen).
 */
@Composable
private fun PartialSaveDialog(
    elapsedSeconds: Int,
    notes: String,
    onNotesChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val elapsedMinutes = elapsedSeconds / 60
    val elapsedSecs = elapsedSeconds % 60

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Session beendet")
        },
        text = {
            Column {
                Text(
                    text = "Du hast ${elapsedMinutes} Min. ${elapsedSecs} Sek. fokussiert.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Möchtest du deinen Fortschritt speichern?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Notizen (optional)") },
                    placeholder = { Text("Was hast du geschafft?") },
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Verwerfen")
            }
        }
    )
}

/**
 * Wassertropfen-Animation während des Schmelzens.
 */
@Composable
private fun WaterDroplets(progress: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "droplets")

    val dropletOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dropletOffset"
    )

    val dropletAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dropletAlpha"
    )

    // Kleine Wassertropfen unter dem Eiswürfel
    Box(
        modifier = Modifier
            .offset(y = (80 + dropletOffset).dp)
            .size((8 * progress).dp.coerceAtLeast(4.dp))
            .alpha(dropletAlpha * progress)
            .clip(CircleShape)
            .background(Color(0xFF64B5F6))
    )
}

/**
 * Formatiert Sekunden zu MM:SS.
 */
private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

