package com.example.fitnfocus.domain

/**
 * Benutzer-Rollen für die App.
 * Wird im Onboarding abgefragt, um personalisierte Inhalte anzubieten.
 */
enum class UserRole(val displayName: String) {
    STUDENT("Student:in"),
    PROFESSIONAL("Berufstätige:r"),
    OTHER("Anderes")
}


