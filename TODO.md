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
- [x] MVP umgesetzt (android-dev): Room + Seeding, Home/Favoriten/Eigene/Einstellungen, Erinnerungen (WorkManager), Spenden-Button, Unit-Tests
- [ ] Manueller Test auf Gerät/Emulator (Benachrichtigung, Teilen, Reboot) (qa-tester)
- [x] App-Icon (Verlauf, Sprechblase mit Herz) und Marken-Theme (Rose/Orange, kein Dynamic Color)

## Recht & Webseiten
- [x] Anbieterdaten aus RPCT übernommen (Nutzer soll bestätigen)
- [x] Datenschutzerklärung, Über-uns, Impressum ausgefüllt (de/en, live auf Pages)

## Play Store
- [x] Package-Name app.rpkindwords festgelegt
- [ ] Keystore, Signing, `bundleRelease` (play-release)
- [x] Grafiken: Icon 512, Feature-Graphic de/en, 4 Screenshots je Sprache (store/graphics)
- [ ] Listing-Texte fertig; offen: Data Safety, Content Rating (13+)
- [x] Spendenlink: vom Nutzer bestätigt (bereits getestet)
- [ ] Testphasen, Produktion

