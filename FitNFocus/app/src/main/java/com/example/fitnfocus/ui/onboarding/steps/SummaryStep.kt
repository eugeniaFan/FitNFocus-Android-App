package com.example.fitnfocus.ui.onboarding.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.ui.onboarding.OnboardingUiState
import java.time.format.DateTimeFormatter

/**
 * Step 5: review the collected onboarding input before completion.
 */
@Composable
fun SummaryStep(
    uiState: OnboardingUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Alles bereit!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hier ist eine Zusammenfassung deiner Eingaben:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRow("Rolle", uiState.selectedRole.displayName)

                if (uiState.moduleName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryRow("Erstes Modul", uiState.moduleName)
                    if (uiState.topics.isNotEmpty()) {
                        SummaryRow("Themen", uiState.topics.joinToString(", "))
                    }
                    uiState.examDate?.let { date ->
                        val formattedDate = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        SummaryRow("Prüfung", formattedDate)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                SummaryRow("Motivation", uiState.selectedMotivation.displayName)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy policy
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Deine Daten sind sicher",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Alle Daten werden nur lokal auf deinem Gerät gespeichert " +
                                "und nicht an Dritte weitergegeben.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
