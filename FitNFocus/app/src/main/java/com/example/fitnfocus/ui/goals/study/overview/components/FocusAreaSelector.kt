package com.example.fitnfocus.ui.goals.study.overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.FocusArea
import com.example.fitnfocus.ui.theme.OutlineVariantSoft
import com.example.fitnfocus.ui.theme.PurpleContainer
import com.example.fitnfocus.ui.theme.PurplePrimary
import com.example.fitnfocus.ui.theme.PurpleTintBg
import com.example.fitnfocus.ui.theme.SurfaceWhite
import com.example.fitnfocus.ui.theme.TextSecondary

/**
 * Horizontale scrollbare Liste der Fokus-Bereiche.
 */
@Composable
fun FocusAreaSelector(
    selectedArea: FocusArea,
    onAreaSelected: (FocusArea) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val selectorHeight = 84.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(selectorHeight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(end = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FocusArea.entries.forEach { area ->
                FocusAreaItem(
                    area = area,
                    isSelected = area == selectedArea,
                    onClick = { onAreaSelected(area) }
                )
            }
        }

        // Rechts-Fade
        if (scrollState.canScrollForward) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(28.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                PurpleTintBg
                            )
                        )
                    )
            )
        }

        // Links-Fade
        if (scrollState.canScrollBackward) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(28.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                PurpleTintBg,
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Einzelner Fokus-Bereich als kreisförmige Card.
 */
@Composable
private fun FocusAreaItem(
    area: FocusArea,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isSelected) PurpleContainer else SurfaceWhite
    val borderColor =
        if (isSelected) PurplePrimary else OutlineVariantSoft
    val iconTint = if (isSelected) PurplePrimary else TextSecondary
    val labelColor =
        if (isSelected) PurplePrimary else TextSecondary

    Column(
        modifier = modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = borderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = area.icon,
                contentDescription = area.displayName,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = area.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

