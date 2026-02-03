package com.example.fitnfocus.ui.goals.study.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import com.example.fitnfocus.ui.goals.study.sessions.util.formatDateHeader
import com.example.fitnfocus.ui.theme.PurplePrimary
import com.example.fitnfocus.ui.goals.study.sessions.components.EnhancedSessionCard
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Sessions for a specific topic with date grouping.
 */
@Composable
fun TopicSessionsScreen(
    goal: LearningGoal,
    topic: String,
    sessions: List<StudySession>,
    isLoading: Boolean,
    isTopicCompleted: Boolean,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onSessionClick: (StudySession) -> Unit,
    onUpdateSessionStatus: (Int, SessionStatus) -> Unit,
    onUpdateSessionNotes: (Int, String) -> Unit,
    onMarkTopicCompleted: (Int, String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    // Group sessions by date
    val sessionsByDate = sessions.groupBy { it.date }

    Spacer(modifier = Modifier.height(28.dp))
    Column(
        modifier = modifier.testTag("screen_topic_sessions")
    ) {
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück"
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = topic,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = goal.moduleName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                modifier = Modifier.testTag("add_session_button"),
                onClick = onAddClick,
                enabled = !isTopicCompleted
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Session hinzufügen",
                    tint = if (isTopicCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    else
                        PurplePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Topic-Status Card
        if (isTopicCompleted) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Thema abgeschlossen!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "Übersicht aller Sessions",
            style = MaterialTheme.typography.titleMedium
        )

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (sessions.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Noch keine Sessions erstellt. Probier es dochh mal aus. Klicke dazu einfach auf das Plus-Symbol.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Display sessions grouped by date
                sessionsByDate.forEach { (date, dateSessions) ->
                    item {
                        Text(
                            text = formatDateHeader(date),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(dateSessions.size) { index ->
                        val session = dateSessions[index]
                        EnhancedSessionCard(
                            session = session,
                            onClick = { onSessionClick(session) },
                            onStatusChange = { newStatus ->
                                onUpdateSessionStatus(
                                    session.id,
                                    newStatus
                                )
                            },
                            onNotesChange = { notes -> onUpdateSessionNotes(session.id, notes) },
                            onMarkTopicCompleted = { isCompleted ->
                                onMarkTopicCompleted(
                                    goal.id,
                                    topic,
                                    isCompleted
                                )
                            },
                            showTopicCompletionButton = !isTopicCompleted
                        )
                    }
                }
            }
        }
    }
}