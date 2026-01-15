package com.example.fitnfocus.ui.study.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.LearningGoal
import java.time.format.DateTimeFormatter


// Swipe Background (helles Lila, leicht transparent)
private val SwipeBackgroundColor = FitNFocusColors.PurpleContainer.copy(alpha = 0.60f)
private val SwipeIconColor = FitNFocusColors.PurplePrimary

// Timeline Farben (Orange Track + Lila Icon)
private val TimelineIconColor = FitNFocusColors.PurplePrimary
private val TimelineTrackColor = FitNFocusColors.OrangeAccent

private val ItemShape = RoundedCornerShape(14.dp)
private val ItemVerticalPadding = 5.dp
private val ItemMinHeight = 92.dp


/**
 * Übersicht aller Lernziele mit Swipe-to-Delete.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningGoalsOverview(
    learningGoals: List<LearningGoal>,
    topicProgress: Map<String, Boolean>,
    onGoalClick: (LearningGoal) -> Unit,
    onDeleteGoal: (LearningGoal) -> Unit,
    onAddGoalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedGoals = learningGoals.sortedWith(
        compareBy(
            { goal -> goal.topics.isNotEmpty() && goal.topics.all { topicProgress[it] == true } },
            { goal -> goal.examDate == null },
            { goal -> goal.examDate }
        )
    )

    Column(modifier = modifier) {
        // Header
        Spacer(modifier = Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Meine Lernziele",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FitNFocusColors.TextPrimary
            )
            IconButton(onClick = onAddGoalClick) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Lernziel hinzufügen",
                    tint = FitNFocusColors.PurplePrimary
                )
            }
        }

        if (sortedGoals.isEmpty()) {
            EmptyGoalsPlaceholder(onAddClick = onAddGoalClick)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                itemsIndexed(items = sortedGoals, key = { _, goal -> goal.id }) { index, goal ->
                    val completedTopics = goal.topics.count { topicProgress[it] == true }
                    val progress =
                        if (goal.topics.isNotEmpty()) completedTopics.toFloat() / goal.topics.size else 0f

                    SwipeableGoalItem(
                        goal = goal,
                        progress = progress,
                        completedCount = completedTopics,
                        isFirst = index == 0,
                        isLast = index == sortedGoals.lastIndex,
                        onClick = { onGoalClick(goal) },
                        onDelete = { onDeleteGoal(goal) }
                    )
                }
            }
        }
    }
}

/**
 * Lernziel-Item mit Swipe-to-Delete.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableGoalItem(
    goal: LearningGoal,
    progress: Float,
    completedCount: Int,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var shouldReset by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                shouldReset = true
            }
            false
        },
        positionalThreshold = { it * 0.4f }
    )

    LaunchedEffect(shouldReset) {
        if (shouldReset) {
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            shouldReset = false
        }
    }

    val swipeProgress = dismissState.progress.coerceIn(0f, 1f)

    val bgColor by animateColorAsState(
        targetValue = if (dismissState.progress > 0f) SwipeBackgroundColor else Color.Transparent,
        animationSpec = tween(150),
        label = "bg"
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = ItemVerticalPadding)
                    .clip(ItemShape)
                    .background(bgColor),
                contentAlignment = Alignment.CenterEnd
            ) {
                val iconAlpha = (swipeProgress / 0.25f).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .graphicsLayer(alpha = iconAlpha),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Kleiner Pfeil zeigt Swipe-Richtung
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SwipeIconColor.copy(alpha = 0.8f)
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Löschen",
                        tint = SwipeIconColor
                    )
                }
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = ItemVerticalPadding)
                .clip(ItemShape)
                .heightIn(min = ItemMinHeight)
        ) {
            GoalTimelineItem(
                goal = goal,
                progress = progress,
                completedCount = completedCount,
                isFirst = isFirst,
                isLast = isLast,
                onClick = onClick
            )
        }
    }
}

/**
 * Timeline-Darstellung eines Lernziels.
 */
@SuppressLint("NewApi")
@Composable
private fun GoalTimelineItem(
    goal: LearningGoal,
    progress: Float,
    completedCount: Int,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val progressColor = when {
        progress >= 1f -> FitNFocusColors.PurplePrimary
        progress > 0f -> FitNFocusColors.OrangeAccent
        else -> FitNFocusColors.TextSecondary
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = ItemMinHeight)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timeline
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(52.dp)
                .fillMaxHeight()
                .padding(start = 4.dp)
            //.background(FitNFocusColors.PurpleContainer.copy(alpha = 0.25f), shape = RoundedCornerShape(12.dp))

        ) {
            // Top connector - Obere Linie
            if (!isFirst) {
                Surface(
                    color = TimelineTrackColor.copy(alpha = 0.75f),
                    modifier = Modifier
                        .width(3.dp)
                        .height(25.dp)
                ) {}
            } else {
                // keine Linie
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Node
            Box(modifier = Modifier.size(34.dp), contentAlignment = Alignment.Center) {
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

            // Bottom connector - untere Linie
            if (!isLast) {
                Surface(
                    color = TimelineTrackColor.copy(alpha = 0.75f),
                    modifier = Modifier
                        .width(3.dp)
                        .height(52.dp)
                ) {}
            } else {
                Spacer(modifier = Modifier.height(52.dp))
            }
        }

        // Content Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 6.dp)
                .fillMaxHeight(),
            colors = CardDefaults.cardColors(

                containerColor = FitNFocusColors.Surface.copy(alpha = 0.70f)
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
                        color = FitNFocusColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$completedCount / ${goal.topics.size} Themen",
                            style = MaterialTheme.typography.bodySmall,
                            color = FitNFocusColors.TextSecondary
                        )
                        Text("•", color = FitNFocusColors.TextSecondary)

                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = progressColor
                        )
                    }
                }

                // Datum-Zeile: Platz IMMER reservieren, damit Höhe identisch bleibt
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val hasDate = goal.examDate != null
                    Icon(
                        Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = FitNFocusColors.TextSecondary.copy(alpha = if (hasDate) 1f else 0f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    val dateText = if (goal.examDate != null) {
                        val formatted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            goal.examDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        } else goal.examDate.toString()
                        "Prüfung: $formatted"
                    } else {
                        " " // Platzhalter
                    }

                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = FitNFocusColors.TextSecondary.copy(alpha = if (hasDate) 1f else 0f)
                    )
                }
            }
        }
    }
}


/**
 * Platzhalter für leere Liste.
 */
@Composable
private fun EmptyGoalsPlaceholder(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = FitNFocusColors.TextSecondary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Noch keine Lernziele",
            style = MaterialTheme.typography.titleMedium,
            color = FitNFocusColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Erstelle dein erstes Lernziel im Onboarding oder füge eines hinzu.",
            style = MaterialTheme.typography.bodySmall,
            color = FitNFocusColors.TextSecondary.copy(alpha = 0.85f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onAddClick
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = FitNFocusColors.PurplePrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Lernziel hinzufügen", color = FitNFocusColors.PurplePrimary)
        }
    }
}
