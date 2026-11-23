---
name: project-content-v10-de
description: Content v10 DE (2026-11-23) - 30 neue Sätze je Textkategorie (ids 068-097), version 10, 1110 Einträge; Vorgehen und Prüfskript
metadata:
  type: project
---

Content v10 DE, 2026-11-23 (Plan `docs/PLAN-SAETZE-V10.md`, Variante B): je Textkategorie 30 neue Sätze, ids `de-<kat>-068..097`, `version` 10, emoji/ascii unverändert. Danach 1110 Einträge (97 je Textkategorie). Keine alten Sätze geändert. EN macht eine andere Instanz parallel.

Kennzahlen: "Schatz" 86/300 (29 %), Emoji-Sätze 78/300, kein "!", Ø 75-85 Zeichen, max. 106.

**Why:** Nutzerwunsch "deutlich mehr Sätze, alle natürlich und warm".

**How to apply:**
- Arbeitsweise, die sich bewährt hat: Entwurf als `# kategorie` + eine Zeile je Satz (`new_de.txt` im Scratchpad), Korrekturen als Ersetzungslisten-Skript (`texter_fixN.py`, assert count==1), Einbau mit `texter_build_v10.py` (ids ab max+1, Einfügen hinter letzter Zeile der Kategorie, `json.dumps(ensure_ascii=False, indent=2)+"\n"` ist byte-gleich zum Original).
- Prüfskript `texter_check_v10.py`: Wortlisten, 5-Gramm-Überschneidung gegen Bestand und innerhalb der neuen Sätze, difflib > 0.72, Emoji-Position, neue Emoji, Opener je Kategorie inkl. Bestand. Fehlalarm: "schuld" in "Entschuldige/Entschuldigung" (Test prüft nur "deine/meine schuld").
- Gesättigte Ideen im Bestand (bei v11 meiden): Kaffee ans Bett, Zettel (Spiegel/Brotdose/Kühlschrank), Sofa, Pulli/Kissen riechen nach dir, Sprachnachricht, Bahnhof abholen, Kalender-Herz, Pizza, Schokolade als Versöhnung, "Du bist der Grund", "Ich freu mich schon darauf". compliment hat 17x "Du bist"-Anfang, thanks 13x "Du hast", morning 7x "Guten Morgen,".
- Neues Emoji 🥐 (Unicode 9, erlaubt) in de-morning-087.
- Offen: Aufnahmen für die 300 neuen ids fehlen (gen_voice.py), EN muss gleich viele Sätze bekommen.
