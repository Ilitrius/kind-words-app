# android-dev Memory

- 2026-10-25 · Build · `.\gradlew.bat assembleDebug lintDebug test` in PowerShell, ~1 Min; Ausgabe mit `Select-String` filtern.
- 2026-10-25 · KSP · Plugin `com.google.devtools.ksp` 2.3.12 läuft mit AGP 9.4.1 + Kotlin 2.4.21 ohne Extra-Flags; Room 2.8.5 ok. Versionen vorab per Maven-Metadaten (dl.google.com/dl/android/maven2/<pfad>/maven-metadata.xml) prüfen.
- 2026-10-25 · Compose · `material-icons-core` explizit nötig; kein `-extended`.
- 2026-10-25 · Tests · org.json in Unit-Tests: `testImplementation org.json:json`; Daten unter `src/test/resources`; Test gegen echte Assets mit relativem Pfad `src/main/assets/...` (Arbeitsverzeichnis = Modul `app`) und `assumeTrue(exists)`.
- 2026-10-25 · Erinnerungen · AlarmManager.setAndAllowWhileIdle + ReminderReceiver (Boot/Update/Zeitwechsel planen neu); WorkManager wurde ersetzt, weil Jobs im App-Standby blockierten · siehe BRAIN.md.
- 2026-10-25 · Inhalte · Neue/geänderte Texte nur wirksam, wenn `version` in der JSON erhöht wird.
- 2026-10-25 · Spenden · `donate_url` in values/donate.xml (translatable=false), Karte nur bei https://.
- 2026-10-27 · Locale · In Compose nie Locale.getDefault() für Anzeige; LocalConfiguration nutzen (In-App-Sprache wirkt nur auf Context-Resources).
- 2026-10-27 · Sandbox ohne Android-SDK (dl.google.com 403): Code ungebaut geschrieben; beim nächsten Windows-Build prüfen.
- 2026-11-23 · AdMob/UMP · play-services-ads 25.5.0 + UMP 4.0.0 (libs.versions.toml), kompiliert ohne Anpassung mit compileSdk 36. `ads/AdsConsent` (Singleton in KindWordsApp, `gather(activity)` nach Onboarding aus MainScreen, `adsReady`, `privacyOptionsRequired`), `ads/AdBanner`. `MobileAdsInitProvider` per tools:node=remove, SDK-Start nur nach UMP `canRequestAds()`. IDs: gradle.properties ADMOB_APP_ID/ADMOB_BANNER_ID (Standard Google-Test-IDs) -> manifestPlaceholders + BuildConfig; ADMOB_CONSENT_DEBUG_EEA nur Debug. Build-Warnung, solange Release Test-IDs nutzt. Release/R8 ohne Keep-Regeln getestet: Banner lädt, kein Absturz. Emulator Pixel_API35 zeigte UMP-Formular von selbst (Geografie EEA-artig). Neue Permissions im Manifest: AD_ID, ACCESS_ADSERVICES_AD_ID/ATTRIBUTION/TOPICS, WAKE_LOCK, FOREGROUND_SERVICE (+INTERNET/ACCESS_NETWORK_STATE schon da). Banner-Refresh-Rate (>=60 s) nur im AdMob-Konto einstellbar, nicht im Code.
