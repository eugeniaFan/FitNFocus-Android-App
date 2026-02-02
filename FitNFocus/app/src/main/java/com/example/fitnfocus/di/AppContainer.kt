package com.example.fitnfocus.di

import com.example.fitnfocus.calendar.CalendarExporter
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.TopicProgressRepository
import com.example.fitnfocus.data.repository.UserPreferencesRepository
import com.example.fitnfocus.domain.usecase.SetTopicCompletionUseCase

/**
 * Dependency injection contract for the application.
 * Defines all injectable dependencies available throughout the app.
 */
interface AppContainer {
    val sessionRepository: SessionRepository
    val topicProgressRepository: TopicProgressRepository
    val calendarExporter: CalendarExporter
    val userPreferencesRepository: UserPreferencesRepository
    val learningGoalRepository: LearningGoalRepository
    val setTopicCompletionUseCase: SetTopicCompletionUseCase
}
