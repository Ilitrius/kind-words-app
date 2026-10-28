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
- Erinnerungen: AlarmManager `setAndAllowWhileIdle` (inexakt) – siehe Abschnitt "Erinnerungen" unten (WorkManager wurde ersetzt).
- Inhaltssprache: DataStore-Override (de/en) oder Systemsprache (de/en, sonst en). Dies ist unabhängig von der UI-Sprache (per-App-Sprache über Android-Systemeinstellung).
- Manifest (gemergt): nur `POST_NOTIFICATIONS` und `RECEIVE_BOOT_COMPLETED`; keine INTERNET-Berechtigung.
- Spenden-Button (Einstellungen): `donate_url` in `res/values/donate.xml` (`translatable=false`, gleicher Stripe-Link wie RPCT-App); öffnet per `ACTION_VIEW` den Browser; ist die URL leer/nicht `https://`, wird die Karte ausgeblendet. Freiwillig, schaltet nichts frei; die App übermittelt selbst keine Daten, keine zusätzliche Berechtigung.
- Zielgruppe: ab 13 Jahren; keine Konten, kein Tracking (Vorgabe Koordinator 2026-10-25).

## Pflichtanforderungen-Abgleich (2026-10-27)
- Plan: `docs/APP-PLAN.md`. Neu im Code (versionCode 6, **noch nicht gebaut/getestet**, in der Cloud ohne Android-SDK geschrieben): Splash (`core-splashscreen`, Theme `Theme.KindWords.Starting`), Datenschutzlink in Einstellungen (`privacy_url` je Sprache), Update-Hinweis (`update/UpdateController`, Play `app-update` 2.1.0, flexibel, max. 1×/24 h via DataStore `last_update_check`), Export/Import JSON (`data/Backup.kt`, Format v1, nur ergänzend, Test `BackupCodecTest`).
- Stand 2026-10-27: Branch `feature/pflichtanforderungen-build6` (gepusht, nicht in `main`; `docs/` ist GitHub Pages, deshalb erst nach erfolgreichem Build mergen). Nur `BackupCodec` + `BackupCodecTest` wurden auf der JVM kompiliert und ausgeführt (4/4 grün, Scratch-Projekt mit Kotlin-JVM-Plugin). Voller Android-Build in der Cloud unmöglich: `dl.google.com` (SDK-Plattform 36, Build-Tools) ist per Netzwerkrichtlinie gesperrt; Maven Central, maven.google.com, Gradle sind erreichbar. Entsperren: Cloud-Umgebung > Edit > Network access / Allowed domains. Sonst lokal bauen und Fehler melden.
- Nutzer-Wunsch-Ablauf: erst Plan (`docs/APP-PLAN.md`), dann Umsetzung, dann bauen/testen. Offen: Build, Lint, Emulator, zweite KI-Prüfung, PR.
- Zu prüfen beim ersten Build: Manifest-Merge (fügt `app-update` Berechtigungen hinzu?), Lint, Splash-Optik, Backup-Roundtrip im Emulator. Crash-Reporting bewusst nicht (siehe Plan 8).

## Fallstricke
- `Icons.Filled.*` braucht `material-icons-core` explizit als Dependency (kommt nicht transitiv durch material3); `ContentCopy` etc. sind nur in `-extended` (bewusst nicht genutzt, zu groß).
- Dynamic Color nur ab API 31 (`Build.VERSION_CODES.S`), minSdk ist 26 – sonst Lint-Fehler.
- Große Bash-Heredocs scheitern unter Windows → Write-Tool; Gradle über `.\gradlew.bat` in PowerShell.

## Offene Fragen
- Zweck/Funktionen der App (Briefing folgt vom Nutzer → `docs/PRODUCT.md`).
- Anbieterdaten: aus RPCT übernommen (René Puf) – bestätigen, ob für diese App identisch.

## Release / Signing
- Upload-Keystore `D:\AI_KI\kind-words-keys\kindwords-upload.jks` (PKCS12, Alias `kindwords-upload`, RSA 4096, gültig ~27 Jahre) + `keystore.properties` (Kopie dort und im Projektroot, gitignored). Key-Passwort = Store-Passwort (PKCS12). SHA-256 des Upload-Zertifikats: 9B:C5:0C:86:B3:54:EE:0E:75:01:C5:2B:95:27:40:93:A2:EC:81:27:3F:AC:24:AD:4F:05:4D:75:03:48:A5:7E
- Gradle liest `keystore.properties`; ohne Datei wird unsigniert gebaut. Release: `.\gradlew.bat lintRelease bundleRelease` · Version 1.0.0 (aktuell versionCode 2; pro Upload erhöhen).
- Release-APK (R8, Room) startet im Emulator ohne Absturz; Erinnerung und Teilen im Release getestet (siehe unten).

## Erinnerungen (Stand 2026-10-25, im Emulator getestet)
- Technik: `AlarmManager.setAndAllowWhileIdle` (inexakt) → `ReminderReceiver` zeigt Benachrichtigung (Teilen-Aktion) und plant den nächsten Alarm; Boot/Update/Zeitwechsel planen neu. Kein WorkManager mehr, Berechtigungen nur `POST_NOTIFICATIONS` + `RECEIVE_BOOT_COMPLETED`.
- Warum nicht WorkManager: Job blieb im App-Standby-Bucket "Rare" (Job-Quota) über zwei Minuten nach Fälligkeit blockiert; selten geöffnete Apps hätten Erinnerungen nicht pünktlich bekommen.
- Getestet (Release-Build, Emulator API 35): Alarm gesetzt, nach Neustart neu geplant, Benachrichtigung erscheint, Teilen öffnet System-Dialog mit dem Satz. Im Emulator steht eine frisch gestartete App nach Reboot im Bucket NEVER/Rare (Alarm +364 Tage) → zum Testen `adb shell am set-standby-bucket app.rpkindwords active`.
- Wochentage-Chips: FlowRow statt horizontalem Scrollen (Sonntag war sonst nicht sichtbar).
- 2026-10-25 · Offen · Sprachumstellung stellt nur die Sätze um, nicht die UI-Texte; Nutzer will beides (siehe TODO.md, nächste Session).

## Sprache & Datenpflege (2026-10-26)
- Sprache: `MainActivity.attachBaseContext` lädt Ressourcen in der gewählten Sprache (DataStore `content_lang`, null = System); Wechsel in den Einstellungen speichert und ruft `recreate()` auf. Benachrichtigungen nutzen `LocaleContext.wrap(…, lang)`.
- Updates: Room ohne Destructive-Fallback; `USER`-Sätze und Favoriten bleiben (getestet). **Jede Schema-Änderung braucht eine Migration + exportiertes Schema, nie `fallbackToDestructiveMigration`.** Bundled-IDs stabil halten (Favoriten hängen an der ID). Neuinstallation/Gerätewechsel verliert Daten (allowBackup=false).

## Inhalte & Auswahl (2026-10-26)
- Sätze neu formuliert (kurz, alltagsnah, wenig "KI-Klang"): Ø 50/44 Zeichen (de/en), Content-Version 2, ids/Kategorien unverändert (Favoriten bleiben; Update-Pfad im Emulator getestet). Stilregeln: .claude/agent-memory/content/notes.md.
- Zufallsauswahl bevorzugt Favoriten: `FavoriteBias.SHARE = 0.4` (MessageRepository.random), Test FavoriteBiasTest.
- `ndk.debugSymbolLevel = SYMBOL_TABLE` gesetzt; AndroidX-.so sind schon gestrippt → Play-Warnung "keine Debug-Symbole" bleibt (nur Hinweis).
- Store-Grafiken v2 (Icon plastisch mit Glanz/Funkeln, Feature-Graphic mit Sprechblasen, Name "RP Kind Words"); Screenshots 1350x2400 (9:16). Quelle: store/tools/*.py, Veröffentlichung via docs/store-assets.
- App-Name "RP Kind Words" (Launcher, Store). versionCode aktuell 5 (noch nicht hochgeladen; Alpha-Release 4 ist bei Google in Prüfung).

## Stil-Anpassung & Ton (2026-10-26)
- Lokale Stilauswertung der eigenen Sätze (`data/StyleProfile.kt`): Achsen Intensität, Verspieltheit, Energie, Länge, Lockerheit; Profil = Durchschnitt (volle Wirkung ab 3 Sätzen). Ziehgewicht `1 + 4·confidence·sim²` (passende Sätze bis 5× wahrscheinlicher), nie 0. `StyleAdapter` gleicht nur das Satzende an ("!" bzw. kein Punkt, ab 2 eigenen Sätzen und ≥60 %); eigene Sätze bleiben unverändert. Wirkt in Home-Vorschlag und Benachrichtigung. Tests: StyleProfileTest. Datenschutztext ergänzt (nur lokal, nichts gespeichert).
- Ton-Regel (Nutzer): "Ich liebe dich" ist gut; keine Neckereien/Beleidigungen/negativen Wörter (Nervensäge, Schnarchnase, pain in the neck, Mist, leer/leise, schlechter Tag, wehtun …). Durchgesetzt durch `ContentToneTest`; Content-Version 3. Auch Entschuldigungen freundlich formulieren (Wertschätzung statt Schuld).

- 2026-10-27 · Update-Hinweis · `update/InAppUpdater.kt`: Play `app-update` 2.1.0 (Listener-API, kein ktx), flexibler Flow, Check nur in onResume (max. alle 6 h), Fehler still verworfen; Snackbar Aktualisieren/Neu starten. Datenschutz + play-console-answers ergänzt · ungebaut, Manifest auf INTERNET prüfen.
- 2026-10-27 · Sprache · Zeit/Wochentage in Einstellungen nutzen `LocalConfiguration.current.locales[0]` statt `Locale.getDefault()` (JVM-Default folgt dem In-App-Override nicht) · sonst gemischte Sprache.
- 2026-10-27 · Akku · Geprüft: nur inexakter AlarmManager, kein WorkManager/Wakelock/Polling/Endlosanimation, UI-Flows `collectAsStateWithLifecycle`, ViewModel-Flows WhileSubscribed bzw. nur DataStore.

## 2026-10-28 Merge
- `feature/pflichtanforderungen-build6` und Update-Hinweis-Branch zusammengeführt: Es gilt `update/UpdateController` (Banner in MainScreen); `InAppUpdater` entfernt. Play `app-update` zieht Fragment 1.0.0 mit → `androidx.fragment` 1.8.9 explizit (sonst Lint-Fehler InvalidFragmentVersionForActivityResult).
- Compose: Strings in Launcher-Callbacks vorab per `stringResource`/`LocalResources.current` holen (Lint LocalContextGetResourceValueCall).
- Gradle-Cache nach abgebrochenem Build kann korrupte .aar enthalten → `--stop`, `~/.gradle/caches/9.8.2/transforms` und defekte Dateien löschen.
