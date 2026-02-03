package com.example.fitnfocus.ui.goals.study.overview.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.ui.goals.study.overview.GoalsOverviewUiDefaults.ItemMinHeight
import com.example.fitnfocus.ui.goals.study.overview.GoalsOverviewUiDefaults.TimelineIconColor
import com.example.fitnfocus.ui.goals.study.overview.GoalsOverviewUiDefaults.TimelineTrackColor
import com.example.fitnfocus.ui.theme.OrangeAccent
import com.example.fitnfocus.ui.theme.PurplePrimary
import com.example.fitnfocus.ui.theme.SurfaceWhite
import com.example.fitnfocus.ui.theme.TextPrimary
import com.example.fitnfocus.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter

@Composable
internal fun GoalTimelineItem(
    goal: LearningGoal,
    progress: Float,
    completedCount: Int,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val progressColor = when {
        progress >= 1f -> PurplePrimary
        progress > 0f -> OrangeAccent
        else -> TextSecondary
    }
    Row(
        modifier = Modifier
            .testTag("goal_item_${goal.moduleName}")
            .fillMaxWidth()
            .heightIn(min = ItemMinHeight)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(52.dp)
                .fillMaxHeight()
                .padding(start = 4.dp)
        ) {
            if (!isFirst) {
                Surface(
                    color = TimelineTrackColor.copy(alpha = 0.75f),
                    modifier = Modifier
                        .width(3.dp)
                        .height(25.dp)
                ) {}
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 3.dp,
                    color = TimelineIconColor,
                    trackColor = TimelineTrackColor.copy(alpha = 0.70f)
                )
                Icon(
                    imageVector = if (progress >= 1f) Icons.Default.Check else Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TimelineIconColor
                )
            }

            if (!isLast) {
                Surface(
                    color = TimelineTrackColor.copy(alpha = 0.75f),
                    modifier = Modifier
                        .width(3.dp)
                        .height(52.dp)
                ) {}
            } else {
                Spacer(
                    modifier = Modifier.height(52.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 6.dp)
                .fillMaxHeight(),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite.copy(alpha = 0.70f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = goal.moduleName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = TextPrimary
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$completedCount / ${goal.topics.size} Themen",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text("•", color = TextSecondary)
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = progressColor
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val hasDate = goal.examDate != null
                    Icon(
                        Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary.copy(alpha = if (hasDate) 1f else 0f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    val dateText = if (goal.examDate != null) {
                        val formatted =
                            goal.examDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        "Prüfung: $formatted"
                    } else {
                        " "
                    }

                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary.copy(alpha = if (hasDate) 1f else 0f)
                    )
                }
            }
        }
    }
}
