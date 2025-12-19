package com.example.fitnfocus.ui.home

import android.R.attr.icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.School

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.viewmodel.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val steps by viewModel.todaySteps.collectAsState()
    val focusMinutes by viewModel.todayFocusMinutes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTodayDashboard()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fit & Focus") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ----- DASHBOARD HEADER -----
            Text(
                text = "Track your progress and stay focused.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text("Today", style = MaterialTheme.typography.headlineSmall)

            ElevatedCard(Modifier.fillMaxWidth()){
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text("Activity")
                    Text("$steps steps")
                }
            }
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Focus")
                    Text("$focusMinutes minutes")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))




            // ----- DASHBOARD BUTTONS -----
//            DashboardButton(
//                title = "Track Activity",
//                icon = Icons.AutoMirrored.Filled.DirectionsRun,
//                description = "Record daily movement",
//                onClick = onNavigateToActivity
//            )
//
//            DashboardButton(
//                title = "Study Session",
//                icon = Icons.Default.School,
//                description = "Start a focused study timer",
//                onClick = onNavigateToStudy
//            )
//
//            DashboardButton(
//                title = "History",
//                icon = Icons.Default.History,
//                description = "View past progress",
//                onClick = onNavigateToHistory
//            )
        }
    }
}

@Composable
fun DashboardButton(
    title: String,
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
