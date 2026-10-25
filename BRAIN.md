# BRAIN – Gemeinsames Projektwissen

Das „Gehirn“ des Projekts: Entscheidungen, Fakten, Fallstricke. Wird von Menschen und allen Agenten gepflegt (Format: `Datum · Thema · Regel · Warum`). Agentenspezifisches Wissen: `.claude/agent-memory/<agent>/`, agentenübergreifendes: `.claude/agent-memory/SHARED.md`. Aufgaben: `TODO.md`.

## Entscheidungen
- 2026-10-25 · Plattform · Kotlin + Jetpack Compose, Material 3, compileSdk/targetSdk 36, minSdk 26 · Nutzervorgabe SDK 36.
- 2026-10-25 · Sprachen · Englisch (Standard) + Deutsch; Texte ausschließlich in `strings.xml`; Lint erzwingt es · Nutzervorgabe.
- 2026-10-25 · Package · `app.kindwords` (vorläufig; nach Play-Veröffentlichung unveränderbar) · Platzhalter, vor Release bestätigen.
- 2026-10-25 · Hosting · Rechtstexte/Infoseiten in `docs/` via GitHub Pages · Nutzervorgabe: im Repo erreichbar.

## Technik-Fakten
- Gradle 9.8.2, AGP 9.4.1 (eingebautes Kotlin), Kotlin 2.4.21, Compose BOM 2025.12.01 (Compose 1.10; neuere BOMs/lifecycle 2.11/core-ktx 1.19 verlangen compileSdk 37), JDK 17, SDK in `D:\Android\Sdk` (Plattformen 35+36).
- `generateLocaleConfig` braucht `app/src/main/res/resources.properties` (`unqualifiedResLocale=en`).

- Vorlage für Datenschutz/Impressum: Seite der RPCT-App (`D:/AI_KI/rpct-app/docs/privacy-policy.html`, Repo Ilitrius/rpct-privacy); Verantwortlicher: René Puf, Neuburg an der Donau.

## Fallstricke
- Dynamic Color nur ab API 31 (`Build.VERSION_CODES.S`), minSdk ist 26 – sonst Lint-Fehler.
- Große Bash-Heredocs scheitern unter Windows → Write-Tool; Gradle über `.\gradlew.bat` in PowerShell.

## Offene Fragen
- Zweck/Funktionen der App (Briefing folgt vom Nutzer → `docs/PRODUCT.md`).
- Anbieterdaten: aus RPCT übernommen (René Puf) – bestätigen, ob für diese App identisch.
