package com.example.fitnfocus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fitnfocus.ui.focus.CollectionScreen
import com.example.fitnfocus.ui.focus.FocusRoute
import com.example.fitnfocus.ui.goals.study.StudyRoute
import com.example.fitnfocus.ui.goals.study.timer.SessionTimerRoute
import com.example.fitnfocus.ui.home.HomeScreen
import com.example.fitnfocus.ui.onboarding.OnboardingRoute
import com.example.fitnfocus.ui.profile.ProfileScreen
import com.example.fitnfocus.viewmodel.StudyViewModel


@Composable
fun FitNFocusNavHost(
    navController: NavHostController,
    startDestination: String,
    studyViewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.Onboarding.route) {
            OnboardingRoute(
                onOnboardingComplete = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Home.route) {
            HomeScreen(
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.Profile.route)
                },
                onStartSession = { sessionId ->
                    navController.navigate(NavRoutes.sessionTimer(sessionId))
                }
            )
        }

        composable(NavRoutes.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = NavRoutes.SESSION_TIMER,
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: return@composable
            SessionTimerRoute(
                sessionId = sessionId,
                onSessionCompleted = {
                    navController.navigate(NavRoutes.Focus.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onSessionStopped = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavRoutes.Focus.route) {
            FocusRoute(
                onNavigateToCollection = { navController.navigate(NavRoutes.Collection.route) }
            )
        }

        composable(NavRoutes.Collection.route) {
            CollectionScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Goals.route) {
            StudyRoute(
                viewModel = studyViewModel,
                onSessionStopped = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onSessionCompleted = {
                    navController.navigate(NavRoutes.Focus.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}