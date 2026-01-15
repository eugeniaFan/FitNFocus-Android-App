package com.example.fitnfocus.ui.study.components

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

object FitNFocusColors {
    val PurplePrimary = Color(0xFF6750A4)      // RGB(103,80,164)
    val PurpleDeep = Color(0xFF4F378B)         // RGB(79,55,139)
    val PurpleTintBg = Color(0xFFF5F3FA)       // RGB(245,243,250)
    val PurpleContainer = Color(0xFFE9E0FC)    // RGB(233,224,252)
    val BorderSoft = Color(0xFFE6E1F0)        // weicher lila-grauer Border


    val OrangeDeep = Color(0xFF9C4800)        // dunkleres Orange für Text/Icon auf OrangeSoft
    val OrangeAccent = Color(0xFFFF9933)       // RGB(255,153,51)
    val OrangeSoft = Color(0xFFFFE8D0)         // RGB(255,232,208)

    val Surface = Color(0xFFFFFFFF)           // neutrales Weiß
    val SurfaceVariant = Color(0xFFF2F0F7)     // RGB(242,240,247)
    val OutlineVariant = Color(0xFFDAD6E6)     // RGB(218,214,230)
    val TextPrimary = Color(0xFF18181C)        // RGB(24,24,28)
    val TextSecondary = Color(0xFF62606E)      // RGB(98,96,110)
}

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
        // Inhalt: horizontale Liste
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                // kleiner Trick: extra End-Padding, damit der Fade rechts nicht das letzte Item überdeckt
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

        // Rechts-Fade (Scroll-Hint)
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
                                FitNFocusColors.PurpleTintBg
                            )
                        )
                    )
            )
        }

        // Links-Fade (optional, zeigt: du kannst auch zurückscrollen)
        if (scrollState.canScrollBackward) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(28.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                FitNFocusColors.PurpleTintBg,
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
    // Unselected: clean/neutral
    // Selected: lila Container + lila Border
    val backgroundColor = if (isSelected) FitNFocusColors.PurpleContainer else FitNFocusColors.Surface

    val borderColor = if (isSelected) FitNFocusColors.PurplePrimary else FitNFocusColors.OutlineVariant

    val iconTint = if (isSelected) FitNFocusColors.PurplePrimary else FitNFocusColors.TextSecondary

    val labelColor = if (isSelected) FitNFocusColors.PurplePrimary else FitNFocusColors.TextSecondary

    Column(
        modifier = modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Kreisförmiger Icon-Container
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

        // Label
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


