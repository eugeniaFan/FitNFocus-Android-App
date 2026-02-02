package com.example.fitnfocus.ui.goals.study.overview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.ui.theme.PurplePrimary
import com.example.fitnfocus.ui.theme.TextSecondary

@Composable
internal fun EmptyGoalsPlaceholder(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = TextSecondary.copy(alpha = 0.6f)
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = "Noch keine Lernziele",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        Text(
            text = "Erstelle dein erstes Lernziel im Onboarding oder füge eines hinzu.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary.copy(alpha = 0.85f)
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )
        OutlinedButton(
            onClick = onAddClick)
        {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = PurplePrimary
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Text(
                text = "Lernziel hinzufügen",
                color = PurplePrimary
            )
        }
    }
}
