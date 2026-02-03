package com.example.fitnfocus.data.mapper

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import com.example.fitnfocus.data.datastore.UserPreferencesKeys
import com.example.fitnfocus.domain.PersonalityProfile
import com.example.fitnfocus.domain.User

/**
 * Mapper between DataStore Preferences and User domain model.
 * Uses domain defaults as single source of truth for fallback values.
 */
object UserPreferencesMapper {

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
        if (raw == null) return default  // use default, if no string
        return try {
            enumValueOf<T>(raw)  // converting into enum
        } catch (e: IllegalArgumentException) {
            Log.e("UserPreferencesMapper", "Invalid enum value: $raw", e)
            default
        }
    }
}
