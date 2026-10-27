# TODO

Legende: `[ ]` offen · `[x]` erledigt · Agent in Klammern

## Setup
- [x] Android-Projekt (SDK 36, Compose) angelegt
- [x] de/en-Ressourcen, Lint-Regeln gegen Hardcoded-Text
- [x] Agenten + Lernprotokoll (`.claude/agents/`, `SHARED.md`)
- [x] `BRAIN.md`, `TODO.md`
- [x] Build + Lint grün (assembleDebug, lintDebug)
- [x] GitHub-Repo Ilitrius/kind-words-app + Pages (https://ilitrius.github.io/kind-words-app/)

## Produkt
- [x] Produkt-Briefing vom Nutzer → `docs/PRODUCT.md`
- [x] MVP umgesetzt (android-dev): Room + Seeding, Home/Favoriten/Eigene/Einstellungen, Erinnerungen (AlarmManager), Spenden-Button, Unit-Tests
- [x] Test im Emulator (Release): Benachrichtigung, Teilen-Aktion, Neustart (siehe BRAIN.md); echtes Gerät steht aus
- [x] App-Icon (Verlauf, Sprechblase mit Herz) und Marken-Theme (Rose/Orange, kein Dynamic Color)

## Recht & Webseiten
- [x] Anbieterdaten aus RPCT übernommen (Nutzer soll bestätigen)
- [x] Datenschutzerklärung, Über-uns, Impressum ausgefüllt (de/en, live auf Pages)

## Play Store
- [x] Package-Name app.rpkindwords festgelegt
- [x] Keystore, Signing, signiertes Release-AAB gebaut und im Emulator (R8) gestartet
- [ ] Keystore + Passwörter extern sichern (Passwortmanager/Backup)
- [x] Grafiken: Icon 512, Feature-Graphic de/en, 4 Screenshots je Sprache (store/graphics)
- [x] Listing-Texte fertig; Data Safety, Content Rating, Zielgruppe 13+ als Antworten vorbereitet (`store/play-console-answers.md`)
- [ ] Antworten in der Play Console eintragen (Nutzer)
- [x] Spendenlink: vom Nutzer bestätigt (bereits getestet)
- [ ] Testphasen, Produktion


## Erledigt am 2026-10-26
- [x] Sprachumstellung: Einstellung "Sprache" stellt jetzt die ganze App um (Oberfläche, Kategorien, Sätze, Benachrichtigung); Activity wird neu erstellt. Im Emulator getestet (de <-> en <-> System).
- [x] Update-Test: Favoriten und eigene Sätze bleiben nach Update (versionCode 2 -> 3) erhalten.

## Offen
- [ ] Spenden-Button: vorhanden in Einstellungen; Nutzer wünscht evtl. weitere Platzierung (Rückfrage).
- [ ] Play Console ausfüllen und Bundle hochladen (Nutzer; Anmeldung/Zahlungsdaten nicht durch Claude)
- [ ] Optional: Auto-Backup für Favoriten/eigene Sätze bei Gerätewechsel (würde Datenschutztext + Data Safety ändern)

## Play Console (Stand 2026-10-26)
- [x] App "RP Kind Words" (app.rpkindwords) angelegt, kostenlos; alle App-Inhalte-Erklärungen gespeichert (Datenschutz, Anzeigen, Zugang, Altersfreigabe, Zielgruppe 13+, Datensicherheit, Werbe-ID, Behörden, Finanzen, Gesundheit)
- [x] Store-Einstellungen (Kategorie Lifestyle, Kontakt), Store-Eintrag en-US + de-DE als Entwurf (Texte, Icon, Vorstellungsgrafik, 4 Screenshots je Sprache)
- [ ] Release-Bundle `app/build/outputs/bundle/release/app-release.aab` (versionCode 4) + `mapping.txt` in der Console hochladen (Automatisierung vom Auto-Mode blockiert → manuell per Drag & Drop)
- [ ] Geschlossener Test: ≥12 Tester, ≥14 Tage, danach Produktionszugriff beantragen; Zahlungskonto-Hinweis in der Console prüfen
- [ ] Grafiken/Screenshots mit dem neuen Namen "RP Kind Words" neu erzeugen (hochauflösend, "cool/genial" laut Nutzer)
- [ ] Favoriten häufiger vorschlagen (gewichtete Zufallsauswahl)

## Stand 2026-10-26 (Abend)
- [x] Release 4 (1.0.0) im geschlossenen Test (Alpha, Google-Gruppe rp-kind-words@googlegroups.com) – 16 Änderungen zur Google-Überprüfung eingereicht (Vorabprüfung läuft, Prüfung meist bis 7 Tage)
- [ ] Nutzer: KI-Asset-Erklärung im Store-Eintrag prüfen; Kurzbeschreibung beginnt mit "RP " (vom Nutzer?); Zahlungskonto-Hinweis klären
- [ ] Nach Freigabe: ≥12 Tester der Gruppe anmelden, 14 Tage testen, Produktionszugriff beantragen
- [x] Neue Grafiken "RP Kind Words", Favoriten-Gewichtung, Debug-Symbole-Einstellung, überarbeitete Sätze (Content v2) – Bundle versionCode 5 gebaut, Upload + neue Store-Grafiken in der Console stehen aus
- [x] Stil-Anpassung an eigene Sätze + Ton-Regel (keine negativen Wörter), Content v3 – im Bundle versionCode 5 (neu gebaut, noch nicht hochgeladen)

## Stand 2026-10-27
- [x] (2026-10-27, ohne Build, siehe BRAIN.md) Update-Hinweis: App merkt sich, wenn es ein Update gibt (Play In-App Updates API, `app-update`-Bibliothek über `gradle/libs.versions.toml`), und benachrichtigt den Nutzer (Hinweis/Snackbar mit "Aktualisieren"). Texte in `values/` + `values-de/`; Datenschutz/Data Safety prüfen (Play-Dienste-Abfrage). (android-dev, i18n-guardian)
- [x] (2026-10-27, ohne Build) Systemsprache umschalten ergänzen bzw. prüfen: Auswahl System/Deutsch/Englisch in den Optionen, und kontrollieren, dass wirklich alle Menüfelder und In-App-Texte (inkl. Benachrichtigungen, Datum/Zahlen) auf die gewählte Sprache wechseln. Gilt für alle Apps.
- [x] (2026-10-27, geprüft, keine Lücken) Akkuschonend: App soll möglichst wenig Energie verbrauchen — keine unnötigen Hintergrund-Jobs/Wakelocks/Polling, Alarme nur wenn nötig (inexakt statt exakt), Coroutines/Flows nur bei sichtbarer UI sammeln (`collectAsStateWithLifecycle`), Animationen/Render-Schleifen im Hintergrund stoppen, Wiederholungen und Neuzeichnen minimieren. Per Profiler/Battery Historian prüfen. Gilt für alle Apps.

- [ ] Nächster Build: `.\gradlew.bat assembleDebug lintDebug test` ausführen (2026-10-27 ohne SDK geschrieben, ungebaut); gemergtes Manifest auf fehlendes INTERNET prüfen; Update-Flow nur über Play-Internal-Test-Track testbar.

## Stand 2026-10-27 (Abend) – To-do für morgen
Ziel: Am Ende steht ein neues, komplettes Release im geschlossenen Test (Alpha) bereit.

Stand: Branch `claude/zen-keller-65rvby`; in der Cloud grün: `assembleDebug`, `lintDebug`, 33 Unit-Tests. Neu seit Build 5: Update-Hinweis, Locale-Fix, Zielgruppe (Für sie/Für ihn/Divers, Room v2, Content v5), Couch/Sofa-Regel, Backup-Ausschluss, Erinnerungs-Fix.

1. [ ] Lokal: `git pull`, dann `.\gradlew.bat clean assembleDebug lintDebug test` (JDK 17) – alle Unit-Tests durchführen (qa-tester)
2. [ ] Emulator `Pixel_API35` (qa-tester): Zielgruppen-Auswahl (sie/ihn/divers, abwählen, Hinweis), Sätze passen zur Auswahl; Sprachumschaltung System/DE/EN in allen Menüfeldern inkl. Benachrichtigung, Wochentage, Uhrzeit; Erinnerung (zufällig + feste Zeiten, Neustart/Boot, Aktion "Teilen"); Favoriten, eigene Sätze; Spenden-Button; Rotation; Dark Mode
3. [ ] Update-Test auf Daten: Installation Build 5 (versionCode 5) -> neuer Build: Favoriten und eigene Sätze bleiben, Room-Migration 1->2 ohne Absturz
4. [ ] Entscheidung: `feature/pflichtanforderungen-build6` mergen (Splash, Datenschutzlink, Export/Import, `docs/APP-PLAN.md`)? Konflikte in 14 Dateien, Update-Hinweis doppelt (`InAppUpdater` vs `UpdateController`) – einen wählen; Room-Version zusammenführen. Danach APP-PLAN.md mit neuem Stand aktualisieren (Zielgruppe, Review, Änderungsprotokoll) (android-dev)
5. [ ] Gemergtes Manifest prüfen (kein INTERNET, keine neue Berechtigung); Lint-Warnung `AppBundleLocaleChanges` (LocaleContext) für das Bundle beurteilen
6. [ ] Datenschutz + Data Safety gegenprüfen: Zielgruppen-Auswahl (nur lokal gespeichert), Backup-Ausschluss, Update-Prüfung (legal-docs, play-release)
7. [ ] Offene Review-Punkte (optional, Opus-Review): `runBlocking` in `MainActivity.attachBaseContext`, Updater bei Rotation, Kategorie nach Prozess-Tod (SavedStateHandle), Favoritenliste ohne Zielgruppenfilter (Entscheid), `countVisible` entfernen
8. [ ] Doppelte KI-Prüfung (Pflicht 9): zweites Modell eines ANDEREN Anbieters festlegen und Review wiederholen; Ergebnis im Plan protokollieren (bisher nur Opus 5.5 = gleicher Anbieter)
9. [ ] Inhalte durchlesen: neue Sätze für sie/ihn (je 100 pro Sprache) auf Ton, Kosenamen, Klischees prüfen (i18n-guardian)
10. [ ] Store-Texte und Screenshots um Zielgruppen-Auswahl ergänzen (de/en, Titel ≤30, Kurz ≤80, Beschreibung ≤4000) (play-release)
11. [ ] Release bauen: versionCode/versionName erhöhen (mind. 6 / z. B. 1.1.0), `.\gradlew.bat bundleRelease` (Keystore unter `D:\AI_KI\...`), AAB im Emulator (R8) starten, `mapping.txt` sichern
12. [ ] Upload in die Play Console, Alpha/Geschlossener Test (Nutzer); Release-Notes de/en; danach ≥12 Tester, 14 Tage
13. [ ] Weiterhin offen: Keystore + Passwörter extern sichern; Anbieterdaten und Zahlungskonto-Hinweis bestätigen; weitere Platzierung des Spenden-Buttons; Crash-Reporting später (Opt-in)
