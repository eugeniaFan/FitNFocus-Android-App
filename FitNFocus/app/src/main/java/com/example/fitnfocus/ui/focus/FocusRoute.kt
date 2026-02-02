package com.example.fitnfocus.ui.focus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.viewmodel.FocusViewModel

/**
 * Route für den FocusScreen (NUR Münzen-Ansicht).
 * Timer-Logik wurde in SessionTimerRoute ausgelagert.
 */
@Composable
fun FocusRoute(
    onNavigateToCollection: () -> Unit,
    viewModel: FocusViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.loadTodaySessions()
    }

    FocusScreen(
        onNavigateToCollection = onNavigateToCollection,
        viewModel = viewModel
    )
}