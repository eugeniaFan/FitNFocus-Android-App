package com.example.fitnfocus.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.viewmodel.StudyViewModel

@Composable
fun StudyScreen(
    onBack: () -> Unit,
    viewModel: StudyViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val subject by viewModel.subject.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(text = "Study Session", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = subject,
            onValueChange = { viewModel.onSubjectChange(it) },
            label = { Text("Subject") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = duration,
            onValueChange = { viewModel.onDurationChange(it) },
            label = { Text("Duration (minutes)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.saveStudySession("2025-12-07") // später dynamisch LocalDate.now()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Study Session")
        }

        if (statusMessage != null) {
            Text(text = statusMessage!!)
        }

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}