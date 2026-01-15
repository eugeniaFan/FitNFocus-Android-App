package com.example.fitnfocus.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.fitnfocus.domain.MotivationType
import com.example.fitnfocus.domain.PersonalityProfile
import com.example.fitnfocus.domain.User
import com.example.fitnfocus.domain.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore Extension für den Context
private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repository für Benutzer-Präferenzen.
 * Verwendet DataStore für persistente Speicherung von Onboarding-Status,
 * Benutzerrolle und Persönlichkeitsprofil.
 */
class UserPreferencesRepository(private val context: Context) {

    // Preference Keys
    private object PreferencesKeys {
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val USER_ROLE = stringPreferencesKey("user_role")
        val PLANNING_PREFERENCE = intPreferencesKey("planning_preference")
        val SOCIAL_PREFERENCE = intPreferencesKey("social_preference")
        val STRUCTURE_PREFERENCE = intPreferencesKey("structure_preference")
        val MOTIVATION_TYPE = stringPreferencesKey("motivation_type")
    }

    /**
     * Flow, der den aktuellen Benutzer mit allen Präferenzen liefert.
     */
    val userFlow: Flow<User> = context.userDataStore.data.map { preferences ->
        User(
            role = preferences[PreferencesKeys.USER_ROLE]?.let {
                UserRole.valueOf(it)
            } ?: UserRole.STUDENT,
            isOnboarded = preferences[PreferencesKeys.IS_ONBOARDED] ?: false,
            personalityProfile = PersonalityProfile(
                planningPreference = preferences[PreferencesKeys.PLANNING_PREFERENCE] ?: 50,
                socialPreference = preferences[PreferencesKeys.SOCIAL_PREFERENCE] ?: 50,
                structurePreference = preferences[PreferencesKeys.STRUCTURE_PREFERENCE] ?: 50,
                motivationType = preferences[PreferencesKeys.MOTIVATION_TYPE]?.let {
                    MotivationType.valueOf(it)
                } ?: MotivationType.STRUCTURED_PROGRESS
            )
        )
    }

    /**
     * Flow, der nur den Onboarding-Status liefert.
     * Wird verwendet, um zu entscheiden, ob das Onboarding angezeigt werden soll.
     */
    val isOnboardedFlow: Flow<Boolean> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_ONBOARDED] ?: false
    }

    /**
     * Speichert das komplette Benutzerprofil nach dem Onboarding.
     */
    suspend fun saveUser(user: User) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDED] = user.isOnboarded
            preferences[PreferencesKeys.USER_ROLE] = user.role.name
            preferences[PreferencesKeys.PLANNING_PREFERENCE] = user.personalityProfile.planningPreference
            preferences[PreferencesKeys.SOCIAL_PREFERENCE] = user.personalityProfile.socialPreference
            preferences[PreferencesKeys.STRUCTURE_PREFERENCE] = user.personalityProfile.structurePreference
            preferences[PreferencesKeys.MOTIVATION_TYPE] = user.personalityProfile.motivationType.name
        }
    }

    /**
     * Markiert das Onboarding als abgeschlossen.
     */
    suspend fun setOnboardingCompleted() {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDED] = true
        }
    }

    /**
     * Setzt alle Präferenzen zurück (für Testzwecke).
     */
    suspend fun clearAllPreferences() {
        context.userDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

