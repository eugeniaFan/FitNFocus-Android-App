package com.example.fitnfocus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnfocus.data.repository.StudyRepository
import com.example.fitnfocus.domain.StudySession
import com.example.fitnfocus.domain.toCalendarEventData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyViewModel(
    private val repository: StudyRepository
) : ViewModel() {

    // UI Inputs
    private val _subject = MutableStateFlow("")
    val subject = _subject.asStateFlow()

    private val _duration = MutableStateFlow("")
    val duration = _duration.asStateFlow()


    // UI States
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog = _showAddDialog.asStateFlow()


    private val _todaySessions = MutableStateFlow<List<StudySession>>(emptyList())
    val todaySessions = _todaySessions.asStateFlow()


    private val _selectedSession = MutableStateFlow<StudySession?>(null)
    val selectedSession = _selectedSession.asStateFlow()


    //Nach Speichern-> "in Kalender eintragen"-Button anzeigen
    private val _lastSavedSession = MutableStateFlow<StudySession?>(null)
    val lastSavedSession = _lastSavedSession.asStateFlow()


    // One-off Events (Dialog schließen, Snackbar, Kalender öffnen)
    private val _uiEvents = MutableSharedFlow<StudyUiEvent>(extraBufferCapacity = 8)
    val uiEvents = _uiEvents.asSharedFlow()

    fun setShowAddDialog(value: Boolean) {
        _showAddDialog.value = value
    }

    fun selectSession(session: StudySession?) {
        _selectedSession.value = session
    }

    fun onSubjectChange(newValue: String) {
        _subject.value = newValue
    }

    fun onDurationChange(newValue: String) {
        _duration.value = newValue
    }

    fun loadSessionsForDate(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val sessions = repository.getStudySessionsByDate(date)
                _todaySessions.value = sessions
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Error loading sessions."))
            } finally {
                _isLoading.value = false
            }

        }
    }


    fun updateSession(session: StudySession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateStudySession(session)
                _todaySessions.value = repository.getStudySessionsByDate(session.date)
                _selectedSession.value = null
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Updated."))
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Update failed."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSession(session: StudySession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteStudySession(session)
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Gelöscht."))
                _todaySessions.value = repository.getStudySessionsByDate(session.date)
                _selectedSession.value = null
            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Löschen fehlgeschlagen."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Session Speichern + optional Kalender öffnen
    fun saveStudySession(date: String, addToCalendar: Boolean = false) {
        val cleanedSubject = _subject.value.trim()
        val minutes = _duration.value.toIntOrNull()

        if (cleanedSubject.isEmpty()) {
            _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Bitte ein Thema eingeben."))
            return
        }

        if (minutes == null || minutes <= 0) {
            _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Please enter a valid duration."))
            return
        }

        val session = StudySession(
            id = 0,
            subject = cleanedSubject,
            durationMinutes = minutes,
            date = date
        )

        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.insertStudySession(session)

                // State
                _duration.value = ""
                _subject.value = ""
                _lastSavedSession.value = session

                // Session-List neuladen
                loadSessionsForDate(date)

                // UI Events: Dialog schließen + Feedback
                _uiEvents.tryEmit(StudyUiEvent.CloseAddDialog)
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Session gespeichert."))

                if(addToCalendar) {
                    val eventData = session.toCalendarEventData()
                    _uiEvents.emit(StudyUiEvent.OpenCalendarInsert(eventData))
                }

            } catch (e: Exception) {
                _uiEvents.tryEmit(StudyUiEvent.ShowMessage("Speichern fehlgeschlagen. Bitte versuchen Sie es erneut."))

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestAddLastSavedToCalendar() {
        val session = _lastSavedSession.value ?: run {
            _uiEvents.tryEmit(StudyUiEvent.ShowMessage("No session to add to calendar."))

            return
        }
        val eventData = session.toCalendarEventData()
        _uiEvents.tryEmit(StudyUiEvent.OpenCalendarInsert(eventData))
    }
}