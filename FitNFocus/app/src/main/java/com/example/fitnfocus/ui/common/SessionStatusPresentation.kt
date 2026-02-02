package com.example.fitnfocus.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.fitnfocus.domain.SessionStatus

/**
 * UI presentation of a session status.
 * Encapsulates text and color for UI use.
 */
data class SessionStatusPresentation(
    val text: String,
    val color: Color
)

/**
 * Maps a domain status to its UI presentation.
 */
@Composable
fun SessionStatus.toPresentation(): SessionStatusPresentation {
    return when (this) {
        SessionStatus.COMPLETED -> SessionStatusPresentation(
            text = "Abgeschlossen",
            color = MaterialTheme.colorScheme.primary
        )

        SessionStatus.PLANNED -> SessionStatusPresentation(
            text = "Geplant",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SessionStatus.STOPPED -> SessionStatusPresentation(
            text = "Gestoppt",
            color = MaterialTheme.colorScheme.error
        )
    }
}
