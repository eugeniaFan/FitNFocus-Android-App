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
 * ViewModel for onboarding flow.
 * Manages onboarding state and processes all user events.
 */
class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val learningGoalRepository: LearningGoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            OnboardingEvent.NextStep -> nextStep()
            OnboardingEvent.PreviousStep -> previousStep()
            OnboardingEvent.Skip -> skipOnboarding()
            OnboardingEvent.Complete -> completeOnboarding()

            is OnboardingEvent.SelectRole ->
                _uiState.update { it.copy(selectedRole = event.role) }

            is OnboardingEvent.UpdateModuleName ->
                _uiState.update { it.copy(moduleName = event.name) }

            is OnboardingEvent.UpdateCurrentTopic ->
                _uiState.update { it.copy(currentTopic = event.topic) }

            OnboardingEvent.AddTopic -> addTopic()
            is OnboardingEvent.RemoveTopic ->
                _uiState.update { it.copy(topics = it.topics - event.topic) }

            is OnboardingEvent.UpdateExamDate ->
                _uiState.update { it.copy(examDate = event.date) }

            is OnboardingEvent.SelectMotivation ->
                _uiState.update { it.copy(selectedMotivation = event.type) }

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
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                val state = _uiState.value

                // Persist user profile
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

                // Persist learning goal if provided
                if (state.moduleName.isNotBlank()) {
                    learningGoalRepository.insertGoal(
                        LearningGoal(
                            moduleName = state.moduleName,
                            topics = state.topics,
                            examDate = state.examDate
                        )
                    )
                }

                _uiState.update { it.copy(isCompleted = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "Speichern fehlgeschlagen. Bitte erneut versuchen.") }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun skipOnboarding() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                userPreferencesRepository.saveUser(
                    User(
                        role = UserRole.STUDENT,
                        isOnboarded = true,
                        personalityProfile = PersonalityProfile()
                    )
                )

                _uiState.update { it.copy(isCompleted = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "Speichern fehlgeschlagen. Bitte erneut versuchen.") }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
