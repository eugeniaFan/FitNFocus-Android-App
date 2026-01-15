package com.example.fitnfocus.ui.onboarding.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Step 4: Persönlichkeits-Slider
 */
@Composable
fun PersonalityStep(
    planningPreference: Int,
    socialPreference: Int,
    structurePreference: Int,
    onPlanningChanged: (Int) -> Unit,
    onSocialChanged: (Int) -> Unit,
    onStructureChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Wie würdest du dich beschreiben?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bewege die Regler zu der Seite, die dich besser beschreibt.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        PersonalitySlider(
            title = "Arbeitsweise",
            leftLabel = "Spontan",
            rightLabel = "Geplant",
            value = planningPreference,
            onValueChange = onPlanningChanged
        )

        Spacer(modifier = Modifier.height(24.dp))

        PersonalitySlider(
            title = "Zusammenarbeit",
            leftLabel = "Alleine",
            rightLabel = "Im Team",
            value = socialPreference,
            onValueChange = onSocialChanged
        )

        Spacer(modifier = Modifier.height(24.dp))

        PersonalitySlider(
            title = "Tagesablauf",
            leftLabel = "Flexibel",
            rightLabel = "Feste Routinen",
            value = structurePreference,
            onValueChange = onStructureChanged
        )
    }
}

@Composable
private fun PersonalitySlider(
    title: String,
    leftLabel: String,
    rightLabel: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = leftLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = rightLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
