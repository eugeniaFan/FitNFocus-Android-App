package com.example.fitnfocus.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Onboarding : NavRoutes("onboarding")
    data object Home : NavRoutes("home")
    data object Focus : NavRoutes("focus")      // Timer & Sammelfiguren
    data object Collection : NavRoutes("collection")
    data object Goals : NavRoutes("goals")      // Lernziele
    data object Profile : NavRoutes("profile")

    companion object {
        // Route mit Session-ID Parameter für direkten Timer-Start
        const val FOCUS_WITH_SESSION = "focus?sessionId={sessionId}"
        fun focusWithSession(sessionId: Int): String = "focus?sessionId=$sessionId"
    }
}