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
- [ ] Build 6 (Splash, Datenschutzlink, Update-Hinweis, Export/Import) bauen: `.\gradlew.bat lintDebug testDebugUnitTest assembleDebug`, im Emulator prüfen; Manifest-Berechtigungen und Data Safety gegenprüfen (Code ist geschrieben, nie kompiliert)
- [ ] Doppelte KI-Prüfung (zweites Modell eines anderen Anbieters) vor Release dokumentieren (`docs/APP-PLAN.md` Abschnitt 8)
- [ ] Doku-Lücken laut Plan: Prüfkriterien Energie, Abhängigkeits-Scan
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

- [x] (2026-10-28) Build + Lint + Tests grün nach Merge von `feature/pflichtanforderungen-build6` (UpdateController statt InAppUpdater; Fragment 1.8.9 explizit wegen Lint, LocalResources in Settings). Gemergtes Manifest: kein INTERNET nötig. Update-Flow nur über Play-Internal-Test-Track testbar.

## Stand 2026-10-28 (Abend)
- [x] Merge, Content v4, Store-Texte neu, QA-Lauf, Fixes (Nav-Labels, Banner); Bundle versionCode 6 gebaut
- [ ] Nutzer: Bundle + mapping.txt hochladen, Store-Texte/Grafiken in Console, Keystore sichern, Tester/14 Tage
- [ ] Toast ohne Browser im Emulator verifizieren; AAB per bundletool testen; Update-Banner im Play-Internal-Test prüfen
