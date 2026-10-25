# android-dev Memory

- 2026-10-25 · Build · `.\gradlew.bat assembleDebug lintDebug test` in PowerShell, ~1 Min; Ausgabe mit `Select-String` filtern.
- 2026-10-25 · KSP · Plugin `com.google.devtools.ksp` 2.3.12 läuft mit AGP 9.4.1 + Kotlin 2.4.21 ohne Extra-Flags; Room 2.8.5 ok. Versionen vorab per Maven-Metadaten (dl.google.com/dl/android/maven2/<pfad>/maven-metadata.xml) prüfen.
- 2026-10-25 · Compose · `material-icons-core` explizit nötig; kein `-extended`.
- 2026-10-25 · Tests · org.json in Unit-Tests: `testImplementation org.json:json`; Daten unter `src/test/resources`; Test gegen echte Assets mit relativem Pfad `src/main/assets/...` (Arbeitsverzeichnis = Modul `app`) und `assumeTrue(exists)`.
- 2026-10-25 · Erinnerungen · WorkManager OneTime + REPLACE, nächster Termin aus reinem `ReminderPlanner` (Random(seed=epochDay)); WorkManager fügt WAKE_LOCK/RECEIVE_BOOT_COMPLETED/FOREGROUND_SERVICE/ACCESS_NETWORK_STATE ins Manifest (kein INTERNET).
- 2026-10-25 · Inhalte · Neue/geänderte Texte nur wirksam, wenn `version` in der JSON erhöht wird.
- 2026-10-25 · Spenden · `donate_url` in values/donate.xml (translatable=false), Karte nur bei https://.
