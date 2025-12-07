package com.example.fitnfocus.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.viewmodel.ActivityViewModel
import com.example.fitnfocus.di.AppViewModelProvider

@Composable
fun HistoryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivityViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val activity by viewModel.activity.collectAsState()

    Column(
        modifier = modifier
    ) {
        Text(text = "Today's History")
        Text(text = "Steps: ${activity?.steps ?: 0}")
        Button(onClick = navigateBack) {
            Text("Back")
        }

    }
}