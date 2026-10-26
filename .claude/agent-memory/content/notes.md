# Content-Agent Notizen

- 2026-10-25 · Stand: messages_de/en.json v1, je 200 Texte (10 Kategorien x 20), IDs `<lang>-<cat>-NNN`, max. Länge ~98 Zeichen (Limit 160).
- Tonalität: warm, ruhig, mit Humor-Einsprengseln; DE Du-Form; keine Kosenamen (geschlechtsneutral), keine Namen, keine Zitate/Liedzeilen.
- Fallstricke: keine doppelten Anführungszeichen in Texten (JSON-Escaping); Gendern vermeiden statt "Liebe/Lieber"; EN/DE eigenständig formulieren, nicht übersetzen.
- Validierung: `python -I` Skript (JSON, eindeutige IDs/Texte, Kategorien, Längen, Verteilung).
- Neue Texte: IDs fortlaufend vergeben, nie wiederverwenden; bei Änderungen `version` erhöhen (Content-Update per id).
- 2026-10-26 · Nutzerwunsch "wenig KI-Klang, kurz, natürlich": Texte v2 (gleiche ids/categories, nur text). Regeln: 25-75 Zeichen (max 100, >=50 % unter 60), 1-2 kurze Sätze, gesprochene Sprache (DE: hab, grad, ne; EN: Contractions). Konkrete Alltagsbilder (Kaffee, Sofa, Wecker, Heimweg) statt Abstraktes; kleine ehrliche Momente statt Kitsch. Verboten: Gedankenstriche, Semikolons, "nicht nur X sondern Y", Dreierlisten, Zauber/Magie/Reise/Seele/Universum/unendlich/einzigartig/strahlen, Sprichwort-Ton, Anführungszeichen, Emoji; "!" max 10 %. Satzanfänge stark variieren. Kosenamen max 10 %. DE/EN eigenständig. Bei Textänderung nur `text` ändern, ids stabil (Favoriten), `version` erhöhen. Prüfskript: Scratchpad check.py (ids/categories gegen `git show HEAD:` vergleichen).

## 2026-10-26 Ton-Regel (Nutzer)
- Keine Neckereien, Beleidigungen oder negativ gefärbten Wörter (Nervensäge, Schnarchnase, pain in the neck, grumpy, Mist gebaut, leer/leise/empty/quiet, schlechter Tag, wehtun/hurt, Schuld/fault, streiten/fight). "Ich liebe dich" ist ausdrücklich gut.
- Entschuldigungen: kurz, warm, wertschätzend ("Du bist mir wichtig"), keine Schuldzuweisung/Selbstanklage.
- Prüfung: ContentToneTest (Stämme + ganze Wörter; Fehlalarme vermeiden: "whatever"≠"hate", "Entschuldige"≠"Schuld").
