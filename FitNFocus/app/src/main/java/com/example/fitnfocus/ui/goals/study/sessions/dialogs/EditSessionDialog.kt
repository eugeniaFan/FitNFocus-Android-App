package com.example.fitnfocus.ui.goals.study.sessions.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.StudySession

/**
 * Dialog for editing a new Focus Session.
 * Topic is displayed but not editable.
 * Only the duration can be entered.
 */
@Composable
fun EditSessionDialog(
    session: StudySession,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (StudySession) -> Unit,
    onDelete: (StudySession) -> Unit
) {
    var editedDuration by remember(session.id) { mutableStateOf(session.durationMinutes.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Session bearbeiten") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Topic display (not editable)
                OutlinedTextField(
                    value = session.topic,
                    onValueChange = { },
                    label = { Text("Thema") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    readOnly = true
                )
                // Duration input
                OutlinedTextField(
                    value = editedDuration,
                    onValueChange = { editedDuration = it },
                    label = { Text("Dauer (Minuten)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading,
                onClick = {
                    val minutes = editedDuration.toIntOrNull()
                    if (minutes != null && minutes > 0) {
                        onUpdate(
                            session.copy(
                                durationMinutes = minutes
                            )
                        )
                    }
                }
            ) { Text("Speichern") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) { Text("Abbrechen") }
                Spacer(Modifier.width(8.dp))
                TextButton(
                    enabled = !isLoading,
                    onClick = { onDelete(session) }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Löschen")
                }
            }
        }
    )
}

