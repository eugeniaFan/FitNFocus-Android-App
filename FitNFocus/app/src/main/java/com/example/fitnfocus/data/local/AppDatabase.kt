package com.example.fitnfocus.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

/**
 * Room database for the FitNFocus application.
 * Manages study sessions, learning goals, topic progress, and daily activities.
 */
@Database(
    entities = [StudySessionEntity::class, LearningGoalEntity::class, TopicProgressEntity::class],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun learningGoalDao(): LearningGoalDao
    abstract fun topicProgressDao(): TopicProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitnfocus_database"
                )
                    // Development mode: drops database on schema changes
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }

        }
    }
}