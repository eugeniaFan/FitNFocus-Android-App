package com.example.fitnfocus.ui.activity

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
    val highMovementMinutes = todayActivity?.highMovementMinutes ?: 0

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Activity Tracking") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, contentDescription = "Back"
                    )
                }
            })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Steps today", style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "$steps", style = MaterialTheme.typography.headlineMedium
                    )
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    Text(
                        text = "$steps / $goal steps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


            }
            Button(
                onClick = { viewModel.addSteps(1000) },
            ) {
                Text(text = "Add 1000 steps")
            }
        }

    }


}
