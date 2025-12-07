package com.example.fitnfocus.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_session ORDER BY date DESC")
    fun getAllStudySessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_session WHERE id = :id")
    suspend fun getStudySessionsById(id: Int): StudySessionEntity?

    @Query("SELECT * FROM study_session WHERE date = :date")
    suspend fun getStudySessionsByDate(date: String): List<StudySessionEntity>

    @Query("SELECT * FROM study_session WHERE subject = :subject")
    suspend fun getStudySessionsBySubject(subject: String): List<StudySessionEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(studySession: StudySessionEntity)

    @Update
    suspend fun updateStudySession(studySession: StudySessionEntity)

    @Delete
    suspend fun deleteStudySession(studySession: StudySessionEntity)

}