package com.example.fitnfocus.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Repräsentiert einen Fokus-Bereich in der App.
 * Nur LEARNING ist aktuell implementiert, andere sind "Coming Soon".
 */
enum class FocusArea(
    val displayName: String,
    val icon: ImageVector,
    val isAvailable: Boolean
) {
    LEARNING(
        displayName = "Lernen",
        icon = Icons.Default.School,
        isAvailable = true
    ),
    FITNESS(
        displayName = "Fitness",
        icon = Icons.Default.FitnessCenter,
        isAvailable = false
    ),
    FINANCE(
        displayName = "Finanzen",
        icon = Icons.Default.AccountBalance,
        isAvailable = false
    ),
    NUTRITION(
        displayName = "Ernährung",
        icon = Icons.Default.Restaurant,
        isAvailable = false
    ),
    DAILY(
        displayName = "Alltägliches",
        icon = Icons.Default.Today,
        isAvailable = false
    ),
    WELLNESS(
        displayName = "Wellness",
        icon = Icons.Default.Spa,
        isAvailable = false
    )
}

