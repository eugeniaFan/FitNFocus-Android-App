package com.example.fitnfocus.ui.goals.study.sessions.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
// TODO AUFRAUMEN
/**
 * DateFormatter für das deutsche Eingabeformat (dd.MM.yyyy).
 * Wird für Prüfungsdaten verwendet.
 */
val GermanDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN)

/**
 * Parst einen deutschen Datumsstring (dd.MM.yyyy) zu LocalDate.
 */
fun parseGermanDate(dateString: String?): LocalDate? {
    if (dateString.isNullOrBlank()) return null

    return runCatching {
        LocalDate.parse(dateString.trim(), GermanDateFormatter)
    }.getOrNull()
}

/**
 * Formatiert ein LocalDate zum deutschen Format (dd.MM.yyyy).
 */
fun formatGermanDate(date: LocalDate?): String {
    if (date == null) return ""

    return date.format(GermanDateFormatter)
}

/**
 * Formatiert das Datum für die Anzeige als Header.
 */
fun formatDateHeader(date: LocalDate): String {
    return try {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        when (date) {
            today -> "Heute"
            yesterday -> "Gestern"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy", Locale.GERMAN)
                date.format(formatter)
            }
        }
    } catch (e: Exception) {
        date.toString()
    }
}