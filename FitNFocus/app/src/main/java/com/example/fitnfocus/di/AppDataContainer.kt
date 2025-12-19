package com.example.fitnfocus.di

import android.content.Context
import com.example.fitnfocus.calendar.CalendarExporter
import com.example.fitnfocus.calendar.IntentCalendarExporter
import com.example.fitnfocus.data.local.AppDatabase
import com.example.fitnfocus.data.repository.ActivityRepository
import com.example.fitnfocus.data.repository.StudyRepository


class AppDataContainer(private val context: Context) : AppContainer {
    override val activityRepository: ActivityRepository by lazy {
        val database = AppDatabase.getInstance(context)
        ActivityRepository(database.activityDao())
    }
    override val studyRepository: StudyRepository by lazy {
        val database = AppDatabase.getInstance(context)
        StudyRepository(database.studyDao())
    }
    override val calendarExporter: CalendarExporter by lazy {
        IntentCalendarExporter()
    }

}