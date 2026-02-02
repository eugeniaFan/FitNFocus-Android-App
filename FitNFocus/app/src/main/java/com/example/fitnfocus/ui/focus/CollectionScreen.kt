package com.example.fitnfocus.ui.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CollectionScreen(
    onBack: () -> Unit
) {
    val bg = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F1E8), Color(0xFFE6DAC8))
    )
    val types = FocusTypes.staticTypes()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Navigate Back to Focus-Screen",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }

            Text(
                text = "Deine Sammlung",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Normal,
            )
        }

        Text(
            text = "Diese Fokus-Typen gibt es:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            types.forEach { type ->
                CollectionTypeCard(type)
            }
        }
    }
}

@Composable
private fun CollectionTypeCard(type: FocusTypeUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                type.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                type.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(type.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}