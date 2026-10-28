# App-Plan: RP Kind Words

Stand: 2026-10-27 · Entwurf; Rechtliches nur Orientierung, keine Rechtsberatung.
Ergänzende Dokumente: `PRODUCT.md` (Funktionen), `../BRAIN.md` (Entscheidungen), `../TODO.md` (Aufgaben).

## 1. Vision & Ziele
- **App-Name:** RP Kind Words (`app.rpkindwords`)
- **Kurzbeschreibung:** Nette Worte für deinen Partner oder deine Partnerin – als Vorschlag, Erinnerung und zum Teilen.
- **Langbeschreibung:** Die App schlägt kurze, liebevolle Sätze vor, die man persönlich sagt oder per Messenger schickt. Man speichert Favoriten, schreibt eigene Sätze und lässt sich zufällig oder zu festen Zeiten erinnern. Alles bleibt lokal auf dem Gerät: kein Konto, kein Tracking, keine Werbung. Deutsch und Englisch.
- **Ziele:** Veröffentlichung im Google Play Store (Alpha läuft), datensparsam, akkuschonend, vollständig offline nutzbar.

## 2. Zielgruppe & Personas
- Altersgruppe: ab 13 Jahren (Koordinator-Vorgabe 2026-10-25).
- Persona A: Paar in Beziehung, das sich im Alltag öfter Wertschätzung zeigen will.
- Persona B: Jemand, dem passende Worte schwerfallen und der Inspiration braucht.

## 3. Funktionale Anforderungen (MoSCoW)
| Prio | Anforderung | Status |
|---|---|---|
| Must | Vorschlag/Zufall, Kategorien, „Neuer Vorschlag“ | ✅ |
| Must | Teilen (Share-Dialog), Kopieren | ✅ |
| Must | Favoriten, eigene Sätze (CRUD) | ✅ |
| Must | Erinnerungen (zufällig / feste Zeiten, Wochentage) | ✅ |
| Must | Sprache wählbar (de/en/System) | ✅ |
| Must | Splash-Screen mit Logo | 🟡 implementiert 2026-10-27, nicht gebaut/getestet |
| Must | Version + Build in den Einstellungen | ✅ |
| Must | Datenschutzerklärung in den Einstellungen verlinkt | 🟡 implementiert, nicht gebaut/getestet |
| Must | Update-Hinweis (nicht blockierend) | 🟡 implementiert, nur über Play testbar |
| Must | Export/Import (JSON) von eigenen Sätzen, Favoriten, Einstellungen | 🟡 implementiert + Unit-Test geschrieben, nicht ausgeführt |
| Should | Spenden-Button (aktuell funktionsfähig, Stripe-Link) | ✅ |
| Could | Weitere Sprachen, weitere Inhaltspakete | offen |

## 4. Nicht-funktionale Anforderungen
- **Sicherheit:** nur `POST_NOTIFICATIONS` + `RECEIVE_BOOT_COMPLETED`; keine `INTERNET`-Berechtigung (Ausnahme: Update-Prüfung über Play-Core, siehe 5); Receiver nicht exportiert; keine Secrets im Repo; Eingabevalidierung beim Import.
- **Performance/Energie (messbare Kriterien):**
  - Kein Hintergrunddienst, keine Netzwerkaufrufe außerhalb der Update-Prüfung (max. 1× pro Start, höchstens alle 24 h).
  - Alarme inexakt (`setAndAllowWhileIdle`), kein Polling, kein WorkManager.
  - Kaltstart < 1,5 s auf Mittelklasse-Gerät (Prüfung mit `adb shell am start -W`).
  - Dark Mode unterstützt.
- **Offline:** Die gesamte App ist offline-first (Room + gebündelte JSON-Inhalte). Einzige netzabhängige Funktion: Update-Prüfung; bei fehlendem Netz stilles Überspringen, keine Fehlermeldung.
- **Datenschutz/DSGVO:** keine personenbezogenen Daten verlassen das Gerät. `allowBackup=false`; Export liegt in der Hand des Nutzers (Dateiauswahl über Storage Access Framework).

## 5. Architektur & Tech-Stack
Kotlin, Jetpack Compose, Material 3, Room, DataStore, AlarmManager. Manuelle DI in `KindWordsApp`, eine `MainViewModel`, State-Routing mit 4 Tabs.
- **Splash:** `androidx.core:core-splashscreen` mit Logo-Icon (keine Animation, akkuschonend).
- **Update-Prüfung:** Google Play In-App-Updates (`com.google.android.play:app-update`), flexibler Ablauf, nur Hinweisbanner auf der Startseite; Prüfung beim Start (max. 1×/24 h, Zeitstempel in DataStore). Play Services übernimmt die Netzwerkkommunikation. Dadurch ändern sich Datenschutztext und Data-Safety-Antworten nicht inhaltlich (keine Daten der App), sie werden aber ergänzt (siehe 9).
- **Export/Import:** JSON-Datei (`kindwords-backup.json`) über `ActivityResultContracts.CreateDocument/OpenDocument`; Format siehe 7.

## 6. KI-Agenten & Lernschleife
Gilt nur für den Entwicklungsprozess, die App enthält keine KI.
- Agenten in `.claude/agents/`, Gedächtnis in `.claude/agent-memory/` (+ `SHARED.md`), Protokoll in `CLAUDE.md`.
- Abweichung vom Standard: kein `/knowledge/`-Ordner; `.claude/agent-memory/` erfüllt die Rolle. Konsolidierung und PR-Freigabe: siehe „Offene Punkte“.

## 7. Daten & Schnittstellen
- Room-Tabelle `messages` (siehe `PRODUCT.md`), Einstellungen in DataStore.
- **Backup-Format (v1):**
  ```json
  { "format": "kindwords-backup", "version": 1,
    "customMessages": [ { "language": "de", "category": "custom", "text": "…" } ],
    "favorites": [ "de-love-001" ],
    "settings": { "contentLang": "de",
                   "reminder": { "enabled": true, "mode": "FIXED", "perDay": 1,
                     "windowStartMin": 540, "windowEndMin": 1200,
                     "fixedTimesMin": [1080], "days": [1,2,3,4,5,6,7] } } }
  ```
  Import: Größenlimit, Texte trimmen und auf max. 500 Zeichen begrenzen, unbekannte Felder ignorieren, Werte auf gültige Bereiche begrenzen, Favoriten-IDs nur übernehmen, wenn sie existieren. Nichts wird gelöscht; Duplikate eigener Sätze werden übersprungen.
- Keine Server-Schnittstellen. Externe Links: Datenschutz (GitHub Pages), Spendenlink.

## 8. Qualitätssicherung & Security-Review
- Unit-Tests (JUnit), Lint (`HardcodedText`, `MissingTranslation`), Emulator-Tests laut `BRAIN.md`.
- **Doppelte KI-Prüfung vor jedem Release (Pflicht, noch nicht durchgeführt):** Code- und UI-Flow-Review durch Claude **und** ein Modell eines anderen Anbieters (z. B. GPT oder Gemini); Befunde abgleichen, Ergebnis hier protokollieren.
  | Release | Claude | 2. Modell | Freigabe |
  |---|---|---|---|
  | 1.0.0 (Build 5) | offen | offen | offen |
- Abhängigkeits-Prüfung: Gradle-Dependency-Check oder Renovate vor Release (offen).
- **Crash-Reporting (Should):** bewusst nicht eingebaut – würde `INTERNET`, Einwilligungsdialog und Datenschutz-Änderungen erfordern. Stattdessen Play Console Android Vitals (anonym, ohne App-SDK). Entscheidung 2026-10-27, Revision möglich.

## 9. Rechtliches & Altersfreigabe
- Angestrebt: **13+**. Begründung: keine Gewalt, keine Glücksspiel-Elemente, keine Werbung, keine Nutzerinteraktion/Chat, kein Standort, nur freiwillige Spende über externen Browser-Link.
- DSGVO Art. 8 (DE: unter 16): Die App verarbeitet keine personenbezogenen Daten und holt keine Einwilligung ein; deshalb keine Elternzustimmung nötig. Bei jeder künftigen Datenverarbeitung (Crash-Reporting, Konten) neu bewerten.
- Google Play: IARC-Antworten und Zielgruppe in `store/play-console-answers.md`; Familienrichtlinien beachten, da 13–15-Jährige adressiert werden (keine Werbung, keine Werbe-ID).
- Datenschutz, Über uns, Impressum: `docs/` (de/en), öffentlich unter https://ilitrius.github.io/kind-words-app/ – Anbieterdaten aus RPCT übernommen, **Nutzer soll bestätigen**.
- Einwilligungsdialog: entfällt, solange keine optionale Datenverarbeitung existiert.
- Datenschutz-Textbausteine bei Änderungen: Update-Prüfung (Google Play Services, nur Versionsabgleich durch Google, keine Daten der App) und Export/Import (lokal, Datei liegt beim Nutzer).

## 10. Risiken & Annahmen
- Annahme: Verteilung nur über Google Play (In-App-Updates funktionieren nur dort).
- Risiko: Neuinstallation/Gerätewechsel verliert Daten → Export/Import mildert das.
- Risiko: Package-Name nach Veröffentlichung nicht änderbar.
- Risiko: Keystore und Passwörter noch nicht extern gesichert.
- Risiko: Zahlungs-/Spendenhinweis der Play Console noch offen.

## 11. Roadmap & Meilensteine
1. ✅ MVP, Signing, Alpha-Release 4 (in Google-Prüfung)
2. 🟡 Pflichtanforderungen aus Abgleich 2026-10-27 (Splash, Datenschutzlink, Update-Hinweis, Export/Import) – Code für Build 6 geschrieben; Build, Lint, Tests und Emulator-Prüfung stehen aus (in der Cloud-Umgebung ohne Android-SDK nicht möglich)
3. Geschlossener Test: ≥12 Tester, 14 Tage
4. Doppelte KI-Prüfung dokumentieren, Produktionszugriff beantragen
5. Weitere Inhalte/Sprachen

## 12. Offene Punkte
- Zweites KI-Modell für die Doppelprüfung festlegen (Anbieter, Zugang).
- Konsolidierungs- und PR-Freigabeprozess für Agenten-Wissen (Pflichtanforderung 14) – Aufwand/Nutzen für ein Ein-Personen-Projekt klären.
- Abhängigkeits-Scan-Werkzeug wählen.
- Anbieterdaten und Zahlungskonto-Hinweis bestätigen.
- Weitere Platzierung des Spenden-Buttons (Rückfrage aus `TODO.md`).
- Crash-Reporting später einführen? (Opt-in mit Consent-Dialog nötig.)
- Widerspruch in `PRODUCT.md` („Dynamic Color“) bereinigt: App nutzt festes Marken-Theme.

## 13. Änderungsprotokoll
- 2026-10-27 · Plan angelegt aus Abgleich mit den Pflichtanforderungen; Umsetzung Splash, Datenschutzlink, Update-Hinweis, Export/Import beschlossen.
- 2026-10-27 · Umsetzung der vier Punkte im Code (versionCode 6); Datenschutz de/en (Abschnitt 5a, Sicherung) und `store/play-console-answers.md` ergänzt. Status 🟡 = ungebaut/ungetestet.
