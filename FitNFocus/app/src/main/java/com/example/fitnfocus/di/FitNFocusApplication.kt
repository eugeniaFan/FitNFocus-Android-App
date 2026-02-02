package com.example.fitnfocus.di

import android.app.Application

/**
 * Application class for FitNFocus.
 * Initializes dependency injection container at application startup.
 */
class FitNFocusApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
