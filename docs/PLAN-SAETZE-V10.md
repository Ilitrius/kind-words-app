# Plan: Satzrunde Content v10 (Entwurf, 2026-11-23)

Ziel: deutlich mehr Sätze, die alle natürlich und warm klingen (Nutzerwunsch), ohne Qualitätsverlust gegenüber v9.

## Ausgangslage (v9, geprüft am 2026-11-23)
- Je Sprache 810 Einträge: 10 Textkategorien mit je 67 Sätzen (670), dazu emoji und ascii mit je 70.
- Durchschnittslänge de 67, en 58 Zeichen (max. 103 bzw. 87). Regeln: `.claude/agents/texter.md` (ganze Sätze, "Schatz"/"sweetheart" in etwa 28 %, Emoji nur am Ende, nichts Neckendes, keine KI-Klang-Marker).
- Jeder Textsatz hat eine Aufnahme `assets/voice/<lang>/<id>-<hash8>.mp3` (de 21 MB, en 19 MB, AAB 68 MB). Ändert sich ein Satz, passt der Hash nicht mehr: die Aufnahme wird übergangen (Gerätestimme).

## Vorschlag: Umfang (Entscheidung nötig)
| Variante | neue Sätze je Kategorie und Sprache | Text-Sätze je Sprache | neue Aufnahmen (beide Sprachen) | ca. Zeichen (Credits) | ca. Zusatzgröße |
|---|---|---|---|---|---|
| A | +20 | 870 | 400 | 25.000 | +12 MB |
| **B (Empfehlung)** | +30 | 970 | 600 | 37.500 | +18 MB |
| C | +50 | 1.170 | 1.000 | 62.500 | +30 MB |
- Emoji und Textsymbole bleiben unverändert (sie werden nicht vorgelesen).
- Größe: das AAB liegt bei 68 MB, das Play-Limit für den Basis-Download ist 200 MB. B ist unkritisch.

## Ablauf
1. **Raster:** je Kategorie Themen/Situationen festlegen (z. B. Alltag, Fernbeziehung, Jahrestag, kleine Gesten), damit neue Sätze nichts doppeln. Vorhandene Anfänge und Schlussformeln zählen (Satzanfänge variieren, wiederkehrende Schlussformeln höchstens zweimal im ganzen Bestand).
2. **Entwurf:** `texter` (Opus) schreibt de und en eigenständig (nicht übersetzt), IDs fortlaufend (`de-love-068` …), nur neue Einträge, `version` auf 10.
3. **Prüfen:** Skript (`python -I`) auf Länge <= 110, Dubletten/Beinahe-Dubletten (auch kategorieübergreifend), KI-Klang-Marker, Emoji-Regeln, Anteil "Schatz"; `ContentToneTest` (Wortstämme! "gemeinsam" fällt durch, "zusammen" schreiben); danach zweite Lesung durch einen eigenen texter-Lauf (ok/überarbeiten/streichen).
4. **Nutzerfreigabe:** Stichprobe (je Kategorie 5 Sätze, de und en) zum Gegenlesen; Muttersprachler-Prüfung Englisch steht seit v9 aus.
5. **Aufnahmen:** `tools/gen_voice.py <lang> <voice_id> --max-chars N` (nur neue Dateien; Schlüssel nur per Umgebungsvariable, nie im Repo). Vorher: ElevenLabs-Lizenz (kommerzielle Nutzung) prüfen und den im Chat gezeigten Schlüssel erneuern. Stimme wie bisher (Jessica, multilingual_v2, Tempo 0.92).
6. **Build und Test:** Unit-Tests (`ContentSyncTest`, `ContentToneTest`), Lint, Emulator: Update von 1.1.0, Favoriten bleiben, neue Sätze erscheinen, Vorlesen spielt Aufnahmen. Version 1.2.0 (versionCode 18).
7. **Texte aktualisieren:** Beschreibung de/en ("rund 970 Sätze"), Neuigkeiten, Datenschutz unverändert (keine neue Datenverarbeitung).

## Risiken und offene Punkte
- **Repo-Größe:** 40 MB Audio liegen im Git; weitere 18 MB blähen die Historie dauerhaft auf. Entscheidung: Git LFS oder Audio als Release-Asset, bevor die neuen Dateien eingecheckt werden.
- **Qualität vor Menge:** lieber +20 sehr gute als +50 mittlere Sätze; schwache Sätze fallen bei der zweiten Lesung raus.
- **Gewichtung:** neue Sätze sollen sofort im Zufall vorkommen (kein Sonderfall im Code nötig, `ContentSync` ergänzt nach Version).
- **Stil-Anpassung:** `StyleAdapter` kann den Satzschluss ändern; Aufnahmen gelten nur für den Originaltext (bestehendes Verhalten prüfen, kein Neuland).
- **Kosten:** Zeichen = Credits bei ElevenLabs; vor dem Lauf das Budget per `--max-chars` begrenzen.

## Fertig, wenn
- alle neuen Sätze Checkliste und ContentToneTest bestehen, der Nutzer die Stichprobe freigegeben hat, de/en gleich viele Sätze haben und jeder Textsatz eine Aufnahme besitzt (Skript: 0 Sätze ohne Datei).
