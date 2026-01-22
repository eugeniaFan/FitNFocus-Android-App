package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * UI-State für den App-Start.
 */
sealed interface MainUiState {
    /** Daten werden geladen (DataStore wird initialisiert) */
    data object Loading : MainUiState

    /** Daten sind bereit, Onboarding-Status ist bekannt */
    data class Ready(val isOnboarded: Boolean) : MainUiState
}

/**
 * ViewModel für den App-Start.
 *
 * Verantwortlich für:
 * - Laden des Onboarding-Status aus dem DataStore
 * - Bereitstellung eines klaren UI-States (Loading/Ready)
 *
 * Wird in FitNFocusApp verwendet, um die Start-Destination zu bestimmen.
 */
class MainViewModel(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /**
     * UI-State für den App-Start.
     * - Loading: DataStore wird initialisiert
     * - Ready: Onboarding-Status ist bekannt
     */
    val uiState: StateFlow<MainUiState> = userPreferencesRepository.isOnboardedFlow
        .map { isOnboarded -> MainUiState.Ready(isOnboarded) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState.Loading
        )
}

