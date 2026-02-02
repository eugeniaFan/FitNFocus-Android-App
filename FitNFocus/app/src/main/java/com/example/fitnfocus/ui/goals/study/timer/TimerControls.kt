package com.example.fitnfocus.ui.goals.study.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Control-Buttons je nach Timer-Status.
 */
@Composable
internal fun TimerControls(
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
            IdleControls(onStartTimer = onStartTimer)
        }

        TimerState.RUNNING -> {
            RunningControls(
                onPauseTimer = onPauseTimer,
                onStopTimer = onStopTimer
            )
        }

        TimerState.PAUSED -> {
            PausedControls(
                onResumeTimer = onResumeTimer,
                onStopTimer = onStopTimer
            )
        }

        TimerState.FINISHED -> {
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
            // Dialog wird im Haupt-Screen angezeigt
        }
    }
}

@Composable
private fun IdleControls(onStartTimer: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onStartTimer,
            modifier = Modifier
                .size(80.dp)
                .testTag("timer_play_button"),
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
}

@Composable
private fun RunningControls(
    onPauseTimer: () -> Unit,
    onStopTimer: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
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

@Composable
private fun PausedControls(
    onResumeTimer: () -> Unit,
    onStopTimer: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
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

