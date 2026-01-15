package com.example.fitnfocus.domain

/**
 * Persönlichkeitsprofil des Nutzers.
 * Basiert auf vereinfachten Big-Five-Skalen für das Onboarding.
 *
 * Die Werte reichen von 0 bis 100:
 * - planningPreference: 0 = sehr spontan, 100 = sehr planorientiert
 * - socialPreference: 0 = Einzelarbeit bevorzugt, 100 = Teamarbeit bevorzugt
 * - structurePreference: 0 = flexible Struktur, 100 = feste Routinen
 */
data class PersonalityProfile(
    val planningPreference: Int = 50,      // Planung vs. Spontanität
    val socialPreference: Int = 50,         // Einzelarbeit vs. Teamarbeit
    val structurePreference: Int = 50,      // Flexibel vs. Strukturiert
    val motivationType: MotivationType = MotivationType.STRUCTURED_PROGRESS
)

