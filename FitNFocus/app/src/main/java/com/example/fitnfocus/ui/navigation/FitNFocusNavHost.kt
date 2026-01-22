package com.example.fitnfocus.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fitnfocus.ui.focus.CollectionScreen
import com.example.fitnfocus.ui.focus.FocusRoute
import com.example.fitnfocus.ui.home.HomeScreen
import com.example.fitnfocus.ui.onboarding.OnboardingRoute
import com.example.fitnfocus.ui.profile.ProfileScreen
import com.example.fitnfocus.ui.goals.study.StudyRoute
import com.example.fitnfocus.viewmodel.StudyViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FitNFocusNavHost(
    navController: NavHostController,
    startDestination: String,
    studyViewModel: StudyViewModel,  // Shared ViewModel für State-Persistenz
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // ONBOARDING SCREEN
        composable(NavRoutes.Onboarding.route) {
            OnboardingRoute(
                onOnboardingComplete = {
                    // Nach dem Onboarding zur Home-Route navigieren und Onboarding aus Backstack entfernen
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // HOME SCREEN (Dashboard)
        composable(NavRoutes.Home.route) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.Profile.route)
                },
                onNavigateToFocus = {
                    navController.navigate(NavRoutes.Focus.route)
                },
                onStartSession = { sessionId ->
                    // Direkt zum Focus-Screen mit Session-ID navigieren
                    navController.navigate(NavRoutes.focusWithSession(sessionId))
                }
            )
        }

        // PROFILE SCREEN
        composable(NavRoutes.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // FOCUS SCREEN (Timer & Sammelfiguren) mit sessionId Parameter
        composable(
            route = NavRoutes.FOCUS_WITH_SESSION,
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.IntType
                    defaultValue = -1  // -1 bedeutet: kein direkter Timer-Start
                }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: -1
            FocusRoute(
                autoStartSessionId = if (sessionId > 0) sessionId else null,
                onNavigateToCollection = { navController.navigate(NavRoutes.Collection.route) },
                onSessionCompleted = {
                    // Erfolgreiche Session → Focus-Bereich
                    navController.navigate(NavRoutes.Focus.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onSessionStopped = {
                    // Stop/Cancel → Dashboard
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // COLLECTION SCREEN
        composable(NavRoutes.Collection.route) {
            CollectionScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // GOALS SCREEN
        composable(NavRoutes.Goals.route) {
            StudyRoute(
                onBack = { navController.popBackStack() },
                viewModel = studyViewModel,  // Shared ViewModel verwenden
                onSessionStopped = {
                    // Bei Stop/Cancel: zum Dashboard navigieren
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onSessionCompleted = {
                    // Bei erfolgreicher Session: zum Focus-Bereich
                    navController.navigate(NavRoutes.Focus.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}