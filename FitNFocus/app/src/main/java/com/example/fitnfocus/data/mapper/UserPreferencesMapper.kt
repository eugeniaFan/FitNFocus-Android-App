package com.example.fitnfocus.data.mapper

import androidx.datastore.preferences.core.Preferences
import com.example.fitnfocus.data.datastore.UserPreferencesKeys
import com.example.fitnfocus.domain.PersonalityProfile
import com.example.fitnfocus.domain.User

/**
 * Mapper für User-Präferenzen zwischen DataStore (Preferences) und Domain (User).
 *
 * Defaults werden aus den Domain-Klassen übernommen (Single Source of Truth).
 */
object UserPreferencesMapper {

    // Domain-Defaults als Referenz (aus User() und PersonalityProfile())
    private val defaults = User()

    /**
     * Konvertiert DataStore Preferences zu einem User Domain-Modell.
     */
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

    /**
     * Extrahiert nur den Onboarding-Status aus Preferences.
     */
    fun preferencesToIsOnboarded(preferences: Preferences): Boolean {
        return preferences[UserPreferencesKeys.IS_ONBOARDED] ?: defaults.isOnboarded
    }

    /**
     * Parst ein Enum sicher aus einem String.
     * Gibt Default zurück wenn raw null ist oder kein gültiger Enum-Wert.
     */
    private inline fun <reified T : Enum<T>> parseEnum(raw: String?, default: T): T {
        return raw?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: default
    }
}

