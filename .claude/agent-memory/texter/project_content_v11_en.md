---
name: project-content-v11-en
description: Content v11 EN (2026-11-25) - EN auf DE-Zahlen gebracht (+140, 1250 Einträge, version 11); Vorgehen, Dubletten-Lage, Test-Falle UP-TO-DATE
metadata:
  type: project
---

Content v11 EN, 2026-11-25: `messages_en.json` von version 10 / 1110 auf version 11 / 1250 Einträge gebracht, je Textkategorie auf die DE-Zahl (love 107, appreciation 114, morning 113, goodnight 109, encouragement 117, missyou 108, compliment 117, humor 107, thanks 111, apology 107; emoji/ascii 70). Neue ids `en-<kat>-098..`. Alte Einträge byte-gleich, nicht committet. DE v11 hatte vorher 140 neue Sätze aus 40 Nutzersätzen plus eigenen bekommen.

**Why:** DE und EN sollen gleich viele Sätze je Kategorie haben, bevor gebaut wird (TODO "Content v11").

**How to apply:**
- Die DE-Neuzugänge sind nur Anregung. Etwa jede vierte DE-Idee gab es in EN schon (Lachen im Nebenzimmer, Schirm, Kollegennamen, Wecker in 10 Min, Sonnenaufgang, Kapitel vorlesen, Spülmaschine läuft, Runde um den Block, Lieblingsfilm, Schal, alte Nachrichten, Winken am Fenster, Quiz, Speisekarte). Vorher die DE-Liste gegen den EN-Bestand lesen und Dubletten durch neue Ideen ersetzen.
- Kennzahlen neu: Kosename 38/140 (wie DE je Kategorie), Emoji in 22 Sätzen, 3 Ausrufezeichen, max. 105 Zeichen. Neue Emoji in EN (aus DE übernommen, Unicode ≤ 8): 🍿 🎲 📚 📞.
- Prüfskript `texter_check_v11.py` (Scratchpad dieser Session): Tonlisten 1:1, Zusatzliste, Emoji am Ende, difflib ≥ 0,58 und 4-Gramme gegen Bestand UND neue Sätze, Opener/Kosename/Emoji je Kategorie, Motivzähler. Drei Korrekturrunden mit Ersetzungsskript (assert count == 1) bis keine echten Treffer mehr.
- Ideen-Dublette ohne Wortgleichheit findet difflib nicht: Ein Ersatzsatz ("one text away all day") brachte die gestrichene DE-Idee "ruf mich zwischendurch an" zurück (en-encouragement-085/048). Nach dem Ersetzen prüfen, ob die neue Idee eine eben verworfene ist. Ebenfalls korrigiert: "Someone mowed the lawn early" (liest sich wie Lärm-Beschwerde).
- Häufigste eigene Treffer: wiederkehrende Formeln ("and I love that", "and it makes me want", "and I'm so thankful for that", "thank you for always", "every bit of it", "Good morning! I hope"), Struktur-Kopien ("Nobody ... the way you do", "There's rain on the window"), Motiv doppelt in der eigenen Charge (Schuhe in love und apology, Rezept in missyou und thanks, "move things around" in appreciation und apology).
- JSON-Datei ist CRLF: `(json.dumps(d, ensure_ascii=False, indent=2) + "\n").replace("\n", "\r\n")` ist byte-gleich; ohne CRLF-Ersatz nicht.
- Test-Falle: `testDebugUnitTest` bleibt nach reiner Asset-Änderung UP-TO-DATE (Tests lesen die Datei per Pfad). Mit `--rerun` starten und in `app/build/test-results/testDebugUnitTest/TEST-*Content*.xml` skipped=0 prüfen.
- Englische Nutzersätze (laut TODO viele Filmzitate) liegen nicht im Repo, sie wurden nicht gesichtet.
- Offen: Aufnahmen (gen_voice.py) für 140 neue EN-ids, Muttersprachler-Check, Stichprobe durch René. Vorgänger: [[project-content-v10]], [[project-content-v10-en-review]], Regeln: [[feedback-review-checks]], [[feedback-second-reading]].
