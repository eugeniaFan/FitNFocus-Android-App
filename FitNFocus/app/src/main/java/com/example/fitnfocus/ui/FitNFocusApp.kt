package com.example.fitnfocus.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.ui.navigation.FitNFocusNavHost
import com.example.fitnfocus.ui.navigation.NavRoutes
import com.example.fitnfocus.viewmodel.MainUiState
import com.example.fitnfocus.viewmodel.MainViewModel
import com.example.fitnfocus.viewmodel.StudyViewModel


@Composable
fun FitNFocusApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val mainViewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val mainUiState by mainViewModel.uiState.collectAsState()

    val studyViewModel: StudyViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val isSessionTimerActive = currentRoute?.startsWith("session_timer") == true

    if (mainUiState is MainUiState.Loading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("loading_screen"),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.testTag("loading_indicator")
            )
        }
        return
    }

    val isOnboarded = (mainUiState as MainUiState.Ready).isOnboarded

    val showBottomBar = currentRoute != NavRoutes.Onboarding.route &&
            isOnboarded &&
            !isSessionTimerActive

    val startDestination = if (isOnboarded) {
        NavRoutes.Home.route
    } else {
        NavRoutes.Onboarding.route
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_bar")
                ) {
                    NavigationBarItem(
                        modifier = Modifier.testTag("nav_home"),
                        selected = currentRoute == NavRoutes.Home.route,
                        onClick = {
                            navController.navigate(NavRoutes.Home.route) {
                                popUpTo(NavRoutes.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Dashboard"
                            )
                        },
                        label = { Text("Dashboard") }
                    )

                    NavigationBarItem(
                        modifier = Modifier.testTag("nav_focus"),
                        selected = currentRoute?.startsWith(NavRoutes.Focus.route) == true,
                        onClick = {
                            navController.navigate(NavRoutes.Focus.route) {
                                popUpTo(NavRoutes.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Timer, contentDescription = "Focus") },
                        label = { Text("Focus") }
                    )

                    NavigationBarItem(
                        modifier = Modifier.testTag("nav_goals"),
                        selected = currentRoute == NavRoutes.Goals.route,
                        onClick = {
                            navController.navigate(NavRoutes.Goals.route) {
                                popUpTo(NavRoutes.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.CollectionsBookmark,
                                contentDescription = "Ziele"
                            )
                        },
                        label = { Text("Ziele") }
                    )
                }
            }
        }
    ) { innerPadding ->
        FitNFocusNavHost(
            navController = navController,
            startDestination = startDestination,
            studyViewModel = studyViewModel,
            modifier = if (isSessionTimerActive) Modifier else Modifier.padding(innerPadding)
        )
    }
}