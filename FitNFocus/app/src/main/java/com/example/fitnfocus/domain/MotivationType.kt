package com.example.fitnfocus.domain

/**
 * Motivations-Archetypen basierend auf der Forschung zu Behavior Change Apps.
 * Jeder Typ spricht unterschiedliche Motivationsmechanismen an.
 */
enum class MotivationType(val displayName: String, val description: String) {
    STRUCTURED_PROGRESS("Fortschrittsübersicht", "Du liebst es, deinen Fortschritt zu sehen und Ziele abzuhaken"),
    SELF_IMPROVEMENT("Tipps & Wissen", "Du möchtest dich selbst verbessern und dazulernen"),
    ADVENTURE("Spielerisch", "Du magst spielerische Herausforderungen und Belohnungen"),
    EMOTIONAL_WELLNESS("Positive Bestärkung", "Du brauchst aufmunternde Worte und emotionale Unterstützung"),
    SOCIAL("Lerngruppe", "Du arbeitest am besten mit anderen zusammen")
}

