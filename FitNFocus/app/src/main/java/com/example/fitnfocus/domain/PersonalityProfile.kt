package com.example.fitnfocus.domain

/**
 * User personality profile based on simplified Big Five scales.
 * Values range from 0 to 100 representing preference strength.
 */
data class PersonalityProfile(
    val planningPreference: Int = 50,
    val socialPreference: Int = 50,
    val structurePreference: Int = 50,
    val motivationType: MotivationType = MotivationType.STRUCTURED_PROGRESS
)
