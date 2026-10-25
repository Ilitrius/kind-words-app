# Gemeinsames Agenten-Wissen (alle lesen, alle ergänzen)

Format: `YYYY-MM-DD · Thema · Regel · Warum`

- 2026-10-25 · Setup · Gradle 9.8.2, AGP 9.4.1 (eingebautes Kotlin, kein `kotlin-android`-Plugin), Kotlin 2.4.21, Compose BOM 2025.12.01, JDK 17 · Stand beim Anlegen (BOM siehe Versions-Eintrag unten).
- 2026-10-25 · Shell · Bash-Heredocs mit großen Dateiblöcken scheiterten unter Windows; Dateien mit dem Write-Tool anlegen, `gradlew.bat` via PowerShell starten · spart Fehlversuche.
- 2026-10-25 · i18n · Texte nur in `strings.xml` (en + de); Lint erzwingt es · Nutzeranforderung.
- 2026-10-25 · Versionen · compileSdk 36 ⇒ Compose BOM ≤ 2026.03.x (1.10), lifecycle 2.10, activity 1.12.4, core-ktx 1.18; neuere verlangen compileSdk 37 · Build-Fehler vermeiden.
- 2026-10-25 · i18n · `generateLocaleConfig` braucht `res/resources.properties` mit `unqualifiedResLocale=en`.
- 2026-10-25 · Inhalte · `messages_de.json`/`messages_en.json` (je 200 Texte, 20 pro Kategorie, version 1) liegen in `app/src/main/assets/messages/`; IDs stabil halten, Änderungen über `version` · Content-Update per id im Room-Import.
- 2026-10-25 · Rechtstexte · Format/Stil der Datenschutzerklärung und des Impressums wie RPCT-App (Kurzfassung-Box, nummerierte Abschnitte, Verantwortlicher René Puf) · Nutzervorgabe.
- 2026-10-25 · Datenschutz/Data Safety · App ohne INTERNET; Berechtigungen im Manifest: POST_NOTIFICATIONS (eigen) + via WorkManager WAKE_LOCK, RECEIVE_BOOT_COMPLETED, FOREGROUND_SERVICE, ACCESS_NETWORK_STATE. Alle Daten (Sätze, Favoriten, eigene Sätze, Einstellungen) nur lokal (Room/DataStore), allowBackup=false. Teilen nur über Android-Share-Dialog. Spenden-Link öffnet externen Browser (Stripe), App sendet selbst nichts. Zielgruppe ab 13, keine Konten/Tracking · Grundlage für Datenschutzerklärung und Data-Safety-Formular (legal-docs, play-release).
- 2026-10-25 · Inhalte · Neue Texte in `assets/messages/messages_<lang>.json`: `version` erhöhen, ids stabil halten (`<lang>-<kategorie>-NNN`), Kategorien nur aus der Liste in PRODUCT.md, sonst Test `realAssetsAreValid` schlägt fehl · Update-Logik id-/versionsbasiert.
- 2026-10-25 · Strings · Kategorienamen als `category_<id>` in strings.xml (en+de); neue Kategorie braucht Eintrag in `MessageRepository.categories` und `categoryLabel()`.
