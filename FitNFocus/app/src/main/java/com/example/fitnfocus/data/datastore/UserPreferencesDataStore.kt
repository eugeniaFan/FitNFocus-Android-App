package com.example.fitnfocus.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * DataStore infrastructure for user preferences.
 * Provides singleton access to user preference storage via Context extension.
 */

private const val USER_PREFERENCES_NAME = "user_preferences"

/**
 * Context extension for user preferences DataStore.
 * Implements singleton pattern ensuring single instance per application.
 */
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)
