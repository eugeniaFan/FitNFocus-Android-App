package com.example.fitnfocus.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * DataStore-Infrastruktur für User-Präferenzen.
 *
 * Diese Datei enthält ausschließlich die DataStore-Setup-Details:
 * - Context Extension für den DataStore
 * - DataStore-Name
 *
 * Wird im DI-Container verwendet, um den DataStore zu erstellen
 * und ins Repository zu injizieren.
 */

private const val USER_PREFERENCES_NAME = "user_preferences"

/**
 * Context Extension für den User-Preferences DataStore.
 * Singleton-Pattern: Erstellt den DataStore nur einmal pro Application.
 */
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

