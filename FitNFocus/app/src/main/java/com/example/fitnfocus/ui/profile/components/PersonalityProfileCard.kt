package com.example.fitnfocus.ui.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Karte für Persönlichkeitsprofil mit Slidern.
 */
@Composable
internal fun PersonalityProfileCard(
    planningPreference: Int,
    socialPreference: Int,
    structurePreference: Int
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PreferenceBar(
                label = "Arbeitsweise",
                leftLabel = "Spontan",
                rightLabel = "Geplant",
                value = planningPreference
            )

            PreferenceBar(
                label = "Zusammenarbeit",
                leftLabel = "Alleine",
                rightLabel = "Im Team",
                value = socialPreference
            )

            PreferenceBar(
                label = "Tagesablauf",
                leftLabel = "Flexibel",
                rightLabel = "Strukturiert",
                value = structurePreference
            )
        }
    }
}

/**
 * Einzelne Präferenz-Anzeige mit Balken.
 */
@Composable
private fun PreferenceBar(
    label: String,
    leftLabel: String,
    rightLabel: String,
    value: Int
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = leftLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = rightLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

