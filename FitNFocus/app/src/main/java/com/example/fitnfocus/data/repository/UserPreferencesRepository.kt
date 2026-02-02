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
 * Repository for user preferences and profile data.
 * Manages DataStore operations and provides reactive flows for user state.
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * Reactive flow of current user with all preferences.
     * Automatically updates when DataStore changes.
     */
    val userFlow: Flow<User> = dataStore.data.map { preferences ->
        UserPreferencesMapper.preferencesToUser(preferences)
    }

    /**
     * Reactive flow of onboarding status.
     * Used to determine whether to show onboarding screens.
     */
    val isOnboardedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        UserPreferencesMapper.preferencesToIsOnboarded(preferences)
    }

    /**
     * Saves complete user profile after onboarding.
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

    suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.IS_ONBOARDED] = true
        }
    }

    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

