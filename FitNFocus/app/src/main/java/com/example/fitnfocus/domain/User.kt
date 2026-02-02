package com.example.fitnfocus.domain

/**
 * Domain model for user profile.
 * Contains all data collected during onboarding.
 */
data class User(
    val role: UserRole = UserRole.STUDENT,
    val isOnboarded: Boolean = false,
    val personalityProfile: PersonalityProfile = PersonalityProfile()
)
