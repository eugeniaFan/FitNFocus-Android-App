package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * UI state for app startup.
 */
sealed interface MainUiState {
    data object Loading : MainUiState
    data class Ready(val isOnboarded: Boolean) : MainUiState
}

/**
 * ViewModel for app startup.
 * Loads onboarding status and provides clear UI state for navigation.
 */
class MainViewModel(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = userPreferencesRepository.isOnboardedFlow
        .map { isOnboarded ->
            MainUiState.Ready(isOnboarded)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState.Loading
        )
}

