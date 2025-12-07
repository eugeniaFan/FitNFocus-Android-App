package com.example.fitnfocus.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.fitnfocus.ui.navigation.FitNFocusNavHost


@Composable
fun FitNFocusApp() {

    val navController = rememberNavController()
    FitNFocusNavHost(navController = navController)
}