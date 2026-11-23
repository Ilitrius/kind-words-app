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
- [x] Build 6 (Splash, Datenschutzlink, Update-Hinweis, Export/Import) bauen: `.\gradlew.bat lintDebug testDebugUnitTest assembleDebug`, im Emulator prüfen; Manifest-Berechtigungen und Data Safety gegenprüfen (Code ist geschrieben, nie kompiliert) (erledigt, Build 12 gebaut und getestet)
- [ ] Doppelte KI-Prüfung (zweites Modell eines anderen Anbieters) vor Release dokumentieren (`docs/APP-PLAN.md` Abschnitt 8)
- [x] Doku-Lücken laut Plan: Prüfkriterien Energie, Abhängigkeits-Scan (2026-11-08 in docs/APP-PLAN.md ergänzt)
- [ ] Spenden-Button: vorhanden in Einstellungen; Nutzer wünscht evtl. weitere Platzierung (Rückfrage).
- [ ] Play Console ausfüllen und Bundle hochladen (Nutzer; Anmeldung/Zahlungsdaten nicht durch Claude)
- [ ] Optional: Auto-Backup für Favoriten/eigene Sätze bei Gerätewechsel (würde Datenschutztext + Data Safety ändern)

## Play Console (Stand 2026-10-26)
- [x] App "RP Kind Words" (app.rpkindwords) angelegt, kostenlos; alle App-Inhalte-Erklärungen gespeichert (Datenschutz, Anzeigen, Zugang, Altersfreigabe, Zielgruppe 13+, Datensicherheit, Werbe-ID, Behörden, Finanzen, Gesundheit)
- [x] Store-Einstellungen (Kategorie Lifestyle, Kontakt), Store-Eintrag en-US + de-DE als Entwurf (Texte, Icon, Vorstellungsgrafik, 4 Screenshots je Sprache)
- [x] Release-Bundle `app/build/outputs/bundle/release/app-release.aab` (versionCode 4) + `mapping.txt` in der Console hochladen (Automatisierung vom Auto-Mode blockiert → manuell per Drag & Drop) (ersetzt durch Build 12)
- [x] (2026-11-23, Nutzer) Geschlossener Test: ≥12 Tester, ≥14 Tage erledigt; App ist in der Beta-Phase live; Zahlungskonto-Hinweis in der Console prüfen
- [x] Grafiken/Screenshots mit dem neuen Namen "RP Kind Words" neu erzeugen (hochauflösend, "cool/genial" laut Nutzer) (neue Screenshots 2026-11-06)
- [x] Favoriten häufiger vorschlagen (gewichtete Zufallsauswahl) (umgesetzt, 40 % Favoritenanteil)

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
- [x] (2026-11-11) AAB getestet: Split-APKs aus dem Release-Bundle (Gradle `:app:makeApkFromBundleForRelease`, nutzt bundletool intern) im Emulator API 35 installiert (master_3 + x86_64 + xxhdpi + de); Start, Übersetzen (ML Kit, nativ) und Vorlesen ohne Fehler.
- [ ] Update-Banner im Play-Internal-Test prüfen (nur über Play testbar). Toast ohne Browser am 2026-11-08 im Emulator verifiziert ("No browser found")
- [x] (2026-11-01) Emoji + ASCII-Katalog (Content v5, Kategorien emoji/ascii), Store-Texte angepasst; Bundle muss neu gebaut werden (versionCode ggf. 7)
- [x] Nutzer: Bundle 1.0.7 (Build 7) + mapping.txt in Play Console hochladen (ersetzt durch Build 12)
- [x] Nutzer: Bundle 1.0.8 (Build 8) statt 1.0.7 + mapping.txt hochladen (ersetzt durch Build 12)
- [x] Nächster Release (1.0.9/Build 9): Content v6 (mehr Emoji-Sätze) enthalten; Store-Beschreibung de/en auf "rund 330" ändern (erledigt)
- [x] Nutzer: Bundle 1.0.9 (Build 9) + mapping.txt hochladen; Store-Beschreibung (store/listings) in Console eintragen (ersetzt durch Build 12)

## Tester-Feedback 2026-11-06 (siehe docs/TESTER-FEEDBACK-2026-11.md)
- [x] (2026-11-06, 1.0.10) ASO: Beschreibung de/en um Schlüsselwörter (Partner, Liebe, nette Worte, Komplimente, Erinnerung, Gute-Nacht-Nachricht) und Anwendungsfälle erweitern; Datenschutz-Absatz ausführlicher; beide Sprachen getrennt optimieren; später regelmäßig prüfen (play-release, i18n-guardian)
- [ ] Nutzerstimmen in die Beschreibung: nur echte Zitate mit Zustimmung, sobald vorhanden (nichts erfinden)
- [ ] Screenshots neu: Textüberlagerungen (Kategorien, Teilen, Erinnerungen, Emoji/Textsymbole), Anwendungs-Kontext, hochwertiges Design, je Sprache (Nutzer/Designer + play-release)
- [x] (2026-11-06, 1.0.10) Onboarding: kurze interaktive Einführung beim ersten Start (nach Splash) mit Überspringen; optional Tooltips bei neuen Funktionen; Texte in `values/` + `values-de/` (android-dev, i18n-guardian)
- [x] (2026-11-06, 1.0.10; ohne In-App-Review-API, bewusst) Button "App bewerten" in den Einstellungen (Play-Store-Bewertungsseite) + optional In-App-Review-API zu passendem Zeitpunkt (nicht zusammen mit der Spendenbitte); Data Safety/Datenschutz prüfen (android-dev, legal-docs)
- [x] (2026-11-06, 1.0.10) Feedback senden: Eintrag in den Einstellungen (E-Mail-Intent, keine Internetberechtigung nötig) (android-dev)
- [ ] Wiederkehr: Erinnerungen existieren; prüfen, ob Hinweis auf neue Inhalte (Content-Updates) sinnvoll ist
- [ ] Bewertungen/Rezensionen nach Freigabe beobachten und Aufgaben daraus ableiten
- [x] Nutzer: Bundle 1.0.10 (Build 10) + mapping.txt hochladen; Store-Beschreibung de/en (store/listings) eintragen; Data Safety bleibt "keine Daten" (ersetzt durch Build 12)
- [ ] Offen: Nutzerstimmen (nur echte, mit Zustimmung; Play-Richtlinie verbietet anonyme Testimonials), Rezensionen beobachten, optional In-App-Review (Play-Review-Bibliothek, einmalig ab Start 10, nie am Spendenbitte-Start; Manifest/Datenschutz prüfen)

- [x] (2026-11-06) Screenshots neu: 6 je Sprache mit Überschriften (Home, Emoji-Kategorie, Teilen, Favoriten, Eigene Sätze, Einstellungen); Tools store/tools/capture_screenshots.py + frame_screenshots.py. In Play Console hochladen (store/graphics/play-screenshots/<lang>/). Hinweis: Teilen-Dialog ist Systemoberfläche (englisch) auch im DE-Satz.
- [x] Mehr nette und liebe Worte hinzufügen (neue Sätze in allen Kategorien, de + en eigenständig formuliert, Ton-Regel beachten, ids fortlaufend, Content-Version erhöhen, Store-Text "rund N Sätze" anpassen) (content, i18n-guardian) (erledigt, Content v7)
- [x] (2026-11-08) Mehr nette und liebe Worte: +240 je Sprache (Content v7, 570 je Sprache), Beschreibung "rund 570"
- [x] (2026-11-08) Lernen auf dem Gerät, "Satz vorschlagen" per Mail, texter-Agent
- [x] Nutzer: Bundle 1.0.11 (Build 11) + mapping.txt hochladen (ersetzt 1.0.10); Beschreibung de/en eintragen (ersetzt durch Build 12)
- [ ] Offen: Nutzersätze per Mail kuratieren (texter), zweite KI-Prüfung (Pflichtanforderung 9), Rezensionen beobachten
- [x] Sätze vorlesen: Button "Vorlesen" auf Home/Favoriten (Android TextToSpeech, offline mit der Systemstimme, keine Berechtigung/Internet nötig); Sprache = Satzsprache, Emoji/Textsymbole nicht mitsprechen (oder kurz beschreiben), Hinweis bei fehlender Stimme, TTS beim Verlassen freigeben (akkuschonend); Texte in values/ + values-de/; Datenschutz prüfen (Systemdienst, App sendet nichts); im Emulator testen (android-dev, qa-tester) (erledigt in 1.0.12)
- [x] Vorlesen in beliebiger Sprache (z. B. Deutsch -> Italienisch): Satz in eine per Auswahl gewählte Zielsprache übersetzen und mit Stimme vorlesen. Stimme in den Einstellungen wählbar (TextToSpeech.getVoices der Zielsprache, Probehören), Standard sehr warm und langsam (Sprechtempo ca. 0.75-0.85, Tonhöhe ca. 0.95, Pausen an Satzzeichen). Entscheidung nötig, wie übersetzt wird: (a) ML Kit On-Device-Translation: Modelle werden einmalig geladen und brauchen dafür INTERNET + Änderung von Store-Text/Datenschutz/Data Safety ("keine Internetberechtigung" entfällt), eigene Sätze übersetzbar; (b) mitgelieferte Übersetzungen des Katalogs per texter-Agent (offline, kein INTERNET, aber nur Katalogsätze, Aufwand je Sprache, Auswahl begrenzt auf wenige Sprachen wie IT, ES, FR); (c) Übersetzungs-App des Systems per Intent öffnen (ohne eigene Übersetzung). Empfehlung: erst (b) für wenige Sprachen, später (a) mit Einwilligung prüfen. Offene Punkte: fehlende Stimmdaten (Download-Hinweis), Emoji/Textsymbole nicht sprechen, Qualität der Übersetzungen (Muttersprachler-Prüfung), Texte de/en, Datenschutz, Emulator-Test (android-dev, texter, qa-tester); baut auf "Sätze vorlesen" auf (erledigt in 1.0.12 (Option a, ML Kit))
- [x] (2026-11-08) Sätze vorlesen (lokale TTS, Tempo, Stimme, Probehören) und Übersetzen vor dem Vorlesen (ML Kit, Option A, mit Einwilligung) umgesetzt, Release 1.0.12
- [ ] Nutzer VOR Upload 1.0.12: Data Safety in der Play Console ändern (Datenerhebung durch ML Kit, siehe store/play-console-answers.md), Store-Beschreibung de/en eintragen, Bundle + mapping.txt hochladen
- [ ] Offen: Qualität der Übersetzungen mit Muttersprachlern prüfen; optional weitere Zielsprachen; Katalogsätze ggf. vorübersetzen (offline ohne ML Kit)
- [ ] Offen (Stand 2026-11-08, nur durch den Nutzer möglich): Keystore extern sichern; Data Safety ändern, Bundle 1.0.12 + mapping.txt, Beschreibungen und Screenshots in der Play Console eintragen; Tester (≥12, 14 Tage) und Produktionszugriff; zweite KI-Prüfung eines anderen Anbieters (Pflichtanforderung 9); Muttersprachler-Prüfung der Übersetzungen
- [x] (2026-11-08) Data Safety in der Play Console als Entwurf ausgefüllt (Erhoben, nicht geteilt: Geräte-/andere IDs, Diagnosedaten, Andere App-Leistungsdaten; Zweck Analyse, optional; verschlüsselt; kein Konto; keine Löschanfrage). Gespeichert, aber NICHT bestätigt eingereicht: im Console unter "Veröffentlichungen - Übersicht" prüfen und mit Build 12 einreichen.
- [ ] Play Console Stand 2026-11-08: Alpha-Track hat Build 11 (1.0.11); Produktionszugriff: 12 Tester angemeldet seit 10 zusammenhängenden Tagen, es fehlen 4 Tage bis 14. Bundle 1.0.12 muss der Nutzer hochladen (Dateiauswahl nicht automatisierbar).
- [x] (2026-11-11) Play-Hinweis "randlose Anzeige": Quelle gefunden, keine Code-Änderung. Die Aufrufe `Window.setStatusBarColor/setNavigationBarColor` stehen nur in `androidx.activity.EdgeToEdgeApi*` (Bibliothek hinter `enableEdgeToEdge()`, Zweige für ältere Android-Versionen); eigener Code und Theme nutzen sie nicht. Der Hinweis blockiert keinen Release. Nutzer: in der Console prüfen, ob dort nur `androidx.activity.EdgeToEdgeApi…` als Aufrufer steht.

## Stand 2026-11-11 (Content v8, Release 1.0.13)
- [x] (2026-11-11) Alle 570 Sätze je Sprache auf Sinn, Logik und Natürlichkeit geprüft (4 texter-Läufe): 90 de / 116 en überarbeitet oder ersetzt (gleiche ids), +20 neue Sätze je Kategorie, insgesamt 810 je Sprache, Content v8. Beschreibung de/en "rund 810".
- [x] (2026-11-11) Release 1.0.13 (Build 13) gebaut: `dist/kindwords-1.0.13-release.aab/.apk`; Unit-Tests (42) und Lint grün; im Emulator Update 1.0.12 -> 1.0.13 getestet (Favorit bleibt, neue Sätze erscheinen, kein Absturz). `mapping.txt` unverändert (Code gleich 1.0.12).
- [ ] Nutzer: statt 1.0.12 jetzt **1.0.13** (Bundle + `dist/mapping.txt`) hochladen; Data Safety VOR dem Upload einreichen (siehe oben); Beschreibung de/en ("rund 810") eintragen.
- [ ] Offen: Paar-Emojis (💑 👫) sind auf älteren Android-Versionen nicht geschlechtsneutral; in v8 aus allen Sätzen entfernt. Neue Kaomoji (emoji/ascii 051-070) auf einem Gerät mit API 26 ansehen (qa-tester).
- [x] (2026-11-15) Play-Hinweise "randlose Anzeige" behoben (Code): `enableEdgeToEdge()` (androidx.activity ruft intern Window.setStatusBarColor/setNavigationBarColor auf, auch in 1.13.0) ersetzt durch `WindowCompat.setDecorFitsSystemWindows(window, false)` + Theme-Einträge (durchsichtige Leisten, `values-v29` ohne Kontrast-Scrim) + Symbolfarbe der Leisten in `KindWordsTheme`; Ausschnitte (Notch) per `displayCutout`-Insets. Release-APK: 0 Aufrufe der veralteten APIs (vorher 3 Klassen). androidx.activity 1.13.0, core-splashscreen 1.2.0. Release 1.0.14 (Build 14) gebaut: `dist/kindwords-1.0.14-release.aab/.apk`, neue `dist/mapping.txt` (alte = `mapping-1.0.13.txt`). Auf dem API-35-Emulator hell/dunkel geprüft.
- [ ] Nutzer: **1.0.16** hochladen (Bundle `dist/kindwords-1.0.16-release.aab` + `dist/mapping.txt`; ersetzt 1.0.13/1.0.14); danach in der Console prüfen, ob beide Hinweise verschwinden (Auswertung dauert oft einige Tage). Bleibt "nicht für alle Nutzer": die in der Console genannten Bildschirme ansehen; verdächtig ist `PlayCoreDialogWrapperActivity` der Play-Update-Bibliothek (app-update 2.1.0 ist die neueste Version).
- [ ] Offen: Optik auf Android 8-14 (API < 35) im Emulator prüfen (nur ein API-35-Image installiert): Leisten durchsichtig, Symbole lesbar, Inhalt nicht unter den Leisten.
- [ ] Offen: 5-Wort-Überschneidungen zwischen einzelnen Sätzen (z. B. "du bist mein Lieblingsmensch", "I'm proud of you") sind bewusst geduldet; bei der nächsten Satzrunde glätten.
- [x] (2026-11-15) Tester-Tablet Galaxy Tab S6 Lite (Android 13, Samsung-Sprachausgabe): Sprache nicht nutzbar. Fix im Code: `speech/VoiceSetup.kt` öffnet bei fehlenden Stimmendaten zuerst die Sprachausgabe-Einstellungen, dann die Standard-Installation, dann Google-Sprachdienste im Play Store; neuer Knopf "Einstellungen der Sprachausgabe" samt Hinweis in den Einstellungen, wenn keine lokale Stimme gefunden wird. Lint/Tests/Debug-Build grün, **auf dem Tablet noch nicht geprüft** (Ursache dort nicht bestätigt; Meldung/Sprache beim Tester erfragen).

## Stand 2026-11-15 (Content v9, Release 1.0.15)
- [x] (2026-11-15) Alle Sätze überarbeitet (Nutzerwunsch: fein, geschmeidig, aufeinander abgestimmt, nicht abgehackt, nichts Neckendes; Füllwort "Schatz" bzw. "Du bist …"): 708 de / 639 en geändert, ids gleich, Content v9. Regeln in `.claude/agents/texter.md` angepasst (ganze Sätze, "Schatz" 25-30 %, Emoji nur am Ende).
- [x] (2026-11-15) Release 1.0.15 (Build 15) gebaut: `dist/kindwords-1.0.15-release.aab/.apk`, `dist/mapping.txt` (1.0.14 = `mapping-1.0.14.txt`); Unit-Tests (42) und Lint grün. Enthält Edge-to-Edge-Fix, Sprachdaten-Helfer und Content v9. Noch nicht committet.
- [ ] Nutzer: Stichprobe der neuen Sätze lesen (de und en); Muttersprachler-Prüfung Englisch ("sweetheart"/"honey") steht weiter aus.
- [ ] Entscheidung Nutzer: weichere, menschlichere Stimme beim Vorlesen. Optionen A-E siehe BRAIN.md (Empfehlung: C, vorab erzeugte Audiodateien für die mitgelieferten Sätze, zuerst Hörprobe mit 10 Sätzen und 2-3 Stimmen).
- [x] (2026-11-15) Vorlesen mit natürlicher Stimme (Option C): ElevenLabs Stimme Jessica (multilingual_v2, Tempo 0.92), alle 670 Textsätze je Sprache (de 21 MB, en 19 MB) als `app/src/main/assets/voice/<lang>/<id>-<hash8>.mp3`; Speaker spielt sie per MediaPlayer, sonst Gerätestimme (Option A: beste Stimme zuerst, Satzpausen). Emoji/Textsymbole, eigene Sätze und übersetzte Sätze weiter per Gerätestimme. Release 1.0.16 (Build 16): AAB 68 MB, APK 102 MB. Im Emulator geprüft (MediaPlayer startet).
- [ ] Wichtig: Wird ein Satz geändert, passt der Hash nicht mehr, die Aufnahme wird übergangen. Neu erzeugen mit `tools/gen_voice.py <lang> <voice_id> --max-chars N` (Schlüssel nur per Umgebungsvariable ELEVENLABS_API_KEY).
- [ ] Nutzer: ElevenLabs-Lizenz (kommerzielle Nutzung im Tarif payg) und Namensnennung prüfen; Schlüssel widerrufen/erneuern (stand im Chat). Entscheiden, ob die 40 MB Audio ins Git-Repo sollen (bläht die Historie dauerhaft auf) oder per Git LFS/Release-Asset.
- [x] (2026-11-23) `app-ads.txt` unter https://ilitrius.github.io/app-ads.txt (Repo Ilitrius/Ilitrius.github.io, Publisher pub-2109805482260954); Kopie `docs/app-ads.txt`.
- [x] (2026-11-23) Werbung per AdMob (Banner Home/Favoriten, UMP): Code, Texte, echte IDs in `gradle.properties`, in Release 1.1.0 enthalten. Offen: Datenschutz-Nachricht in AdMob und Refresh >= 60 s im Konto prüfen (Nutzer).
- [ ] Datenschutz/Store: Vorlesen bleibt offline und ohne Datenübertragung; Beschreibung ggf. um "natürliche Stimme" ergänzen. Aufnahme-Download aus Sicht Data Safety unverändert (Audio liegt im App-Paket).

## Stand 2026-11-23 (Widget, Version 1.1.0)
- [x] (2026-11-23) Widget "Nettes Wort des Tages" (Glance, Kategorie wählbar) umgesetzt; Debug/Release-Build, Lint, Unit-Tests grün (Cloud, SDK 36).
- [x] (2026-11-23) Version 1.1.0 / versionCode 17, Store-Texte de/en + Neuigkeiten, Datenschutz de/en (Abschnitte 4, 12d, 12e), Console-Nachtrag.
- [ ] Emulatortest Widget (API 35 und 26): hinzufügen, Kategorie wechseln/ändern, Neu, Teilen, Dunkelmodus, Sprachwechsel, Neustart, Update von 1.0.16.
- [ ] Store-Screenshots mit Widget (de/en), danach Store-Eintrag aktualisieren.
- [x] (2026-11-23) Release 1.1.0 gebaut (`lintRelease bundleRelease`): `dist/kindwords-1.1.0-release.aab/.apk`, `dist/mapping.txt`. Nutzer: hochladen; Datenschutz-Seiten gehen mit dem Merge in `main` live (GitHub Pages).
- [ ] Qualitätsrunde: weitere warme, natürliche Sätze (Content v10; Aufnahmen per `tools/gen_voice.py` nachziehen, Hash je Satz).
- [x] Werbung umgesetzt (siehe oben).
- [ ] (2026-11-23) Satzrunde Content v10 geplant: `docs/PLAN-SAETZE-V10.md` (Umfang A/B/C und Audio-Ablage vom Nutzer zu entscheiden).
- [x] (2026-11-23) Content v10: je Sprache 300 neue Sätze (30 je Kategorie), zweite Lesung erledigt (de 71, en 78 Sätze überarbeitet/ersetzt), Tests grün. Offen: Stichprobe `docs/V10-STICHPROBE.md` (René), Muttersprachler-Prüfung EN, Aufnahmen für 600 Sätze (nach ElevenLabs-Lizenz/Schlüssel), danach Version 1.2.0 und Store-Zahl "rund 970 Sätze".

## Stand 2026-11-23 (Abend, Release 1.1.0)
- [x] Merge `main` -> `feature/admob-build17` committet (db27def); Lint/Tests/Release-Build grün.
- [ ] Nutzer: Play Console: Data Safety (Werbung + Widget, siehe `store/play-console-answers.md`) und Anzeigen-Angabe "enthält Werbung" prüfen, dann 1.1.0 + `dist/mapping.txt` hochladen, Store-Texte und "Neuigkeiten" eintragen; Branch per PR in `main` mergen (Datenschutz-Seiten gehen dann live).
- [ ] Emulatortest Release-APK: Banner (Echt-ID, nicht anklicken), UMP, Widget (API 35 und 26), Update von 1.0.16.
- [ ] Aufnahmen für 300 neue Textsätze je Sprache (`tools/gen_voice.py`) nach ElevenLabs-Klärung; bis dahin Gerätestimme.
- [ ] Offen unverändert: Keystore extern sichern, Muttersprachler-Prüfung EN, V10-Stichprobe lesen, Fold-Screenshots (unversioniert in `store/graphics/`) einsortieren.
- [x] (2026-11-23) Release-APK im Emulator getestet; zwei R8-Fehler im Widget behoben (Keep-Regeln), `dist/` neu gebaut.
- [ ] Nutzer: AdMob: Datenschutz-Nachricht (EWR/UK/CH) für app.rpkindwords anlegen und veröffentlichen; sonst fragt UMP im EWR nichts ab.
- [x] (2026-11-23) Widget-Konfigurationsdialog: Insets behoben (im Release-APK geprüft).
- [ ] Widget: Dunkelmodus, Sprachwechsel, Neustart, API 26 testen; "Datenschutzoptionen" erscheint ohne UMP-Formular (Ursache vermutlich die fehlende AdMob-Nachricht, nach Veröffentlichung neu prüfen).
