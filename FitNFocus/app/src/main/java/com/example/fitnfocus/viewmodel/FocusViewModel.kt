package com.example.fitnfocus.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.LearningGoalRepository
import com.example.fitnfocus.data.repository.SessionRepository
import com.example.fitnfocus.data.repository.TopicProgressRepository
import com.example.fitnfocus.domain.SessionStatus
import com.example.fitnfocus.domain.StudySession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel für den Focus-Screen.
 * Verwaltet geplante Sessions, Timer und Sammelfiguren.
 */
class FocusViewModel(
    private val sessionRepository: SessionRepository,
    private val learningGoalRepository: LearningGoalRepository,
    private val topicProgressRepository: TopicProgressRepository
) : ViewModel() {

    private val dateFormatter: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("dd.MM.yyyy")
    } else {
        null
    }

    // Geplante Sessions für heute
    private val _plannedSessions = MutableStateFlow<List<StudySession>>(emptyList())
    val plannedSessions = _plannedSessions.asStateFlow()

    // Anzahl der abgeschlossenen Sessions (Münzen)
    private val _completedSessionsCount = MutableStateFlow(0)
    val completedSessionsCount = _completedSessionsCount.asStateFlow()

    // Aktive Timer-Session
    private val _activeTimerSession = MutableStateFlow<StudySession?>(null)
    val activeTimerSession = _activeTimerSession.asStateFlow()

    // Cache für Modul-Namen
    private val moduleNameCache = mutableMapOf<Int, String>()

    /**
     * Lädt alle Sessions für den Focus-Bereich.
     */
    fun loadTodaySessions() {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return@launch

            val today = LocalDate.now()

            // Alle geplanten Sessions (unabhängig vom Datum)
            val allPlannedSessions = sessionRepository.getAllPlannedSessions()
            _plannedSessions.value = allPlannedSessions

            // Abgeschlossene Sessions nur von heute zählen (für Münzen-Anzeige)
            val todaySessions = sessionRepository.getSessionsByDate(today)
            _completedSessionsCount.value = todaySessions.count {
                it.status == SessionStatus.COMPLETED
            }

            // Lade Modul-Namen für alle Sessions
            allPlannedSessions.forEach { session ->
                session.goalId?.let { goalId ->
                    if (!moduleNameCache.containsKey(goalId)) {
                        val goal = learningGoalRepository.getGoalById(goalId)
                        goal?.let { moduleNameCache[goalId] = it.moduleName }
                    }
                }
            }
        }
    }

    /**
     * Gibt den Modul-Namen für eine Session zurück.
     */
    fun getModuleNameForSession(session: StudySession): String {
        return session.goalId?.let { moduleNameCache[it] } ?: "Lernen"
    }

    /**
     * Startet den Timer für eine Session.
     */
    fun startTimer(session: StudySession) {
        _activeTimerSession.value = session
    }

    /**
     * Bricht den Timer ab und kehrt zur Übersicht zurück.
     */
    fun cancelTimer() {
        _activeTimerSession.value = null
    }

    /**
     * Schließt eine Session ab.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun completeSession(sessionId: Int, goalId: Int, topic: String, markTopicCompleted: Boolean, notes: String = "") {
        viewModelScope.launch {
            try {
                // Session-Status auf COMPLETED setzen
                sessionRepository.updateSessionStatus(sessionId, SessionStatus.COMPLETED)

                // Notizen speichern wenn vorhanden
                if (notes.isNotBlank()) {
                    sessionRepository.updateSessionNotes(sessionId, notes)
                }

                // Optional: Topic als abgeschlossen markieren (nur wenn goalId gültig)
                if (markTopicCompleted && goalId > 0) {
                    // Prüfen ob Topic bereits abgeschlossen ist
                    val alreadyCompleted = topicProgressRepository.isTopicCompleted(goalId, topic)

                    if (!alreadyCompleted) {
                        topicProgressRepository.markTopicCompleted(goalId, topic, true)
                    }

                    // Alle offenen Sessions für dieses Topic abschließen
                    sessionRepository.completeAllSessionsForTopic(topic)
                }

                // Timer zurücksetzen
                _activeTimerSession.value = null

                // Sessions neuladen
                loadTodaySessions()
            } catch (e: Exception) {
                // Fehler abfangen um App-Absturz zu verhindern
                _activeTimerSession.value = null
                loadTodaySessions()
            }
        }
    }

    /**
     * Startet Timer für eine bestimmte Session-ID (vom Dashboard aus).
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun startTimerForSessionId(sessionId: Int) {
        viewModelScope.launch {
            val session = sessionRepository.getSessionById(sessionId)
            session?.let {
                _activeTimerSession.value = it
            }
        }
    }
}

