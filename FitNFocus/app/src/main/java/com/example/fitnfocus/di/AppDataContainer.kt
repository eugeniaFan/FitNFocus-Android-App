package com.example.fitnfocus.di

import android.content.Context
import com.example.fitnfocus.calendar.CalendarExporter
import com.example.fitnfocus.calendar.IntentCalendarExporter
import com.example.fitnfocus.data.local.AppDatabase
import com.example.fitnfocus.data.repository.ActivityRepository
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.TopicProgressRepository
import com.example.fitnfocus.data.repository.UserPreferencesRepository


class AppDataContainer(private val context: Context) : AppContainer {

    private val database by lazy { AppDatabase.getInstance(context) }

    override val activityRepository: ActivityRepository by lazy {
        ActivityRepository(database.activityDao())
    }
    override val sessionRepository: SessionRepository by lazy {
        SessionRepository(database.sessionDao())
    }
    override val topicProgressRepository: TopicProgressRepository by lazy {
        TopicProgressRepository(database.topicProgressDao())
    }
    override val calendarExporter: CalendarExporter by lazy {
        IntentCalendarExporter()
    }
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
    override val learningGoalRepository: LearningGoalRepository by lazy {
        LearningGoalRepository(database.learningGoalDao())
    }
}