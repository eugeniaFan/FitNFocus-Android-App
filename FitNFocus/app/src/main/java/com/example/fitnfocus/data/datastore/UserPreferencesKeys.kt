package com.example.fitnfocus.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Zentrale Definition aller Preference Keys für den User-DataStore.
 *
 * Vorteile:
 * - Keys sind konsistent und leicht auffindbar
 * - Keine Key-Dopplungen möglich
 * - Änderungen an einem Ort
 *
 * WICHTIG: Key-Namen sollten stabil bleiben!
 * Wenn du Key-Namen änderst, wirken alte gespeicherte Werte wie "gelöscht".
 */
object UserPreferencesKeys {

    // Onboarding-Status
    val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")

    // Benutzer-Rolle (als String gespeichert: "STUDENT", "PROFESSIONAL", "OTHER")
    val USER_ROLE = stringPreferencesKey("user_role")

    // Persönlichkeitsprofil - Slider-Werte (0-100)
    val PLANNING_PREFERENCE = intPreferencesKey("planning_preference")
    val SOCIAL_PREFERENCE = intPreferencesKey("social_preference")
    val STRUCTURE_PREFERENCE = intPreferencesKey("structure_preference")

    // Motivationstyp (als String gespeichert: Enum.name)
    val MOTIVATION_TYPE = stringPreferencesKey("motivation_type")
}

