package com.example.fitnfocus.ui.focus

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.viewmodel.FocusViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FocusRoute(
    autoStartSessionId: Int? = null,
    onNavigateToCollection: () -> Unit,
    onSessionCompleted: () -> Unit,
    onSessionStopped: () -> Unit = {},  // Stop/Cancel → Dashboard
    viewModel: FocusViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(Unit) {
        viewModel.loadTodaySessions()
    }

    FocusScreen(
        autoStartSessionId = autoStartSessionId,
        onNavigateToCollection = onNavigateToCollection,
        onSessionCompleted = onSessionCompleted,
        onSessionStopped = onSessionStopped,
        viewModel = viewModel
    )
}