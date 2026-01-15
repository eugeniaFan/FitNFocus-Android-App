package com.example.fitnfocus.ui.activity

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.ui.designsystem.components.FnfCard
import com.example.fitnfocus.ui.designsystem.components.FnfPrimaryButton
import com.example.fitnfocus.ui.designsystem.components.FnfScreenScaffold
import com.example.fitnfocus.ui.theme.Dimens
import com.example.fitnfocus.viewmodel.ActivityViewModel
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivityViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val todayActivity by viewModel.activity.collectAsState()

    val today = LocalDate.now().toString()

    LaunchedEffect(today) {
        viewModel.loadActivity(today)
    }
    val steps = todayActivity?.steps ?: 0
    val goal = 10000
    val progress = (steps.toFloat() / goal).coerceIn(0f, 1f)
    val highMovementMinutes = todayActivity?.highMovementMinutes ?: 0 // Später vllt

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )


    FnfScreenScaffold(
        title = "Activity Tracking",
        onBack = onBack
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(Dimens.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FnfCard(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Text("Steps today", style = MaterialTheme.typography.titleMedium)
                Text("$steps", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(Dimens.s))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.s)


                )
                Text(
                    "$steps / $goal steps",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = Dimens.s)
                )
            }

            FnfPrimaryButton(
                text = "Add 1000 steps",
                onClick = { viewModel.addSteps(1000) }
            )
        }
    }



}
