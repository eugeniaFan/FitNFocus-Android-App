package com.example.fitnfocus.ui.goals.study.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Dialog for "Partial Completion" (early stop).
 */
@Composable
internal fun PartialSaveDialog(
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
                    text = "Du hast $elapsedMinutes Min. $elapsedSecs Sek. fokussiert.",
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
