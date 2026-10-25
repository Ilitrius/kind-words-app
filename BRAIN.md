# BRAIN – Gemeinsames Projektwissen

Das „Gehirn“ des Projekts: Entscheidungen, Fakten, Fallstricke. Wird von Menschen und allen Agenten gepflegt (Format: `Datum · Thema · Regel · Warum`). Agentenspezifisches Wissen: `.claude/agent-memory/<agent>/`, agentenübergreifendes: `.claude/agent-memory/SHARED.md`. Aufgaben: `TODO.md`.

## Entscheidungen
- 2026-10-25 · Plattform · Kotlin + Jetpack Compose, Material 3, compileSdk/targetSdk 36, minSdk 26 · Nutzervorgabe SDK 36.
- 2026-10-25 · Sprachen · Englisch (Standard) + Deutsch; Texte ausschließlich in `strings.xml`; Lint erzwingt es · Nutzervorgabe.
- 2026-10-25 · Package · `app.rpkindwords` (Nutzerentscheidung; namespace + applicationId; nach Play-Veröffentlichung unveränderbar) · Nutzervorgabe.
- 2026-10-25 · Hosting · Rechtstexte/Infoseiten in `docs/` via GitHub Pages · Nutzervorgabe: im Repo erreichbar.

## Technik-Fakten
- Gradle 9.8.2, AGP 9.4.1 (eingebautes Kotlin), Kotlin 2.4.21, Compose BOM 2025.12.01 (Compose 1.10; neuere BOMs/lifecycle 2.11/core-ktx 1.19 verlangen compileSdk 37), JDK 17, SDK in `D:\Android\Sdk` (Plattformen 35+36).
- `generateLocaleConfig` braucht `app/src/main/res/resources.properties` (`unqualifiedResLocale=en`).

- Vorlage für Datenschutz/Impressum: Seite der RPCT-App (`D:/AI_KI/rpct-app/docs/privacy-policy.html`, Repo Ilitrius/rpct-privacy); Verantwortlicher: René Puf, Neuburg an der Donau.

- Bibliotheken (compileSdk 36 getestet): Room 2.8.5 + KSP 2.3.12 (`ksp`-Plugin neben AGP-9-Built-in-Kotlin funktioniert ohne Zusatzflags; KSP-Versionierung ist seit 2.3 unabhängig von Kotlin), DataStore 1.2.1, lifecycle-viewmodel-compose 2.10.0, material-icons-core (BOM), Unit-Tests: JUnit 4.13.2 + `org.json:json` (android.jar-org.json ist in Unit-Tests gestubbt).
- Architektur: `KindWordsApp` (manuelle DI: Room, DataStore, `MessageRepository`), eine `MainViewModel`, State-Routing mit 4 Tabs (`ui/MainScreen.kt`), keine Navigation-Lib. Room-Schema-Export: `app/schemas/`.
- Inhalte: `messages` (Room) wird aus `assets/messages/messages_<lang>.json` per `ContentSync.plan` (rein, getestet) abgeglichen; installierte Version je Sprache in DataStore (`content_version_<lang>`); nur wenn Datei-Version > installiert. BUNDLED: einfügen/aktualisieren (favorite/hidden bleiben), nicht mehr vorhandene löschen außer Favoriten; USER nie angefasst. Fehlende/defekte Dateien werden übersprungen. Eigene Sätze: id `user-<uuid>`, Kategorie `custom`. Neue Inhalte also immer `version` erhöhen!
- Erinnerungen: WorkManager `OneTimeWork` (inexakt, kein Exact-Alarm) mit `initialDelay` bis zum nächsten Termin (`ReminderPlanner`, deterministisch pro Datum); Worker zeigt Notification und plant den nächsten Termin. WorkManager überlebt Reboot selbst; zusätzlich Neuplanung bei jedem App-Start. Wochentage gelten für beide Modi (Zufall: 1–3×/Tag, gleichmäßig auf Zeitfenster verteilt; Fix: bis 6 Uhrzeiten). `POST_NOTIFICATIONS` wird erst beim Einschalten der Erinnerung angefragt.
- Inhaltssprache: DataStore-Override (de/en) oder Systemsprache (de/en, sonst en). Dies ist unabhängig von der UI-Sprache (per-App-Sprache über Android-Systemeinstellung).
- Manifest (gemergt): `POST_NOTIFICATIONS` (eigen) + von WorkManager: `WAKE_LOCK`, `RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE`, `ACCESS_NETWORK_STATE`. **Keine** `INTERNET`-Berechtigung. Teilen via Share-Intent/Chooser ohne Berechtigung.
- Spenden-Button (Einstellungen): `donate_url` in `res/values/donate.xml` (`translatable=false`, gleicher Stripe-Link wie RPCT-App); öffnet per `ACTION_VIEW` den Browser; ist die URL leer/nicht `https://`, wird die Karte ausgeblendet. Freiwillig, schaltet nichts frei; die App übermittelt selbst keine Daten, keine zusätzliche Berechtigung.
- Zielgruppe: ab 13 Jahren; keine Konten, kein Tracking (Vorgabe Koordinator 2026-10-25).

## Fallstricke
- `Icons.Filled.*` braucht `material-icons-core` explizit als Dependency (kommt nicht transitiv durch material3); `ContentCopy` etc. sind nur in `-extended` (bewusst nicht genutzt, zu groß).
- Dynamic Color nur ab API 31 (`Build.VERSION_CODES.S`), minSdk ist 26 – sonst Lint-Fehler.
- Große Bash-Heredocs scheitern unter Windows → Write-Tool; Gradle über `.\gradlew.bat` in PowerShell.

## Offene Fragen
- Zweck/Funktionen der App (Briefing folgt vom Nutzer → `docs/PRODUCT.md`).
- Anbieterdaten: aus RPCT übernommen (René Puf) – bestätigen, ob für diese App identisch.

## Release / Signing
- Upload-Keystore `D:\AI_KI\kind-words-keys\kindwords-upload.jks` (PKCS12, Alias `kindwords-upload`, RSA 4096, gültig ~27 Jahre) + `keystore.properties` (Kopie dort und im Projektroot, gitignored). Key-Passwort = Store-Passwort (PKCS12). SHA-256 des Upload-Zertifikats: 9B:C5:0C:86:B3:54:EE:0E:75:01:C5:2B:95:27:40:93:A2:EC:81:27:3F:AC:24:AD:4F:05:4D:75:03:48:A5:7E
- Gradle liest `keystore.properties`; ohne Datei wird unsigniert gebaut. Release: `.\gradlew.bat lintRelease bundleRelease` · Version 1.0.0 (versionCode 1; pro Upload erhöhen).
- Release-APK (R8, Room, WorkManager) startet im Emulator ohne Absturz; Erinnerungs-Benachrichtigung im Release nicht getestet.

## Erinnerungen (Stand 2026-10-25, im Emulator getestet)
- Technik: `AlarmManager.setAndAllowWhileIdle` (inexakt) → `ReminderReceiver` zeigt Benachrichtigung (Teilen-Aktion) und plant den nächsten Alarm; Boot/Update/Zeitwechsel planen neu. Kein WorkManager mehr, Berechtigungen nur `POST_NOTIFICATIONS` + `RECEIVE_BOOT_COMPLETED`.
- Warum nicht WorkManager: Job blieb im App-Standby-Bucket "Rare" (Job-Quota) über zwei Minuten nach Fälligkeit blockiert; selten geöffnete Apps hätten Erinnerungen nicht pünktlich bekommen.
- Getestet (Release-Build, Emulator API 35): Alarm gesetzt, nach Neustart neu geplant, Benachrichtigung erscheint, Teilen öffnet System-Dialog mit dem Satz. Im Emulator steht eine frisch gestartete App nach Reboot im Bucket NEVER/Rare (Alarm +364 Tage) → zum Testen `adb shell am set-standby-bucket app.rpkindwords active`.
- Wochentage-Chips: FlowRow statt horizontalem Scrollen (Sonntag war sonst nicht sichtbar).
