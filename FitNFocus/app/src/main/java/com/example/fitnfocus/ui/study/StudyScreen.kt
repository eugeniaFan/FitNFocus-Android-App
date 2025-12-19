package com.example.fitnfocus.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun StudyScreen(
    onBack: () -> Unit,
    viewModel: com.example.fitnfocus.viewmodel.StudyViewModel,
    snackbarHostState: SnackbarHostState
) {
    val subject by viewModel.subject.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val sessions by viewModel.todaySessions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedSession by viewModel.selectedSession.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()

    val today = LocalDate.now().toString()

    LaunchedEffect(today) {
        viewModel.loadSessionsForDate(today)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Today’s sessions", style = MaterialTheme.typography.titleLarge)

                IconButton(onClick = { viewModel.setShowAddDialog(true) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add session")
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (sessions.isEmpty()) {
                Text(
                    "No sessions saved today yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sessions) { s ->
                        SessionCard(
                            session = s,
                            onClick = { viewModel.selectSession(s) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            androidx.compose.material3.Button(onClick = onBack) {
                Text("Back")
            }
        }
    }

    // -------- Add Dialog (Checkbox + optional Kalender) --------
    if (showAddDialog) {
        AddSessionDialog(
            subject = subject,
            duration = duration,
            isLoading = isLoading,
            onSubjectChange = viewModel::onSubjectChange,
            onDurationChange = viewModel::onDurationChange,
            onDismiss = { viewModel.setShowAddDialog(false) },
            onSave = { addToCalendar ->
                viewModel.saveStudySession(today, addToCalendar)
            }
        )
    }

    // -------- Edit Dialog --------
    if (selectedSession != null) {
        EditSessionDialog(
            session = selectedSession!!,
            isLoading = isLoading,
            onDismiss = { viewModel.selectSession(null) },
            onUpdate = { updated -> viewModel.updateSession(updated) },
            onDelete = { toDelete -> viewModel.deleteSession(toDelete) }
        )
    }
}
