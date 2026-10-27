# Übergabe (Stand 2026-10-27)

## Allgemein
- Dieser Stand wurde in einer Cloud-Sitzung (ohne Android-SDK) erstellt: **nichts davon wurde gebaut, gelintet oder getestet.**
- Branch `ccr-4dfc41dd-r2vb0x` ist nicht in main/master gemergt; `versionCode`/`versionName` nicht erhöht; keine PR.
- Erster Schritt lokal: `.\gradlew.bat assembleDebug lintDebug test`, Fehler beheben, dann im Emulator `Pixel_API35` prüfen (Sprachumschaltung System/DE/EN in allen Menüfeldern).
- Danach: mergen, Version erhöhen, `.\gradlew.bat bundleRelease` (Keystore unter `D:\AI_KI\...`), Upload in die Play Console macht der Nutzer selbst.
- Neue Update-Bibliothek `com.google.android.play:app-update` 2.1.0 (Version nicht gegen Maven geprüft): im gemergten Manifest prüfen, dass keine neue Berechtigung hinzukommt; Update-Flow nur über Play-Test-Track testbar (nicht per adb/Sideload).
- Offene Punkte stehen in `TODO.md`.

## Kind Words
- Umgesetzt (ungebaut): Update-Hinweis (`update/InAppUpdater.kt`, Snackbar in `MainScreen`), Locale-Fix für Wochentage/Uhrzeit in `SettingsScreen`, Datenschutz (`docs/privacy-policy.html`, `docs/datenschutz.html`, `store/play-console-answers.md`).
- Akku geprüft: keine Lücken.
- Offen: Data-safety-Abgleich in der Console, Ablauf im internen Test-Track testen.
