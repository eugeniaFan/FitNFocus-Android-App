# Study Feature - Ziel-Struktur

## Übersicht

Diese Dokumentation beschreibt die empfohlene Paket- und Dateistruktur für das Study-Feature unter Berücksichtigung des Projektstandards (ViewModels zentral unter `viewmodel/`).

**Status: ✅ Neue Struktur unter `ui.study/` erstellt**

> Die alten Dateien unter `ui.goals/` bleiben vorerst bestehen, bis die Migration abgeschlossen ist.

---

## Ziel-Struktur (ERSTELLT ✅)

```
com.example.fitnfocus/
├── ui/
│   └── study/                              # Feature-Package (NEU)
│       ├── StudyUiState.kt                 # ✅ Zentrale UI States (LearningNavigationState, TopicStatus, TopicItem, etc.)
│       ├── StudyUiEvent.kt                 # ✅ UI Events für ViewModel-Communication
│       │
│       ├── goals/                          # ✅ Lernziele Sub-Feature
│       │   ├── GoalsOverviewScreen.kt      # ✅ Liste aller Lernziele (mit Swipe-to-Delete)
│       │   ├── GoalDetailScreen.kt         # ✅ Detail-Ansicht eines Ziels mit Topics
│       │   └── components/                 # ✅ Wiederverwendbare Goal-UI
│       │       ├── FitNFocusColors.kt      # ✅ Design System Farben
│       │       └── FocusAreaSelector.kt    # ✅ FocusArea Auswahl
│       │
│       ├── sessions/                       # ✅ Sessions Sub-Feature
│       │   ├── components/                 # ✅ Session-spezifische UI
│       │   │   └── SessionCard.kt          # ✅ Session-Karte
│       │   └── dialogs/                    # ✅ Session Dialoge
│       │       ├── AddSessionDialog.kt     # ✅ Neue Session hinzufügen
│       │       ├── EditSessionDialog.kt    # ✅ Session bearbeiten
│       │       └── ConfirmDeleteDialog.kt  # ✅ Lösch-Bestätigung
│       │
│       ├── timer/                          # ✅ Timer Sub-Feature
│       │   ├── SessionTimerUiState.kt      # ✅ Timer-spezifischer UI State
│       │   └── TimerState.kt               # ✅ Timer-Zustände (enum)
│       │
│       └── dialogs/                        # (Placeholder für allgemeine Dialoge)
│
│   └── goals/                              # ALTE Struktur (zur Migration bereit)
│       ├── StudyRoute.kt                   # → Später nach ui/study verschieben
│       ├── StudyScreen.kt                  # → Später nach ui/study verschieben
│       ├── StudyDialogs.kt                 # → Aufgeteilt in sessions/dialogs/
│       └── ...
│
└── viewmodel/                              # Zentrale ViewModels (Projektstandard ✅)
    ├── StudyViewModel.kt                   # ✅ Haupt-ViewModel für Study-Feature
    ├── StudyUiEvent.kt                     # → Sollte nach ui/study verschoben werden
    └── (SessionTimerViewModel.kt)          # → Sollte aus ui/goals/timer hierher
```

---

## Begründung

### 1. UI-Dateien bleiben im Feature `ui.study/`

| Aspekt | Begründung |
|--------|------------|
| **Feature-Isolation** | Alle Study-bezogenen Screens, Components und Dialoge sind logisch gruppiert |
| **Namensänderung goals → study** | "study" ist aussagekräftiger für das Gesamtfeature (Lernziele + Sessions + Timer) |
| **Klare Sub-Feature Trennung** | `goals/`, `sessions/`, `timer/` ermöglichen gezielte Navigation im Code |
| **Wiederverwendbarkeit** | `components/` Ordner pro Sub-Feature für kleine, wiederverwendbare UI-Elemente |
| **Testbarkeit** | Screens und Components können isoliert in Previews getestet werden |

### 2. ViewModels bleiben zentral unter `viewmodel/`

| Aspekt | Begründung |
|--------|------------|
| **Projektstandard** | Konsistenz mit bestehenden ViewModels (HomeViewModel, FocusViewModel, etc.) |
| **Zentrale Übersicht** | Alle Business-Logik-Container auf einen Blick sichtbar |
| **Feature-Zuordnung durch Namen** | `StudyViewModel`, `SessionTimerViewModel` sind eindeutig zuordenbar |
| **DI-Freundlich** | Hilt/Koin Module finden ViewModels an bekannter Stelle |

### 3. Feature-Zuordnung bleibt klar

| Strategie | Umsetzung |
|-----------|-----------|
| **Package-Namen** | `ui.study.*` macht Feature-Zugehörigkeit sofort ersichtlich |
| **Klassennamen-Präfixe** | `StudyViewModel`, `GoalDetailScreen`, `SessionCard` |
| **State-Platzierung** | UI States in `ui.study/` da von Composables konsumiert |

---

## Empfohlene Migration (Minimal-Invasiv)

### Schritt 1: Package umbenennen
- `ui.goals` → `ui.study` (IDE Refactor > Rename)

### Schritt 2: Sub-Packages erstellen
```
ui.study/goals/       # Bestehende Goal-Components verschieben
ui.study/sessions/    # Neue Struktur für Sessions
ui.study/timer/       # Bereits vorhanden, bleibt
```

### Schritt 3: Dateien neu organisieren
- `LearningGoalsOverview.kt` → `goals/GoalsOverviewScreen.kt`
- `LearningGoalDetail.kt` → `goals/GoalDetailScreen.kt`
- `StudyComponents.kt` (SessionCard) → `sessions/components/SessionCard.kt`
- `StudyDialogs.kt` → Aufteilen in `dialogs/` Ordner

### Schritt 4: ViewModels
- `SessionTimerViewModel.kt` aus `ui.goals.timer/` → `viewmodel/`
- `StudyViewModel.kt` bleibt in `viewmodel/`

---

## Aktuelle vs. Ziel-Struktur (Diff)

### AKTUELL:
```
ui/goals/
├── StudyComponents.kt          → sessions/components/SessionCard.kt
├── StudyDialogs.kt             → dialogs/ (aufteilen)
├── StudyRoute.kt               → study/StudyRoute.kt
├── StudyScreen.kt              → study/StudyScreen.kt
├── components/
│   ├── AddLearningGoalBottomSheet.kt   → dialogs/
│   ├── EditLearningGoalBottomSheet.kt  → dialogs/
│   ├── FocusAreaSelector.kt            → goals/components/
│   ├── LearningGoalDetail.kt           → goals/GoalDetailScreen.kt
│   └── LearningGoalsOverview.kt        → goals/GoalsOverviewScreen.kt
└── timer/
    ├── SessionTimerScreen.kt           → timer/ (bleibt)
    ├── SessionTimerUiState.kt          → timer/ (bleibt)
    ├── SessionTimerViewModel.kt        → viewmodel/ (verschieben!)
    └── TimerState.kt                   → timer/ (bleibt)

viewmodel/
├── StudyViewModel.kt           (bleibt)
└── StudyUiEvent.kt             → ui.study/StudyUiEvent.kt (UI-nah)
```

---

## State-Strategie

### Option A: Ein zentraler State (Empfohlen für kleine Features)
```kotlin
// ui/study/StudyUiState.kt
data class StudyUiState(
    val goals: List<LearningGoalUi> = emptyList(),
    val selectedGoal: LearningGoalUi? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### Option B: Getrennte States (Für komplexere Features)
```kotlin
// ui/study/StudyUiState.kt
data class GoalsUiState(...)
data class SessionsUiState(...)

// ui/study/timer/SessionTimerUiState.kt (bereits vorhanden)
data class SessionTimerUiState(...)
```

---

## Fazit

Diese Struktur bietet:
- ✅ **Konsistenz** mit bestehendem Projektstandard (ViewModels zentral)
- ✅ **Skalierbarkeit** durch klare Sub-Feature-Trennung
- ✅ **Lesbarkeit** durch sprechende Package- und Klassennamen
- ✅ **Wartbarkeit** durch isolierte Components und Dialoge
- ✅ **Minimal-invasive Migration** durch schrittweise Umstrukturierung

