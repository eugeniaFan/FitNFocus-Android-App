package com.example.fitnfocus.di

import android.app.Application

class FitNFocusApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }

}