package com.example.fitnfocus.di

import com.example.fitnfocus.calendar.CalendarExporter
import com.example.fitnfocus.data.repository.ActivityRepository
import com.example.fitnfocus.data.repository.StudyRepository

interface AppContainer {
    val activityRepository: ActivityRepository
    val studyRepository: StudyRepository
    val calendarExporter: CalendarExporter
}