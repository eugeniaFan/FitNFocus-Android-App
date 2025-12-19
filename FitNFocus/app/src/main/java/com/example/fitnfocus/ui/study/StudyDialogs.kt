package com.example.fitnfocus.ui.study

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.StudySession


@Composable
fun AddSessionDialog(
    subject: String,
    duration: String,
    isLoading: Boolean,
    onSubjectChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (Boolean) -> Unit
) {
    var addToCalendar by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Focus Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = onSubjectChange,
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = onDurationChange,
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { addToCalendar = !addToCalendar },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = addToCalendar,
                        onCheckedChange = { addToCalendar = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("In Kalender eintragen")
                }
            }
        },
        confirmButton = {
            Button(onClick = {onSave(addToCalendar)}, enabled = !isLoading) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditSessionDialog(
    session: StudySession,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (StudySession) -> Unit,
    onDelete: (StudySession) -> Unit
) {
    var editedSubject by remember(session.id) { mutableStateOf(session.subject) }
    var editedDuration by remember(session.id) { mutableStateOf(session.durationMinutes.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = editedSubject,
                    onValueChange = { editedSubject = it },
                    label = { Text("Subject") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = editedDuration,
                    onValueChange = { editedDuration = it },
                    label = { Text("Duration (minutes)") },
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
                                subject = editedSubject.trim(),
                                durationMinutes = minutes
                            )
                        )
                    }
                }
            ) { Text("Update") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                TextButton(
                    enabled = !isLoading,
                    onClick = { onDelete(session) }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Delete")
                }
            }
        }
    )
}