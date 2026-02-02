package com.example.fitnfocus.ui.goals.study.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.ui.goals.study.TopicStatus
import com.example.fitnfocus.ui.theme.OrangeAccent
import com.example.fitnfocus.ui.theme.OrangeSoft
import com.example.fitnfocus.ui.theme.OutlineVariantSoft
import com.example.fitnfocus.ui.theme.PurplePrimary
import com.example.fitnfocus.ui.theme.SurfaceVariantSoft
import com.example.fitnfocus.ui.theme.TextPrimary
import com.example.fitnfocus.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter


/**
 * Detailansicht eines einzelnen Lernziels mit allen Themen.
 */

@Composable
fun GoalDetailScreen(
    modifier: Modifier = Modifier,
    goal: LearningGoal,
    topicProgress: Map<String, Boolean>,
    topicStatusMap: Map<String, TopicStatus> = emptyMap(),
    onBackClick: () -> Unit,
    onEditClick: (LearningGoal) -> Unit,
    onTopicClick: (String) -> Unit,
    onTopicToggle: (Int, String, Boolean) -> Unit
) {
    val completedCount = goal.topics.count { topicProgress[it] == true }
    val progress = if (goal.topics.isNotEmpty()) {
        completedCount.toFloat() / goal.topics.size
    } else 0f

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
            }

            Text(
                text = goal.moduleName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            IconButton(
                modifier = Modifier.testTag("goal_edit_button"),
                onClick = { onEditClick(goal) }
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Lernziel bearbeiten")
            }
        }

        goal.examDate?.let { date ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))

                val formattedDate = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                Text(
                    text = "Prüfung am $formattedDate",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Fortschritts-Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PurplePrimary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Fortschritt",
                            style = MaterialTheme.typography.labelMedium,
                            color = SurfaceVariantSoft.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "$completedCount / ${goal.topics.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Themen erledigt",
                            style = MaterialTheme.typography.bodySmall,
                            color = SurfaceVariantSoft.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(99.dp)),
                    trackColor = Color.White.copy(alpha = 0.35f),
                    strokeCap = StrokeCap.Round,
                    color = OrangeAccent
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Themen",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (goal.topics.isEmpty()) {
            Text(
                text = "Noch keine Themen hinzugefügt.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                itemsIndexed(
                     goal.topics
                ) { index, topic ->
                    val isCompleted = topicProgress[topic] == true
                    val topicStatus = topicStatusMap[topic] ?: TopicStatus.NOT_STARTED

                    TopicTimelineItem(
                        topic = topic,
                        isCompleted = isCompleted,
                        topicStatus = topicStatus,
                        isFirst = index == 0,
                        isLast = index == goal.topics.lastIndex,
                        onToggle = { onTopicToggle(goal.id, topic, !isCompleted) },
                        onClick = { onTopicClick(topic) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicTimelineItem(
    topic: String,
    isCompleted: Boolean,
    topicStatus: TopicStatus,
    isFirst: Boolean,
    isLast: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
) {
    val statusText = when (topicStatus) {
        TopicStatus.COMPLETED -> "Abgeschlossen"
        TopicStatus.IN_PROGRESS -> "In Bearbeitung"
        TopicStatus.NOT_STARTED -> "Nicht gestartet"
    }

    val statusColor = when (topicStatus) {
        TopicStatus.COMPLETED -> PurplePrimary
        TopicStatus.IN_PROGRESS -> OrangeAccent
        TopicStatus.NOT_STARTED -> TextSecondary
    }

    val connectorColor = when (topicStatus) {
        TopicStatus.COMPLETED -> PurplePrimary
        TopicStatus.IN_PROGRESS -> OrangeAccent
        TopicStatus.NOT_STARTED -> OutlineVariantSoft
    }

    val cardColor = when (topicStatus) {
        TopicStatus.COMPLETED -> SurfaceVariantSoft
        TopicStatus.IN_PROGRESS -> OrangeSoft
        TopicStatus.NOT_STARTED -> Color.White
    }

    val circleIconTint = when (topicStatus) {
        TopicStatus.COMPLETED, TopicStatus.IN_PROGRESS -> Color.White
        TopicStatus.NOT_STARTED -> TextSecondary
    }

    val topicTextColor =
        if (isCompleted) TextSecondary else TextPrimary

    val topicTag = "topic_item_" + topic.replace(" ", "_")
    Row(
        modifier = Modifier
            .testTag(topicTag)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            if (!isFirst) {
                Surface(
                    color = connectorColor,
                    modifier = Modifier
                        .width(2.dp)
                        .height(20.dp)
                ) {}
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when (topicStatus) {
                            TopicStatus.COMPLETED -> PurplePrimary
                            TopicStatus.IN_PROGRESS -> OrangeAccent
                            TopicStatus.NOT_STARTED -> SurfaceVariantSoft
                        }
                    )
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                when (topicStatus) {
                    TopicStatus.COMPLETED -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Erledigt",
                        modifier = Modifier.size(16.dp),
                        tint = circleIconTint
                    )

                    TopicStatus.IN_PROGRESS -> Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "In Bearbeitung",
                        modifier = Modifier.size(14.dp),
                        tint = circleIconTint
                    )

                    TopicStatus.NOT_STARTED -> { /* Leerer Kreis */
                    }
                }
            }

            if (!isLast) {
                Surface(
                    color = connectorColor,
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                ) {}
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                        color = topicTextColor
                    )

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }
        }
    }
}

