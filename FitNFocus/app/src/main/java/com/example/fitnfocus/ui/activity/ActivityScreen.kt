package com.example.fitnfocus.ui.activity

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.viewmodel.ActivityViewModel


@Composable
fun ActivityScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivityViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Column{
        Text("Activity Tracking")
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}