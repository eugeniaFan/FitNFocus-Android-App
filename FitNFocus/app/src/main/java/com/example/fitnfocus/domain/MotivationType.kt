package com.example.fitnfocus.domain

/**
 * Motivation archetypes based on behavior change app research.
 * Each type responds to different motivational mechanisms.
 */
enum class MotivationType(val displayName: String, val description: String) {
    STRUCTURED_PROGRESS(
        displayName = "Fortschrittsübersicht",
        description = "Du liebst es, deinen Fortschritt zu sehen und Ziele abzuhaken"
    ),
    SELF_IMPROVEMENT(
        displayName = "Tipps & Wissen",
        description = "Du möchtest dich selbst verbessern und dazulernen"
    ),
    ADVENTURE(
        displayName = "Spielerisch",
        description = "Du magst spielerische Herausforderungen und Belohnungen"
    ),
    EMOTIONAL_WELLNESS(
        displayName =  "Positive Bestärkung",
        description = "Du brauchst aufmunternde Worte und emotionale Unterstützung"
    ),
    SOCIAL(
        displayName = "Lerngruppe",
        description = "Du arbeitest am besten mit anderen zusammen"
    )
}
