package com.example.fitnfocus.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.ui.profile.components.LearningGoalItem
import com.example.fitnfocus.ui.profile.components.MotivationCard
import com.example.fitnfocus.ui.profile.components.PersonalityProfileCard
import com.example.fitnfocus.ui.profile.components.ProfileHeader
import com.example.fitnfocus.ui.profile.components.ProfileSection
import com.example.fitnfocus.ui.profile.components.ResetProfileCard
import com.example.fitnfocus.ui.profile.components.ResetProfileDialog
import com.example.fitnfocus.viewmodel.ProfileViewModel

/**
 * Profile screen showing onboarding data and saved learning goals.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val user by viewModel.user.collectAsState()
    val learningGoals by viewModel.learningGoals.collectAsState()

    // Debug-only confirmation dialog for profile reset.
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { Text("Mein Profil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ProfileHeader(
                role = user.role,
                motivationType = user.personalityProfile.motivationType
            )

            ProfileSection(title = "Persönlichkeitsprofil") {
                PersonalityProfileCard(
                    planningPreference = user.personalityProfile.planningPreference,
                    socialPreference = user.personalityProfile.socialPreference,
                    structurePreference = user.personalityProfile.structurePreference
                )
            }

            ProfileSection(title = "Motivation") {
                MotivationCard(motivationType = user.personalityProfile.motivationType)
            }

            if (learningGoals.isNotEmpty()) {
                ProfileSection(title = "Lernziele (${learningGoals.size})") {
                    learningGoals.forEach { goal ->
                        LearningGoalItem(
                            moduleName = goal.moduleName,
                            topicCount = goal.topics.size,
                            examDate = goal.examDate?.toString()
                        )
                    }
                }
            }

            ProfileSection(title = "Einstellungen") {
                ResetProfileCard(onResetClick = { showResetDialog = true })
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Reset confirmation dialog.
    if (showResetDialog) {
        ResetProfileDialog(
            onConfirm = {
                viewModel.resetProfile()
                showResetDialog = false
                onNavigateBack()
            },
            onDismiss = { showResetDialog = false }
        )
    }
}
