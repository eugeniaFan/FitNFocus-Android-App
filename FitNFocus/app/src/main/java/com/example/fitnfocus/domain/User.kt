package com.example.fitnfocus.domain

/**
 * Benutzer-Datenmodell.
 * Enthält alle Informationen, die im Onboarding erfasst werden.
 */
data class User(
    val role: UserRole = UserRole.STUDENT,
    val isOnboarded: Boolean = false,
    val personalityProfile: PersonalityProfile = PersonalityProfile()
)

