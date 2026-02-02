package com.example.fitnfocus.ui.profile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Bestätigungs-Dialog zum Zurücksetzen des Profils.
 */
@Composable
internal fun ResetProfileDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Profil zurücksetzen?") },
        text = {
            Text(
                "Folgende Daten werden unwiderruflich gelöscht:\n\n" +
                        "• Alle Lernziele und Themen\n" +
                        "• Alle Sessions und Fortschritte\n" +
                        "• Dein Persönlichkeitsprofil\n\n" +
                        "Das Onboarding wird beim nächsten App-Start erneut angezeigt."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Alles löschen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

