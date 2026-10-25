# Content-Agent Notizen

- 2026-10-25 · Stand: messages_de/en.json v1, je 200 Texte (10 Kategorien x 20), IDs `<lang>-<cat>-NNN`, max. Länge ~98 Zeichen (Limit 160).
- Tonalität: warm, ruhig, mit Humor-Einsprengseln; DE Du-Form; keine Kosenamen (geschlechtsneutral), keine Namen, keine Zitate/Liedzeilen.
- Fallstricke: keine doppelten Anführungszeichen in Texten (JSON-Escaping); Gendern vermeiden statt "Liebe/Lieber"; EN/DE eigenständig formulieren, nicht übersetzen.
- Validierung: `python -I` Skript (JSON, eindeutige IDs/Texte, Kategorien, Längen, Verteilung).
- Neue Texte: IDs fortlaufend vergeben, nie wiederverwenden; bei Änderungen `version` erhöhen (Content-Update per id).
