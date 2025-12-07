package com.example.fitnfocus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitnfocus.ui.activity.ActivityScreen
import com.example.fitnfocus.ui.history.HistoryScreen
import com.example.fitnfocus.ui.home.HomeScreen
import com.example.fitnfocus.ui.study.StudyScreen

@Composable
fun FitNFocusNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
        modifier = modifier
    ) {

        // HOME SCREEN
        composable(NavRoutes.Home.route) {
            HomeScreen(
                onNavigateToActivity = { navController.navigate(NavRoutes.Activity.route) },
                onNavigateToStudy = { navController.navigate(NavRoutes.Study.route) },
                onNavigateToHistory = { navController.navigate(NavRoutes.History.route) }
            )
        }

        // ACTIVITY TRACKING SCREEN
        composable(NavRoutes.Activity.route) {
            ActivityScreen(onBack = { navController.popBackStack() })
        }

        // STUDY SCREEN
        composable(NavRoutes.Study.route) {
            StudyScreen(
                ///viewModel = viewModel(factory = AppViewModelProvider.Factory),
                onBack = { navController.popBackStack() }
            )
        }
    }
}