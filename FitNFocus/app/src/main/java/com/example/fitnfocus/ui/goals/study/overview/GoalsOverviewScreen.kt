package com.example.fitnfocus.ui.goals.study.overview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.ui.goals.study.overview.components.EmptyGoalsPlaceholder
import com.example.fitnfocus.ui.theme.PurplePrimary
import com.example.fitnfocus.ui.theme.TextPrimary
import com.example.fitnfocus.ui.goals.study.overview.components.SwipeableGoalItem

/**
 * Overview of learning goals with swipe-to-delete support.
 */
@Composable
fun GoalsOverviewScreen(
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

    Column(
        modifier = modifier
            .testTag("screen_goals_overview")
    ) {
        Spacer(
            modifier = Modifier.height(22.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Meine Lernziele",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            IconButton(
                modifier = Modifier
                    .testTag("goals_add_goal_button"),
                onClick = onAddGoalClick
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Lernziel hinzufügen",
                    tint = PurplePrimary
                )
            }
        }

        if (sortedGoals.isEmpty()) {
            EmptyGoalsPlaceholder(
                onAddClick = onAddGoalClick
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                itemsIndexed(
                    items = sortedGoals,
                    key = { _, goal -> goal.id }
                ) { index, goal ->
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
