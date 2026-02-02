package com.example.fitnfocus.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Onboarding : NavRoutes("onboarding")
    data object Home : NavRoutes("home")
    data object Focus : NavRoutes("focus")
    data object Collection : NavRoutes("collection")
    data object Goals : NavRoutes("goals")
    data object Profile : NavRoutes("profile")

    companion object {
        const val SESSION_TIMER = "session_timer/{sessionId}"
        fun sessionTimer(sessionId: Int): String = "session_timer/$sessionId"
    }
}