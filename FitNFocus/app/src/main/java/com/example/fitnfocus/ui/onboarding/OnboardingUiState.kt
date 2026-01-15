package com.example.fitnfocus.ui.onboarding

import com.example.fitnfocus.domain.MotivationType
import com.example.fitnfocus.domain.UserRole
import java.time.LocalDate

/**
 * UI State für das Onboarding.
 * Enthält alle Daten, die während des Onboardings gesammelt werden.
 */
data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 6,

    // Step 1: Rolle
    val selectedRole: UserRole = UserRole.STUDENT,

    // Step 2: Modul/Prüfung
    val moduleName: String = "",
    val topics: List<String> = emptyList(),
    val currentTopic: String = "",
    val examDate: LocalDate? = null,

    // Step 3: Motivation
    val selectedMotivation: MotivationType = MotivationType.STRUCTURED_PROGRESS,

    // Step 4: Persönlichkeit (0-100)
    val planningPreference: Int = 50,
    val socialPreference: Int = 50,
    val structurePreference: Int = 50,

    // Status
    val isSaving: Boolean = false,
    val isCompleted: Boolean = false
)

/**
 * Sealed Interface für alle Onboarding-Events.
 * Ermöglicht typsichere Event-Verarbeitung.
 */
sealed interface OnboardingEvent {
    // Navigation
    data object NextStep : OnboardingEvent
    data object PreviousStep : OnboardingEvent
    data object Skip : OnboardingEvent
    data object Complete : OnboardingEvent

    // Step 1: Rolle
    data class SelectRole(val role: UserRole) : OnboardingEvent

    // Step 2: Modul
    data class UpdateModuleName(val name: String) : OnboardingEvent
    data class UpdateCurrentTopic(val topic: String) : OnboardingEvent
    data object AddTopic : OnboardingEvent
    data class RemoveTopic(val topic: String) : OnboardingEvent
    data class UpdateExamDate(val date: LocalDate?) : OnboardingEvent

    // Step 3: Motivation
    data class SelectMotivation(val type: MotivationType) : OnboardingEvent

    // Step 4: Persönlichkeit
    data class UpdatePlanningPreference(val value: Int) : OnboardingEvent
    data class UpdateSocialPreference(val value: Int) : OnboardingEvent
    data class UpdateStructurePreference(val value: Int) : OnboardingEvent
}

