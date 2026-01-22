package com.example.fitnfocus.ui.goals.study.sessions.util

import android.annotation.SuppressLint
import android.os.Build
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * DateFormatter für das deutsche Eingabeformat (dd.MM.yyyy).
 * Wird für Prüfungsdaten verwendet.
 * Null auf API < 26.
 */
@SuppressLint("NewApi")
val GermanDateFormatter: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    DateTimeFormatter.ofPattern("dd.MM.yyyy")
} else {
    null
}

/**
 * Parst einen deutschen Datumsstring (dd.MM.yyyy) zu LocalDate.
 * Gibt null zurück bei Fehlern oder auf API < 26.
 */
@SuppressLint("NewApi")
fun parseGermanDate(dateString: String?): LocalDate? {
    if (dateString.isNullOrBlank()) return null
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null

    return runCatching {
        LocalDate.parse(dateString.trim(), GermanDateFormatter)
    }.getOrNull()
}

/**
 * Formatiert ein LocalDate zum deutschen Format (dd.MM.yyyy).
 * Gibt leeren String zurück bei null oder auf API < 26.
 */
@SuppressLint("NewApi")
fun formatGermanDate(date: LocalDate?): String {
    if (date == null) return ""
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return ""

    return date.format(GermanDateFormatter)
}

/**
 * Formatiert das Datum für die Anzeige als Header.
 */
@SuppressLint("NewApi")
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