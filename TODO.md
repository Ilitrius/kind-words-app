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
