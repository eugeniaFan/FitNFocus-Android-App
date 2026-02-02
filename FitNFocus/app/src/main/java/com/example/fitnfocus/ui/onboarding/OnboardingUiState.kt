package com.example.fitnfocus.ui.onboarding

import com.example.fitnfocus.domain.MotivationType
import com.example.fitnfocus.domain.UserRole
import java.time.LocalDate

/**
 * UI state for the onboarding flow.
 * Holds all data collected across onboarding steps.
 */
data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalSteps: Int = 6,

    val selectedRole: UserRole = UserRole.STUDENT,

    val moduleName: String = "",
    val topics: List<String> = emptyList(),
    val currentTopic: String = "",
    val examDate: LocalDate? = null,

    val selectedMotivation: MotivationType = MotivationType.STRUCTURED_PROGRESS,

    val planningPreference: Int = 50,
    val socialPreference: Int = 50,
    val structurePreference: Int = 50,

    val isSaving: Boolean = false,
    val isCompleted: Boolean = false,
    val errorMessage: String? = null
)

sealed interface OnboardingEvent {
    data object NextStep : OnboardingEvent
    data object PreviousStep : OnboardingEvent
    data object Skip : OnboardingEvent
    data object Complete : OnboardingEvent

    data class SelectRole(val role: UserRole) : OnboardingEvent

    data class UpdateModuleName(val name: String) : OnboardingEvent
    data class UpdateCurrentTopic(val topic: String) : OnboardingEvent
    data object AddTopic : OnboardingEvent
    data class RemoveTopic(val topic: String) : OnboardingEvent
    data class UpdateExamDate(val date: LocalDate?) : OnboardingEvent

    data class SelectMotivation(val type: MotivationType) : OnboardingEvent

    data class UpdatePlanningPreference(val value: Int) : OnboardingEvent
    data class UpdateSocialPreference(val value: Int) : OnboardingEvent
    data class UpdateStructurePreference(val value: Int) : OnboardingEvent
}