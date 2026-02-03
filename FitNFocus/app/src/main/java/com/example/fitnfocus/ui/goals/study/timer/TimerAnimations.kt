package com.example.fitnfocus.ui.goals.study.timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Water droplet animation during melting.
 */
@Composable
internal fun WaterDroplets(progress: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "droplets")

    val dropletOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dropletOffset"
    )

    val dropletAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dropletAlpha"
    )

    Box(
        modifier = Modifier
            .offset(y = (80 + dropletOffset).dp)
            .size((8 * progress).dp.coerceAtLeast(4.dp))
            .alpha(dropletAlpha * progress)
            .clip(CircleShape)
            .background(Color(0xFF64B5F6))
    )
}

/**
 * Formats seconds to MM:SS.
 */
internal fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}

