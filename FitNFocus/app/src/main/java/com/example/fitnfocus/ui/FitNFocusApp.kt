package com.example.fitnfocus.ui

import android.annotation.SuppressLint
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnfocus.di.AppViewModelProvider
import com.example.fitnfocus.ui.navigation.FitNFocusNavHost
import com.example.fitnfocus.ui.navigation.NavRoutes
import com.example.fitnfocus.viewmodel.MainUiState
import com.example.fitnfocus.viewmodel.MainViewModel
import com.example.fitnfocus.viewmodel.StudyViewModel


@SuppressLint("NewApi")  // API-Checks für ViewModels werden intern behandelt
@Composable
fun FitNFocusApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Onboarding-Status über MainViewModel (saubere Architektur: UI → ViewModel → Repository)
    val mainViewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val mainUiState by mainViewModel.uiState.collectAsState()

    // ==================== SHARED VIEWMODELS ====================
    // StudyViewModel auf App-Ebene erstellen, damit der State zwischen Tab-Wechseln erhalten bleibt
    val studyViewModel: StudyViewModel = viewModel(factory = AppViewModelProvider.Factory)

    // Prüfe ob Focus-Screen mit aktivem Timer ist
    // Navbar nur ausblenden wenn sessionId > 0 im Argument UND in der Route
    val sessionIdArg = backStackEntry?.arguments?.getInt("sessionId") ?: -1
    val routeContainsSession = currentRoute?.contains("sessionId=") == true && !currentRoute.contains("sessionId=-1")
    val isFocusTimerActive = currentRoute?.startsWith("focus") == true &&
            sessionIdArg > 0 &&
            routeContainsSession

    // Während des Ladens einen Ladebildschirm anzeigen
    if (mainUiState is MainUiState.Loading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Ab hier ist mainUiState garantiert Ready
    val isOnboarded = (mainUiState as MainUiState.Ready).isOnboarded

    // BottomBar nur anzeigen, wenn nicht im Onboarding und nicht im aktiven Timer
    val showBottomBar = currentRoute != NavRoutes.Onboarding.route &&
            isOnboarded &&
            !isFocusTimerActive

    // startDestination basierend auf Onboarding-Status
    val startDestination = if (isOnboarded) {
        NavRoutes.Home.route  // Onboarding gemacht -> Dashboard
    } else {
        NavRoutes.Onboarding.route  // Onboarding nicht gemacht -> Onboarding
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    // Dashboard - Anzeige
                    NavigationBarItem(
                        selected = currentRoute == NavRoutes.Home.route,
                        onClick = {
                            navController.navigate(NavRoutes.Home.route) {
                                popUpTo(NavRoutes.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") }
                    )

                    // Focus - Timer & Sammelfiguren
                    NavigationBarItem(selected = currentRoute?.startsWith(NavRoutes.Focus.route) == true,
                        onClick = {
                            navController.navigate(NavRoutes.Focus.route) {
                                popUpTo(NavRoutes.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Timer, contentDescription = "Focus") },
                        label = { Text("Focus") }
                    )

                    // Ziele - Lernziele verwalten
                    NavigationBarItem(
                        selected = currentRoute == NavRoutes.Goals.route,
                        onClick = {
                            navController.navigate(NavRoutes.Goals.route) {
                                popUpTo(NavRoutes.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.CollectionsBookmark, contentDescription = "Ziele") },
                        label = { Text("Ziele") }
                    )
                }
            }
        }
    ) { innerPadding ->
        FitNFocusNavHost(
            navController = navController,
            startDestination = startDestination,
            studyViewModel = studyViewModel,  // Shared ViewModel weitergeben
            modifier = if (isFocusTimerActive) Modifier else Modifier.padding(innerPadding)
        )
    }
}