package com.example.fitnfocus.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnfocus.ui.onboarding.components.OnboardingNavigationButtons
import com.example.fitnfocus.ui.onboarding.components.OnboardingProgressIndicator
import com.example.fitnfocus.ui.onboarding.components.OnboardingTopBar
import com.example.fitnfocus.ui.onboarding.steps.*

/**
 * Hauptbildschirm für das Onboarding.
 * Koordiniert die einzelnen Steps und die Navigation.
 */
@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header: Step-Anzeige + Skip
        OnboardingTopBar(
            currentStep = uiState.currentStep,
            totalSteps = uiState.totalSteps,
            onSkip = { onEvent(OnboardingEvent.Skip) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fortschrittsbalken
        OnboardingProgressIndicator(
            currentStep = uiState.currentStep,
            totalSteps = uiState.totalSteps
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hauptinhalt - animierter Wechsel zwischen Steps
        AnimatedContent(
            targetState = uiState.currentStep,
            modifier = Modifier.weight(1f),
            label = "onboarding_step"
        ) { step ->
            OnboardingStepContent(step = step, uiState = uiState, onEvent = onEvent)
        }

        // Footer: Navigation Buttons
        OnboardingNavigationButtons(
            currentStep = uiState.currentStep,
            totalSteps = uiState.totalSteps,
            isSaving = uiState.isSaving,
            onPrevious = { onEvent(OnboardingEvent.PreviousStep) },
            onNext = { onEvent(OnboardingEvent.NextStep) },
            onComplete = { onEvent(OnboardingEvent.Complete) }
        )
    }
}

/**
 * Rendert den Inhalt für den aktuellen Step.
 */
@Composable
private fun OnboardingStepContent(
    step: Int,
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    when (step) {
        0 -> WelcomeStep()

        1 -> RoleStep(
            selectedRole = uiState.selectedRole,
            onRoleSelected = { onEvent(OnboardingEvent.SelectRole(it)) }
        )

        2 -> ModuleStep(
            moduleName = uiState.moduleName,
            topics = uiState.topics,
            currentTopic = uiState.currentTopic,
            examDate = uiState.examDate,
            onModuleNameChanged = { onEvent(OnboardingEvent.UpdateModuleName(it)) },
            onCurrentTopicChanged = { onEvent(OnboardingEvent.UpdateCurrentTopic(it)) },
            onAddTopic = { onEvent(OnboardingEvent.AddTopic) },
            onRemoveTopic = { onEvent(OnboardingEvent.RemoveTopic(it)) },
            onExamDateChanged = { onEvent(OnboardingEvent.UpdateExamDate(it)) }
        )

        3 -> MotivationStep(
            selectedMotivation = uiState.selectedMotivation,
            onMotivationSelected = { onEvent(OnboardingEvent.SelectMotivation(it)) }
        )

        4 -> PersonalityStep(
            planningPreference = uiState.planningPreference,
            socialPreference = uiState.socialPreference,
            structurePreference = uiState.structurePreference,
            onPlanningChanged = { onEvent(OnboardingEvent.UpdatePlanningPreference(it)) },
            onSocialChanged = { onEvent(OnboardingEvent.UpdateSocialPreference(it)) },
            onStructureChanged = { onEvent(OnboardingEvent.UpdateStructurePreference(it)) }
        )

        5 -> SummaryStep(uiState = uiState)
    }
}

