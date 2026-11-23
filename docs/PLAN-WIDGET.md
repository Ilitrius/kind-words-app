# Plan: Startbildschirm-Widget "Satz des Tages" (Entwurf, 2026-11-23)

Ziel: mehr Bindung und Sichtbarkeit im Alltag, ohne neue Berechtigung, ohne Datenübertragung (Datenschutz und Data Safety bleiben unverändert).

## Funktion
- Zeigt einen warmen Satz (Inhaltssprache der App, Kategorie nicht gefiltert), Hintergrund im Marken-Theme (Rose/Orange), hell/dunkel.
- Tippen auf den Satz: öffnet die App. Knopf "Teilen": Android-Share-Dialog. Knopf "Neu": neuer Vorschlag (Favoriten-Gewichtung und Lernprofil wie in der App).
- Größen: klein (Satz + Neu) und breit (Satz + Neu + Teilen), anpassbar (responsive).
- "Satz des Tages": einmal pro Kalendertag automatisch wechselnd; der aktuelle Satz wird in DataStore gehalten, damit Neustarts ihn nicht ändern.

## Technik
- Jetpack Glance (`androidx.glance:glance-appwidget` + `glance-material3`), Versionen nur über `gradle/libs.versions.toml`. Alternative: RemoteViews (keine neue Abhängigkeit, mehr Handarbeit).
- `KindWordsWidget` (GlanceAppWidget) + `KindWordsWidgetReceiver` (Manifest, `exported=true`, nur `APPWIDGET_UPDATE`), `res/xml/kindwords_widget_info.xml` mit Vorschaubild und Beschreibung (de/en).
- Daten über `MessageRepository.random(...)` und `SettingsRepository` (kein eigener Datenpfad); Textauswahl mit Test.
- Tageswechsel: `updatePeriodMillis` (inexakt, akkuschonend) plus Aktualisierung beim Öffnen der App. Keine Wakelocks, keine exakten Alarme, kein Polling.
- Alle Texte in `values/` und `values-de/` (Widget-Name, Beschreibung, Knöpfe, Leerzustand "App einmal öffnen").
- Leerzustand: Ist noch kein Inhalt geseedet, zeigt das Widget einen freundlichen Hinweis und öffnet die App.

## Qualität und Prüfung
- Unit-Tests: Tagesauswahl (gleicher Tag = gleicher Satz, Tageswechsel, Sprachwechsel).
- Lint (HardcodedText, MissingTranslation), Release-Build mit R8 (Glance-Regeln prüfen), Emulator API 35 und API 26: Widget hinzufügen, Neu/Teilen/Öffnen, Sprachwechsel, Dunkelmodus, Neustart, App-Update.
- Merged-Manifest prüfen: keine neue Berechtigung.

## Release
- Neue Version 1.1.0 (versionCode erhöhen), Store-Texte de/en um "Widget" ergänzen (Titel <=30, Kurzbeschreibung <=80, Beschreibung <=4000), neue Screenshots mit Widget; Datenschutz unverändert (nur Hinweis, dass das Widget lokal arbeitet).

## Werbung (spätere Entscheidung, nicht Teil dieses Plans)
- Nicht im Widget (Play-Richtlinie, schlechte Erfahrung). Wenn, dann dezent in der App.
- Folgen: AdMob + INTERNET-Berechtigung, Einwilligung (UMP), Datenschutzerklärung, Data Safety, Altersangaben, `app-ads.txt` neu. Test nur mit Test-IDs im Debug-Build, nie auf eigene Anzeigen klicken (Lehre aus der RPCT-Sperre).

## Offene Entscheidungen
1. Glance (empfohlen) oder RemoteViews?
2. Soll das Widget auch eine Kategorie wählen lassen (Konfiguration beim Hinzufügen)? Empfehlung: nicht in Version 1.
3. Emoji/Textsymbole im Widget anzeigen? Empfehlung: ja, Satz mit Emoji am Ende wie in der App.
