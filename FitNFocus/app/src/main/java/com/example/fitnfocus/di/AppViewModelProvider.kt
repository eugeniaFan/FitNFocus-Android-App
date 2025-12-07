package com.example.fitnfocus.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnfocus.viewmodel.StudyViewModel
import com.example.fitnfocus.viewmodel.HomeViewModel
import com.example.fitnfocus.viewmodel.ActivityViewModel
import com.example.fitnfocus.viewmodel.HistoryViewModel


object AppViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            ActivityViewModel(app.container.activityRepository)
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            HomeViewModel(app.container.activityRepository)
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            StudyViewModel(app.container.studyRepository)
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            HistoryViewModel(app.container.activityRepository, app.container.studyRepository)
        }

    }
}