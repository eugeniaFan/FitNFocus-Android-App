package com.example.fitnfocus.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitnfocus.viewmodel.FocusViewModel
import com.example.fitnfocus.viewmodel.HomeViewModel
import com.example.fitnfocus.viewmodel.MainViewModel
import com.example.fitnfocus.viewmodel.OnboardingViewModel
import com.example.fitnfocus.viewmodel.ProfileViewModel
import com.example.fitnfocus.viewmodel.SessionTimerViewModel
import com.example.fitnfocus.viewmodel.StudyViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            StudyViewModel(
                app.container.sessionRepository,
                app.container.learningGoalRepository,
                app.container.topicProgressRepository,
                app.container.setTopicCompletionUseCase
            )
        }

        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            HomeViewModel(
                app.container.sessionRepository,
                app.container.userPreferencesRepository,
                app.container.learningGoalRepository,
                app.container.topicProgressRepository
            )
        }

        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            FocusViewModel(
                app.container.sessionRepository,
                app.container.learningGoalRepository
            )
        }

        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            SessionTimerViewModel(
                app.container.sessionRepository,
                app.container.learningGoalRepository,
                app.container.setTopicCompletionUseCase
            )
        }

        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            OnboardingViewModel(
                app.container.userPreferencesRepository,
                app.container.learningGoalRepository
            )
        }

        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            ProfileViewModel(
                app.container.userPreferencesRepository,
                app.container.learningGoalRepository,
                app.container.sessionRepository
            )
        }

        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FitNFocusApplication
            MainViewModel(app.container.userPreferencesRepository)
        }
    }
}
