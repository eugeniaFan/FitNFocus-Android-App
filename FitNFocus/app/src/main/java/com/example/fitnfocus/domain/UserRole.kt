package com.example.fitnfocus.domain

/**
 * User roles for app personalization.
 * Collected during onboarding to provide tailored content.
 */
enum class UserRole(val displayName: String) {
    STUDENT("Student:in"),
    PROFESSIONAL("Berufstätige:r"),
    OTHER("Anderes")
}
