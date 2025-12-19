package com.example.fitnfocus.ui.study

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.di.FitNFocusApplication
import com.example.fitnfocus.viewmodel.StudyUiEvent
import com.example.fitnfocus.viewmodel.StudyViewModel
import kotlinx.coroutines.launch

@Composable
fun StudyRoute(
    onBack: () -> Unit,
    viewModel: StudyViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val app = context.applicationContext as FitNFocusApplication
    val calendarExporter = app.container.calendarExporter
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    // Events zentral sammeln (einmal pro Route)
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                StudyUiEvent.CloseAddDialog -> {
                    viewModel.setShowAddDialog(false)
                }
                is StudyUiEvent.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
                is StudyUiEvent.OpenCalendarInsert -> {
                    val intent = calendarExporter.buildInsertIntent(event.event)
                    context.startActivity(intent)
                }
            }
        }
    }
    StudyScreen(
        onBack = onBack,
        viewModel = viewModel,
        snackbarHostState = snackbarHostState
    )
}
