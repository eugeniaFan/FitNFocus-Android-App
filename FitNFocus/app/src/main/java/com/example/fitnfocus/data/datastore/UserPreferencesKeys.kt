package com.example.fitnfocus.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Central definition of all DataStore preference keys for user data.
 * Ensures consistency and prevents key duplication across the application.
 */
object UserPreferencesKeys {

    val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
    val USER_ROLE = stringPreferencesKey("user_role")
    val PLANNING_PREFERENCE = intPreferencesKey("planning_preference")
    val SOCIAL_PREFERENCE = intPreferencesKey("social_preference")
    val STRUCTURE_PREFERENCE = intPreferencesKey("structure_preference")
    val MOTIVATION_TYPE = stringPreferencesKey("motivation_type")
}

