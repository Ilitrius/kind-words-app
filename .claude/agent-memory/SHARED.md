# Gemeinsames Agenten-Wissen (alle lesen, alle ergänzen)

Format: `YYYY-MM-DD · Thema · Regel · Warum`

- 2026-10-25 · Setup · Gradle 9.8.2, AGP 9.4.1 (eingebautes Kotlin, kein `kotlin-android`-Plugin), Kotlin 2.4.21, Compose BOM 2025.12.01, JDK 17 · Stand beim Anlegen (BOM siehe Versions-Eintrag unten).
- 2026-10-25 · Shell · Bash-Heredocs mit großen Dateiblöcken scheiterten unter Windows; Dateien mit dem Write-Tool anlegen, `gradlew.bat` via PowerShell starten · spart Fehlversuche.
- 2026-10-25 · i18n · Texte nur in `strings.xml` (en + de); Lint erzwingt es · Nutzeranforderung.
- 2026-10-25 · Versionen · compileSdk 36 ⇒ Compose BOM ≤ 2026.03.x (1.10), lifecycle 2.10, activity 1.12.4, core-ktx 1.18; neuere verlangen compileSdk 37 · Build-Fehler vermeiden.
- 2026-10-25 · i18n · `generateLocaleConfig` braucht `res/resources.properties` mit `unqualifiedResLocale=en`.
- 2026-10-25 · Inhalte · `messages_de.json`/`messages_en.json` (je 200 Texte, 20 pro Kategorie, version 1) liegen in `app/src/main/assets/messages/`; IDs stabil halten, Änderungen über `version` · Content-Update per id im Room-Import.
- 2026-10-25 · Rechtstexte · Format/Stil der Datenschutzerklärung und des Impressums wie RPCT-App (Kurzfassung-Box, nummerierte Abschnitte, Verantwortlicher René Puf) · Nutzervorgabe.
- 2026-10-25 · Datenschutz/Data Safety · App ohne INTERNET; Manifest nur POST_NOTIFICATIONS + RECEIVE_BOOT_COMPLETED (WorkManager entfernt); Daten nur lokal; Teilen über System-Dialog; Spenden-Link extern · Stand nach Umstellung auf AlarmManager.
- 2026-10-25 · Inhalte · Neue Texte in `assets/messages/messages_<lang>.json`: `version` erhöhen, ids stabil halten (`<lang>-<kategorie>-NNN`), Kategorien nur aus der Liste in PRODUCT.md, sonst Test `realAssetsAreValid` schlägt fehl · Update-Logik id-/versionsbasiert.
- 2026-10-25 · Strings · Kategorienamen als `category_<id>` in strings.xml (en+de); neue Kategorie braucht Eintrag in `MessageRepository.categories` und `categoryLabel()`.
- 2026-10-25 · Entscheidungen · Package app.rpkindwords final; Spendenlink (Stripe, wie RPCT) und Anbieterdaten vom Nutzer bestätigt; Texte freigegeben · Nutzer.
- 2026-10-25 · Design · App-Theme fest in Markenfarben (Rose #D6245F / Orange #E8590C), Dynamic Color entfernt; Icon = Verlauf Orange→Rose, weisse Sprechblase, Herz; Store-Grafiken per Skripte in store/tools · Nutzer wollte modern und HD.
- 2026-10-25 · Emulator · AVD Pixel_API35 vorhanden; UI-Knoten per uiautomator-dump finden (Extended-FAB-Text ist nicht auffindbar → Koordinate), Tab-Labels doppeln Titel → letzten Treffer nehmen; adb input text kann keine Umlaute · für Screenshot-Skript.
- 2026-10-25 · Signing · keytool auf PKCS12: -keypass wird ignoriert, Key-Passwort muss = Store-Passwort sein, sonst 'Get Key failed' beim Packen; 'java.util.Properties' in .kts per import nutzen (im android-Block löst 'java' auf die Extension auf) · Release-Build.
- 2026-10-25 · Play · Data safety: 'Erhebung' = Übertragung vom Gerät weg; rein lokale Daten nicht angeben → bei dieser App 'keine Daten erhoben/geteilt'; ändert sich mit INTERNET/SDKs/Werbung. Antworten + Begründung in store/play-console-answers.md · Google-Hilfe (geprüft 2026-10-25).
- 2026-10-25 · Erinnerungen · Zeitkritische Reminder NICHT mit WorkManager/JobScheduler (Standby-Bucket-Quoten blockieren Jobs), sondern AlarmManager.setAndAllowWhileIdle; Emulator-Test: `am set-standby-bucket <pkg> active`, `dumpsys alarm` (policyWhenElapsed/app_standby) · Fehler gefunden beim Release-Test.
- 2026-10-25 · Tests · `cmd jobscheduler run -f` startet WorkManager-Jobs nur kurz und bricht sie ab, solange die Wartezeit nicht erreicht ist → kein verlässlicher Test; echte Fälligkeit abwarten. adb `force-stop` löst bei WorkManager "Application was force-stopped, rescheduling" aus.
- 2026-10-25 · UI · Horizontal scrollende Chip-Reihen verstecken Inhalte (Sonntag); für kurze Auswahllisten FlowRow nutzen.
- 2026-10-26 · Daten · Room-Schema ändern ⇒ Migration schreiben + Schema exportieren, nie destruktiv; bundled-IDs nie ändern (Favoriten hängen an ihnen) · Nutzer: Favoriten/eigene Sätze müssen Updates überleben (getestet).
- 2026-10-26 · Sprache · UI-Sprache per attachBaseContext + recreate(); LocaleContext.wrap für Benachrichtigungen; nach Sprachwechsel Settings-Tab bleibt erhalten · ersetzt reine Inhaltssprache.
