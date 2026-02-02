package com.example.fitnfocus

import com.example.fitnfocus.data.local.StudySessionEntity
import com.example.fitnfocus.data.mapper.SessionMapper
import com.example.fitnfocus.domain.SessionStatus
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDate

class SessionMapperTest {
    @Test
    fun entityToDomain_mapsAllFieldsCorrectly() {

        val entity = StudySessionEntity(
            id = 1,
            topic = "Mathe",
            durationMinutes = 45,
            epochDay = 10,
            goalId = 2,
            status = "COMPLETED",
            notes = "Wichtig",
            elapsedSeconds = 2700
        )

        val domain = SessionMapper.entityToDomain(entity)

        assertEquals(1, domain.id)
        assertEquals("Mathe", domain.topic)
        assertEquals(45, domain.durationMinutes)
        assertEquals(LocalDate.ofEpochDay(10), domain.date)
        assertEquals(2, domain.goalId)
        assertEquals(SessionStatus.COMPLETED, domain.status)
        assertEquals("Wichtig", domain.notes)
        assertEquals(2700, domain.elapsedSeconds)
    }
}