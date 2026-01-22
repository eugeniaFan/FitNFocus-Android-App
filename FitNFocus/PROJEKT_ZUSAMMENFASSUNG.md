# FitNFocus – Projektzusammenfassung

## 1) Kurzbeschreibung & Ziel
**FitNFocus** ist eine Android-App zur **Lernorganisation** (Lernsessions, Themen, Ziele, Verlauf) mit motivierenden Elementen und Fokus auf **Produktivität** und **Routine**. Der Kernnutzen für Nutzer:innen ist:
- schneller Einstieg durch **Onboarding** (Personalisierung)
- bessere Lernstruktur durch **geplante Sessions**, Fortschritt und Historie
- klare, moderne UI mit visuellem Feedback (u.a. Animationen)

## 2) Funktionsumfang (Features)

### Onboarding (Personalisierung)
- mehrstufiger Einstieg, um zentrale Daten/Präferenzen zu erfassen (z.B. Rolle, Modul, Themen, Prüfungsdatum, Motivation/Präferenzen)
- Ziel: die App direkt mit sinnvollen Defaults starten zu lassen und Inhalte/Ansichten besser auf die Nutzer:innen abzustimmen

### Sessions / Lernen
- Erstellen und Verwalten von **Study Sessions** (inkl. Datum, Dauer, Thema)
- Status-Logik für Sessions (z.B. **PLANNED**, **IN_PROGRESS**, **COMPLETED**)
- Notizen zu Sessions (z.B. „Was hat gut funktioniert?“)
- Tagesbasierte Auswertung (z.B. Gesamtlernzeit pro Datum)

### Themen & Fortschritt
- Themen-orientierte Sicht auf das Lernen
- Fortschrittsdarstellung pro Thema (z.B. ob es Sessions dazu gibt / ob abgeschlossen)
- Möglichkeit, alle offenen Sessions eines Themas gesammelt „abzuschließen“

### Verlauf / Historie
- Übersicht bereits durchgeführter Sessions
- Filter/Sortierung nach Datum und Thema

### Kalender-Verknüpfung
- Export/Anlegen von Terminen im Gerätekalender (z.B. geplante Lernsession als Event)
- Nutzen: Lernplanung wird in den Alltag integriert und ist außerhalb der App sichtbar

### UI-Feedback & Animationen
- animierte Übergänge/Progress-Darstellungen (z.B. sanfte Aktualisierung von Fortschrittswerten)
- Ziel: verständlicher Status, motivierendes Feedback und „polished“ Look & Feel

## 3) Code-Architektur (Übersicht)
Die App ist klar in Schichten organisiert (MVVM-Style und „MVP-nahe“ Trennung der Verantwortlichkeiten):

**UI (Jetpack Compose)**
- Screens/Composables: Darstellung + User-Interaktionen

**ViewModels (State + Logik)**
- halten UI-State (z.B. via Kotlin Coroutines/StateFlow)
- rufen Use-Cases/Repositories auf
- kapseln Business-Regeln (z.B. Statuswechsel, Laden/Speichern)

**Repositories (Datenzugriff / Abstraktion)**
- klare Schnittstelle zwischen ViewModels und Datenquellen
- bündeln Logik, ob Daten aus Room, System-APIs (Kalender) etc. kommen

**Persistenz (Room)**
- Entities + DAOs + AppDatabase
- reaktive Datenströme (Flows) wo sinnvoll

**Plattform-Integration**
- z.B. Kalender-Export über Android-APIs (Intent/CalendarContract)

### Wie ich den Code geordnet habe
- `data/`:
  - `local/`: Room (Entities, DAOs, Database)
  - `repository/` (oder ähnlich): Repositories als Zugriffsschicht
- `ui/`:
  - `screens/`: Compose Screens
  - `components/`: wiederverwendbare UI-Bausteine
  - `theme/`: Farben, Typografie, Material3-Theme
- `viewmodel/` (oder pro Feature-Package): ViewModels je Feature

> Ergebnis: UI bleibt „dumm“, ViewModels steuern den State, Repositories kapseln Datenzugriff – dadurch ist der Code strukturiert, leichter testbar und gut erweiterbar.

## 4) MVP (Minimal Viable Product)
Mein MVP deckt die wichtigsten User-Ziele ab:
1. **Onboarding**: Nutzer:in setzt Modul/Themen/Präferenzen → App ist sofort personalisiert.
2. **Sessions planen & durchführen**: Sessions erstellen, starten/abschließen, Notizen führen.
3. **Fortschritt sehen**: Überblick über erledigte/geplante Sessions + Verlauf.
4. **Kalender-Integration**: geplante Sessions in den Kalender übernehmen.

## 5) Room (Persistenz) – konkret
- Speicherung von Lern-Sessions, Themen/Progress, Zielen und ggf. Aktivitätsdaten in einer lokalen SQLite DB via **Room**.
- Beispiel: `SessionDao` enthält Queries für typische Use-Cases:
  - alle Sessions als `Flow` (reaktiv)
  - Sessions pro Datum / Thema
  - Gesamtlernzeit pro Tag
  - Status-Updates (partial updates für Status, Notizen, elapsed time)

## 6) ViewModels – kurz & praxisnah
- pro Feature ein eigenes ViewModel (z.B. Onboarding, Sessions/Study, History, Activity, Profile)
- Aufgaben:
  - Daten laden/speichern (über Repositories)
  - UI-State verwalten (Loading/Content/Error)
  - UI-Events verarbeiten (z.B. „Session abgeschlossen“, „Notiz gespeichert“, „Export in Kalender“)

## 7) Design: Farben (Orange & Lila)
Die App nutzt eine klare, wiedererkennbare Farbwelt aus **Orange** und **Lila**:
- **Orange**: Energie, Aktivität, „Call-to-Action“ (z.B. wichtige Buttons, Highlights)
- **Lila**: Struktur, Fokus, Ruhe (z.B. Primärfarbe, Akzente, ruhige Flächen)

Die konkreten Farbwerte liegen im Compose-Theme unter `ui/theme/` (z.B. `Color.kt`).

## 8) Onboarding – Nutzen für User (warum das wichtig ist)
Onboarding reduziert die Einstiegshürde und erhöht den Nutzen direkt ab dem ersten Start:
- weniger „leere Screens“ → sofort passende Inhalte
- klare Zielsetzung (Modul/Themen/Datum) → höhere Motivation
- Standardwerte/Personalisierung → weniger manuelle Konfiguration im Alltag

---

**Hinweis:** Wenn du willst, kann ich diese Datei noch um (1) konkrete Screen-Liste, (2) eine kleine Architektur-Grafik (ASCII), und (3) die exakten Orange/Lila Hex-Werte aus `ui/theme/Color.kt` ergänzen.

