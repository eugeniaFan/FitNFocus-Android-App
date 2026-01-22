package com.example.fitnfocus.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.fitnfocus.data.datastore.UserPreferencesKeys
import com.example.fitnfocus.data.mapper.UserPreferencesMapper
import com.example.fitnfocus.domain.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository für Benutzer-Präferenzen.
 *
 * Verantwortlichkeiten:
 * - Flows für reaktives Lesen von User-Daten
 * - Edit-Operationen für Schreibzugriffe
 *
 * Best Practices:
 * - Kein Context: DataStore wird über DI injiziert (testbar!)
 * - Mapping delegiert an UserPreferencesMapper (Single Responsibility)
 * - Keys aus UserPreferencesKeys (keine Magic Strings)
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * Flow, der den aktuellen Benutzer mit allen Präferenzen liefert.
     * Reagiert automatisch auf Änderungen im DataStore.
     */
    val userFlow: Flow<User> = dataStore.data.map { preferences ->
        UserPreferencesMapper.preferencesToUser(preferences)
    }

    /**
     * Flow, der nur den Onboarding-Status liefert.
     * Wird verwendet, um zu entscheiden, ob das Onboarding angezeigt werden soll.
     */
    val isOnboardedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        UserPreferencesMapper.preferencesToIsOnboarded(preferences)
    }

    /**
     * Speichert das komplette Benutzerprofil nach dem Onboarding.
     */
    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_ONBOARDED] = user.isOnboarded
            preferences[UserPreferencesKeys.USER_ROLE] = user.role.name
            preferences[UserPreferencesKeys.PLANNING_PREFERENCE] = user.personalityProfile.planningPreference
            preferences[UserPreferencesKeys.SOCIAL_PREFERENCE] = user.personalityProfile.socialPreference
            preferences[UserPreferencesKeys.STRUCTURE_PREFERENCE] = user.personalityProfile.structurePreference
            preferences[UserPreferencesKeys.MOTIVATION_TYPE] = user.personalityProfile.motivationType.name
        }
    }

    /**
     * Markiert das Onboarding als abgeschlossen.
     */
    suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_ONBOARDED] = true
        }
    }

    /**
     * Setzt alle Präferenzen zurück (für Testzwecke).
     */
    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

