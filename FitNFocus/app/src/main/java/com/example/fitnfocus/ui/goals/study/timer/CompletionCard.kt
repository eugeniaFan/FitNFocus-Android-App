package com.example.fitnfocus.ui.goals.study.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Card for session completion (after FINISHED).
 */
@Composable
internal fun CompletionCard(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timer_complete_session_button")
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
