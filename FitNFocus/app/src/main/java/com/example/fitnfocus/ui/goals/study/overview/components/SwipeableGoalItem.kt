package com.example.fitnfocus.ui.goals.study.overview.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.ui.goals.study.overview.GoalsOverviewUiDefaults.ItemMinHeight
import com.example.fitnfocus.ui.goals.study.overview.GoalsOverviewUiDefaults.ItemShape
import com.example.fitnfocus.ui.goals.study.overview.GoalsOverviewUiDefaults.ItemVerticalPadding
import com.example.fitnfocus.ui.goals.study.overview.GoalsOverviewUiDefaults.SwipeBackgroundColor
import com.example.fitnfocus.ui.goals.study.overview.GoalsOverviewUiDefaults.SwipeIconColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SwipeableGoalItem(
    goal: LearningGoal,
    progress: Float,
    completedCount: Int,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.4f },
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                // Allow the swipe to complete. It resets in the effect below.
                false
            } else {
                true
            }
        }
    )
    // Reset the swipe state after dismissal to keep the item stable.
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            dismissState.reset()
        }
    }

    val swipeProgress = dismissState.progress.coerceIn(0f, 1f)

    val bgColor by animateColorAsState(
        targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
            SwipeBackgroundColor
        else
            Color.Transparent,
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SwipeIconColor.copy(alpha = 0.8f)
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
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
