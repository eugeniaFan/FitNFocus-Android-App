package com.example.fitnfocus.ui.focus

data class FocusTypeUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val iconRes: Int? = null // später Bilder
)

object FocusTypes {
    fun staticTypes(): List<FocusTypeUi> = listOf(
        FocusTypeUi(
            id = "analyst",
            title = "Analyst",
            subtitle = "Lernen, Mathe, Logik",
            description = "Wächst, je mehr du strukturierte Lern-Sessions machst (z. B. Mathe, Aufgaben, Wiederholung)."
        ),
        FocusTypeUi(
            id = "thinker",
            title = "Thinker",
            subtitle = "Lesen, Theorie",
            description = "Passt zu ruhigen Deep-Work-Sessions: Lesen, Zusammenfassen, Verstehen statt Abarbeiten."
        ),
        FocusTypeUi(
            id = "athlete",
            title = "Athlete",
            subtitle = "Fitness, Bewegung",
            description = "Steht für Fokus durch Aktivität: kurze, intensive Sessions oder Bewegung als Energie-Boost."
        ),
        FocusTypeUi(
            id = "balancer",
            title = "Balancer",
            subtitle = "Kurz & ruhig",
            description = "Für kurze Sessions, Reset, Stress runter: kleine Einheiten, die Konstanz aufbauen."
        )
    )
}