package com.example.fitnfocus.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context


@Database(
    entities = [
        DailyActivityEntity::class,
        StudySessionEntity::class,
        LearningGoalEntity::class,
        TopicProgressEntity::class
    ],
    version = 7,  // Erhöht: date/examDate/completedAt → epochDay (Long statt String)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
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
                    .fallbackToDestructiveMigration(dropAllTables = true) // Für Entwicklungsphase: löscht DB bei Schema-Änderung
                    .build()
                INSTANCE = instance
                instance
            }

            }
        }


}