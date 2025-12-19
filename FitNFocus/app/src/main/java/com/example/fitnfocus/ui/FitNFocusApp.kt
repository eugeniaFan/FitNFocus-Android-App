package com.example.fitnfocus.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnfocus.ui.navigation.FitNFocusNavHost
import com.example.fitnfocus.ui.navigation.NavRoutes


@Composable
fun FitNFocusApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
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

                // Activity - Anzeige
                NavigationBarItem(
                    selected = currentRoute == NavRoutes.Activity.route,
                    onClick = {
                        navController.navigate(NavRoutes.Activity.route) {
                            // verhindert bei mehrfachen klicken denselben Screen
                            // auf den Stack zu packen:
                            popUpTo(NavRoutes.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = "Activity") },
                    label = { Text("Activity") }
                )

                // Study - Anzeige
                NavigationBarItem(
                    selected = currentRoute == NavRoutes.Study.route,
                    onClick = {
                        navController.navigate(NavRoutes.Study.route) {
                            popUpTo(NavRoutes.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.School, contentDescription = "Focus") },
                    label = { Text("Focus") }
                )
            }
        }
    ) { innerPadding ->
        FitNFocusNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}