---
name: project-content-v9
description: Content v9 (2026-11-15) - Stilwechsel auf fließende ganze Sätze mit "Schatz"/"Du bist", Emoji nur am Ende; was geändert wurde und welche Prüfungen sich bewährt haben
metadata:
  type: project
---

Content v9, Auftrag des Nutzers vom 2026-11-15: "Überprüfe alle Sätze. Mach diese fein, geschmeidig, aufeinander abgestimmt und absolut sinnig. Nicht beleidigend, neckisch oder sonst negativ. Benutze als Füllwort Schatz oder Du bist. Die Sätze sollen in sich stimmig sein, nicht abgehackt."

Ergebnis: alle 670 Textsätze je Sprache von einer Hand überarbeitet (708 de / 639 en inkl. einiger emoji/ascii-Zeilen geändert), ids unverändert, `version` 9, Release 1.0.15. Kosename-Anteil 28 % de ("Schatz"), 30 % en ("sweetheart"/"honey").

**Why:** Der knappe Fragment-Stil aus v2 bis v8 ("Wie du meine Hand nimmst. Das liebe ich.") wirkte auf den Nutzer abgehackt, besonders beim Vorlesen durch die Sprachausgabe. Er will weiche, runde Sätze und ausdrücklich die Anrede "Schatz".

**How to apply:**
- Schreibregeln stehen in `.claude/agents/texter.md` (Abschnitt "So klingt ein Mensch"). Die alten Regeln "kurz, 25-75 Zeichen, Kosenamen max. 10 %" sind aufgehoben.
- Bewährte automatische Checks (Skript je Lauf neu schreiben, Vorlage war `build_v9.py` im Session-Scratchpad): ContentToneTest-Listen 1:1; Zusatzliste (KI-Floskeln, leer/leise/quiet, Gedankenstriche, Semikolon, Anführungszeichen); Emoji nur am Ende und nur bereits verwendete Emoji; kein Punkt vor Emoji; Satzende . ! ?; Länge ≤ 110; Verkürzungen (grad, würd, wär, nix); 5-Wort-Überschneidungen über alle Textkategorien; Satzanfänge und Kosename-Anteil je Kategorie zählen.
- Häufigste Nacharbeit: wiederkehrende Schlussformeln ("und dafür danke ich dir" 6x, "das mag ich sehr an dir" 3x), "Ich hab …"-Anfänge in humor (15 von 67), "Thank you …"/"I'm sorry …"-Anfänge, mehrere "You're my favorite person" in einer Kategorie.
- Falle "gemeinsam" (enthält "einsam") -> "zusammen". Geschlechtsneutral bleiben: kein "Gastgeber", kein 🏃.
- Text als `NNN|Text`-Zeilen je Kategorie schreiben und per Skript ins JSON übernehmen (ascii enthält Backslashes). Vorgänger: [[project-content-v8]], Prüfregeln: [[feedback-review-checks]].
