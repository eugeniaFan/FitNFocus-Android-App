package com.example.fitnfocus.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.viewmodel.OnboardingViewModel

/**
* Route entry for onboarding that binds the screen to its ViewModel.
 */
@Composable
fun OnboardingRoute(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate to the main flow once onboarding is completed.
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onOnboardingComplete()
        }
    }

    OnboardingScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}
