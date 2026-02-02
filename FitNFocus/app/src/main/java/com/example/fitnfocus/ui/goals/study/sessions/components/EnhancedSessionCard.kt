package com.example.fitnfocus.ui.goals.study.sessions.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import com.example.fitnfocus.ui.common.toPresentation
import com.example.fitnfocus.ui.theme.OrangeAccent
import com.example.fitnfocus.ui.theme.PurpleContainer

/**
 * Erweiterte Session-Card mit Status, Notes und Topic-Completion.
 */
@Composable
fun EnhancedSessionCard(
    session: StudySession,
    onClick: () -> Unit,
    onStatusChange: (SessionStatus) -> Unit,
    onNotesChange: (String) -> Unit,
    onMarkTopicCompleted: (Boolean) -> Unit,
    showTopicCompletionButton: Boolean,
    modifier: Modifier = Modifier
) {
    var showNotesDialog by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PurpleContainer.copy(alpha = 0.70f)
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${session.durationMinutes} min",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    ) {
                        // Status-Badge (zentralisiert)
                        val statusPresentation = session.status.toPresentation()

                        Surface(
                            color = statusPresentation.color.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = statusPresentation.text,
                                style = MaterialTheme.typography.labelSmall,
                                color = statusPresentation.color,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp,
                                        vertical = 4.dp)
                            )
                        }
                    }
                }

                Box {
                    val isCompleted = session.status == SessionStatus.COMPLETED
                    IconButton(
                        onClick = { showStatusMenu = true },
                        enabled = !isCompleted
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Status ändern",
                            tint = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Geplant") },
                            onClick = {
                                onStatusChange(SessionStatus.PLANNED); showStatusMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Schedule, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Abgeschlossen") },
                            onClick = {
                                onStatusChange(SessionStatus.COMPLETED); showStatusMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Check, null) }
                        )
                    }
                }
            }

            if (session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = session.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showNotesDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, OrangeAccent)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Notes,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (session.notes.isBlank()) "Notiz" else "Bearbeiten",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (showTopicCompletionButton && session.status == SessionStatus.COMPLETED) {
                    Button(
                        onClick = { onMarkTopicCompleted(true) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thema fertig", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }

    if (showNotesDialog) {
        var notesText by remember { mutableStateOf(session.notes) }

        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text("Notizen / Todos") },
            text = {
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text( text = "Was wurde erledigt? Todos...") },
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onNotesChange(notesText)
                    showNotesDialog = false
                }) { Text("Speichern") }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) { Text("Abbrechen") }
            }
        )
    }
}