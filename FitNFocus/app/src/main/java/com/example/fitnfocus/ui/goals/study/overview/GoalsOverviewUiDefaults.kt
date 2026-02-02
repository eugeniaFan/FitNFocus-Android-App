package com.example.fitnfocus.ui.goals.study.overview

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.ui.theme.OrangeAccent
import com.example.fitnfocus.ui.theme.PurpleContainer
import com.example.fitnfocus.ui.theme.PurplePrimary

/**
 * UI constants for the goals overview feature.
 */
internal object GoalsOverviewUiDefaults {
    val SwipeBackgroundColor = PurpleContainer.copy(alpha = 0.60f)
    val SwipeIconColor = PurplePrimary

    val TimelineIconColor = PurplePrimary
    val TimelineTrackColor = OrangeAccent

    val ItemShape = RoundedCornerShape(14.dp)
    val ItemVerticalPadding = 5.dp
    val ItemMinHeight = 92.dp
}
