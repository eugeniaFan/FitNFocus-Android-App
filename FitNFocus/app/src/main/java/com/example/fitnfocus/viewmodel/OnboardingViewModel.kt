package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.UserPreferencesRepository
import com.example.fitnfocus.domain.LearningGoal
import com.example.fitnfocus.domain.PersonalityProfile
import com.example.fitnfocus.domain.User
import com.example.fitnfocus.domain.UserRole
import com.example.fitnfocus.ui.onboarding.OnboardingEvent
import com.example.fitnfocus.ui.onboarding.OnboardingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel für das Onboarding.
 * Verwaltet den Zustand und verarbeitet alle Events.
 */
class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val learningGoalRepository: LearningGoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    /**
     * Zentrale Event-Verarbeitung - Single Point of Entry
     */
    fun onEvent(event: OnboardingEvent) {
        when (event) {
            // Navigation
            OnboardingEvent.NextStep -> nextStep()
            OnboardingEvent.PreviousStep -> previousStep()
            OnboardingEvent.Skip -> skipOnboarding()
            OnboardingEvent.Complete -> completeOnboarding()

            // Step 1: Rolle
            is OnboardingEvent.SelectRole ->
                _uiState.update { it.copy(selectedRole = event.role) }

            // Step 2: Modul
            is OnboardingEvent.UpdateModuleName ->
                _uiState.update { it.copy(moduleName = event.name) }
            is OnboardingEvent.UpdateCurrentTopic ->
                _uiState.update { it.copy(currentTopic = event.topic) }
            OnboardingEvent.AddTopic -> addTopic()
            is OnboardingEvent.RemoveTopic ->
                _uiState.update { it.copy(topics = it.topics - event.topic) }
            is OnboardingEvent.UpdateExamDate ->
                _uiState.update { it.copy(examDate = event.date) }

            // Step 3: Motivation
            is OnboardingEvent.SelectMotivation ->
                _uiState.update { it.copy(selectedMotivation = event.type) }

            // Step 4: Persönlichkeit
            is OnboardingEvent.UpdatePlanningPreference ->
                _uiState.update { it.copy(planningPreference = event.value.coerceIn(0, 100)) }
            is OnboardingEvent.UpdateSocialPreference ->
                _uiState.update { it.copy(socialPreference = event.value.coerceIn(0, 100)) }
            is OnboardingEvent.UpdateStructurePreference ->
                _uiState.update { it.copy(structurePreference = event.value.coerceIn(0, 100)) }
        }
    }

    private fun nextStep() {
        _uiState.update {
            it.copy(currentStep = (it.currentStep + 1).coerceAtMost(it.totalSteps - 1))
        }
    }

    private fun previousStep() {
        _uiState.update {
            it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0))
        }
    }

    private fun addTopic() {
        val topic = _uiState.value.currentTopic.trim()
        if (topic.isNotEmpty() && topic !in _uiState.value.topics) {
            _uiState.update {
                it.copy(topics = it.topics + topic, currentTopic = "")
            }
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val state = _uiState.value

            // User-Profil speichern
            val user = User(
                role = state.selectedRole,
                isOnboarded = true,
                personalityProfile = PersonalityProfile(
                    planningPreference = state.planningPreference,
                    socialPreference = state.socialPreference,
                    structurePreference = state.structurePreference,
                    motivationType = state.selectedMotivation
                )
            )
            userPreferencesRepository.saveUser(user)

            // Lernziel speichern (wenn vorhanden)
            if (state.moduleName.isNotBlank()) {
                learningGoalRepository.insertGoal(
                    LearningGoal(
                        moduleName = state.moduleName,
                        topics = state.topics,
                        examDate = state.examDate
                    )
                )
            }

            _uiState.update { it.copy(isSaving = false, isCompleted = true) }
        }
    }

    private fun skipOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            userPreferencesRepository.saveUser(
                User(
                    role = UserRole.STUDENT,
                    isOnboarded = true,
                    personalityProfile = PersonalityProfile()
                )
            )

            _uiState.update { it.copy(isSaving = false, isCompleted = true) }
        }
    }
}

