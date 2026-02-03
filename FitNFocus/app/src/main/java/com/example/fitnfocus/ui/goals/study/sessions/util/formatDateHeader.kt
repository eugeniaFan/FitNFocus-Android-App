package com.example.fitnfocus.ui.goals.study.sessions.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 *
 * DateFormatter for the German input format.(dd.MM.yyyy).
 */
val GermanDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN)


fun parseGermanDate(dateString: String?): LocalDate? {
    if (dateString.isNullOrBlank()) return null

    return runCatching {
        LocalDate.parse(dateString.trim(), GermanDateFormatter)
    }.getOrNull()
}

/**
 * Formats a LocalDate to German format
 */
fun formatGermanDate(date: LocalDate?): String {
    if (date == null) return ""

    return date.format(GermanDateFormatter)
}

/**
 * Formats the date for display as a header
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