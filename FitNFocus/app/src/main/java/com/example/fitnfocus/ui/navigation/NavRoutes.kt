package com.example.fitnfocus.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Activity : NavRoutes("activity")
    data object Study : NavRoutes("study")


}