package com.example.fitnfocus.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.viewmodel.HomeViewModel
import com.example.fitnfocus.viewmodel.TodayLearningItem
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.filled.Pause


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFocus: () -> Unit = {},  // Navigiert zum Focus-Tab
    onStartSession: (Int) -> Unit = {},  // Startet Session direkt mit Timer (sessionId)
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val focusMinutes by viewModel.todayFocusMinutes.collectAsState()
    val totalPlannedMinutes by viewModel.totalPlannedMinutes.collectAsState()

    // "Sessions heute" (alle Sessions: geplant, in Bearbeitung, abgeschlossen)
    val todayLearningItems by viewModel.todayLearningItems.collectAsState()

    // Abgeschlossene Themen heute
    val todayCompletedTopics by viewModel.todayCompletedTopics.collectAsState()

    // Ausgewählte Session für Bearbeitung
    val selectedSessionForEdit by viewModel.selectedSessionForEdit.collectAsState()

    // Dialog-State für Reset-Bestätigung
    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadTodayDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // Wichtig: Damit Material nicht automatisch StatusBar-Padding addiert,
                // kannst du hier entweder WindowInsets(0) setzen oder statusBars nur teilweise nutzen.
                // Variante A (kein extra top inset):
                windowInsets = WindowInsets(0, 0, 0, 0),
                // Variante B (wenn du safe-area willst, aber weniger "Luft" als dein Screenshot):
                // windowInsets = WindowInsets.statusBars.only(WindowInsetsSides.Top),

                title = {
                    // Logo links
                    /*Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "FitNFocus Logo",
                        modifier = Modifier.height(70.dp),
                    )*/
                    Row{
                        Text(text = "Hallo Eugenia")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Spacer(modifier = Modifier.height(4.dp))
            // ----- DASHBOARD HEADER -----
            Text(
                text = "Bereit für eine fokussierte Session?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )


            // Focus Card - zeigt gelernte Minuten
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Focus-Zeit", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (totalPlannedMinutes > 0) {
                                "$focusMinutes / $totalPlannedMinutes Minuten"
                            } else {
                                "$focusMinutes Minuten"
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
            }


            // ----- SESSIONS HEUTE -----
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = "Sessions heute",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (todayLearningItems.isEmpty()) {
                Text(
                    text = "Heute wurde noch keine Session erstellt.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                todayLearningItems.forEach { item ->
                    InteractiveTodayLearningCard(
                        item = item,
                        onClick = { viewModel.selectSessionForEdit(item) },
                        onStatusChange = { status -> viewModel.updateSessionStatus(item.sessionId, status) },
                        onNotesChange = { notes -> viewModel.updateSessionNotes(item.sessionId, notes) },
                        onMarkTopicCompleted = { isCompleted ->
                            item.goalId?.let { goalId ->
                                viewModel.markTopicCompleted(goalId, item.topic, isCompleted)
                            }
                        },
                        onStartSession = { onStartSession(item.sessionId) }  // Direkt Timer starten mit sessionId
                    )
                }
            }
        }
    }

    // Reset-Bestätigungs-Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Onboarding zurücksetzen?") },
            text = {
                Text("Alle deine Einstellungen werden gelöscht und das Onboarding wird beim nächsten App-Start erneut angezeigt.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetOnboarding()
                        showResetDialog = false
                    }
                ) {
                    Text("Zurücksetzen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}


/**
 * Kompakte Karte für Sessions im Dashboard.
 * Zeigt: Lern-Icon | Thema | Minuten | Start-Button
 */
@Composable
private fun InteractiveTodayLearningCard(
    item: TodayLearningItem,
    onClick: () -> Unit,
    onStatusChange: (SessionStatus) -> Unit,
    onNotesChange: (String) -> Unit,
    onMarkTopicCompleted: (Boolean) -> Unit,
    onStartSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }
    var showNotesViewDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    // Nur klickbar wenn nicht abgeschlossen
                    if (item.status != SessionStatus.COMPLETED) {
                        Modifier.clickable(onClick = {
                            // Bei Klick: Notizen anzeigen falls vorhanden, sonst normaler Click
                            if (item.notes.isNotBlank()) {
                                showNotesViewDialog = true
                            } else {
                                onClick()
                            }
                        })
                    } else {
                        Modifier
                    }
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lern-Icon (zeigt Bereich an)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Lernen",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Thema und Status
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.topic,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )

                // Status-Badge
                val statusColor = when (item.status) {
                    SessionStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                    SessionStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
                    SessionStatus.PLANNED -> MaterialTheme.colorScheme.onSurfaceVariant
                    SessionStatus.STOPPED -> MaterialTheme.colorScheme.error
                }
                val statusText = when (item.status) {
                    SessionStatus.COMPLETED -> "Abgeschlossen"
                    SessionStatus.IN_PROGRESS -> "In Bearbeitung"
                    SessionStatus.PLANNED -> "Geplant"
                    SessionStatus.STOPPED -> "Gestoppt"
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor
                )
            }

            // Minuten-Anzeige
            Text(
                text = "${item.durationMinutes} min",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Action-Button basierend auf Status
            when (item.status) {
                SessionStatus.PLANNED -> {
                    // Start-Button (Play)
                    FilledIconButton(
                        onClick = onStartSession,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Starten",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                SessionStatus.COMPLETED -> {
                    // Grünes Häkchen für abgeschlossene Sessions (kein Button mehr)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = Color(0xFF4CAF50),  // Grün
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Abgeschlossen",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                SessionStatus.IN_PROGRESS -> {
                    FilledIconButton(
                        onClick = onStartSession,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.inversePrimary
                        )
                    ) {
                        Icon(
                            Icons.Default.Pause,
                            contentDescription = "Pause",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                SessionStatus.STOPPED -> {
                    // Gestoppte Sessions können fortgesetzt werden
                    FilledIconButton(
                        onClick = onStartSession,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Fortsetzen",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
