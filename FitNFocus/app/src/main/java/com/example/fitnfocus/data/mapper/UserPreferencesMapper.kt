package com.example.fitnfocus.data.mapper

import androidx.datastore.preferences.core.Preferences
import com.example.fitnfocus.data.datastore.UserPreferencesKeys
import com.example.fitnfocus.domain.PersonalityProfile
import com.example.fitnfocus.domain.User

/**
 * Mapper between DataStore Preferences and User domain model.
 * Uses domain defaults as single source of truth for fallback values.
 */
object UserPreferencesMapper { // TODO Simplify and align with other mapper implementations

    private val defaults = User()

    fun preferencesToUser(preferences: Preferences): User {
        return User(
            role = parseEnum(preferences[UserPreferencesKeys.USER_ROLE], defaults.role),
            isOnboarded = preferences[UserPreferencesKeys.IS_ONBOARDED] ?: defaults.isOnboarded,
            personalityProfile = PersonalityProfile(
                planningPreference = preferences[UserPreferencesKeys.PLANNING_PREFERENCE]
                    ?: defaults.personalityProfile.planningPreference,
                socialPreference = preferences[UserPreferencesKeys.SOCIAL_PREFERENCE]
                    ?: defaults.personalityProfile.socialPreference,
                structurePreference = preferences[UserPreferencesKeys.STRUCTURE_PREFERENCE]
                    ?: defaults.personalityProfile.structurePreference,
                motivationType = parseEnum(
                    preferences[UserPreferencesKeys.MOTIVATION_TYPE],
                    defaults.personalityProfile.motivationType
                )
            )
        )
    }

    fun preferencesToIsOnboarded(preferences: Preferences): Boolean {
        return preferences[UserPreferencesKeys.IS_ONBOARDED] ?: defaults.isOnboarded
    }

    /**
     * Safely parses enum from string with fallback to default value.
     */
    private inline fun <reified T : Enum<T>> parseEnum(raw: String?, default: T): T {
        return raw?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: default
    }
}
