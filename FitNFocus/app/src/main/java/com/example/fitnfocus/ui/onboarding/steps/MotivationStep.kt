package com.example.fitnfocus.ui.onboarding.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.domain.MotivationType

/**
 * Step 3: choose a motivation archetype.
 */
@Composable
fun MotivationStep(
    selectedMotivation: MotivationType,
    onMotivationSelected: (MotivationType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Was motiviert dich am meisten?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Wähle, was dir am meisten gegen Prokrastination hilft.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        MotivationType.entries.forEach { motivation ->
            MotivationCard(
                motivation = motivation,
                isSelected = motivation == selectedMotivation,
                onClick = { onMotivationSelected(motivation) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MotivationCard(
    motivation: MotivationType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (motivation) {
        MotivationType.STRUCTURED_PROGRESS -> Icons.Default.BarChart
        MotivationType.SELF_IMPROVEMENT -> Icons.Default.Lightbulb
        MotivationType.ADVENTURE -> Icons.Default.SportsEsports
        MotivationType.EMOTIONAL_WELLNESS -> Icons.Default.Favorite
        MotivationType.SOCIAL -> Icons.Default.Groups
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = motivation.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = motivation.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
