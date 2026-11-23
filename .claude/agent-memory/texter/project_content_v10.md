---
name: project-content-v10
description: Content v10 (2026-11-23) - je Textkategorie 30 neue Sätze (ids 068-097), EN von einer texter-Instanz, DE parallel von einer anderen; was das Prüfskript fand
metadata:
  type: project
---

Content v10 (Plan `docs/PLAN-SAETZE-V10.md`, Variante B): je Sprache 300 neue Textsätze, 30 je Kategorie, ids `-068..-097`, emoji/ascii unverändert, `version` 10. Danach 1110 Einträge je Sprache (970 Textsätze).

Stand 2026-11-23: EN eingebaut (nicht committet), ContentToneTest und ContentSyncTest grün. DE schrieb eine zweite Instanz parallel, EN wurde ohne Blick in die DE-Neuzugänge geschrieben. Kosename je Kategorie 9/30 (sweetheart und honey gemischt), Emoji in 5-8 je Kategorie, nur bereits verwendete Emoji, Ø-Länge 66-78, max. 95.

**Why:** Nutzer will deutlich mehr natürliche, warme Sätze ohne Qualitätsverlust gegenüber v9.

**How to apply:**
- Arbeitsweise, die sich bewährt hat: zuerst Themenraster je Kategorie, Entwurf als `kat|Text`-Zeilen in eine Scratchpad-Datei, Prüfskript (`texter_check_v10.py`: Ton-Listen 1:1, KI-Floskeln, Satzzeichen, Emoji-Position und bekannte Emoji, Länge, difflib-Ähnlichkeit > 0,6 gegen den Bestand, 5-Wort-Überschneidungen, Opener und Kosename je Kategorie), Ersetzungen per Mapping-Skript (assert, dass jeder alte Text gefunden wurde), Einfügen per Skript direkt hinter den letzten Eintrag der Kategorie, dann Vergleich gegen `git show HEAD:` (keine alte id verändert, Reihenfolge gleich).
- JSON-Format: `json.dumps(ensure_ascii=False, indent=2) + "\n"` reproduziert die Datei exakt.
- Häufigste Treffer in eigenen Entwürfen: "true love" (Floskel, rutscht leicht rein), "one of my favorite ..." und "favorite" generell (im Bestand schon sehr oft), "I want you to know", "make it up to you", "I'm so proud of you", "I can't wait to see", "Good morning. I hope you"; Opener-Häufung "You have/You're so/You look" in compliment, "I miss" in missyou, "I'm sorry" in apology (Hälfte ohne "sorry"-Opener formulieren, z. B. mit konkreter Wiedergutmachung).
- Beinahe-Dubletten zum Bestand entstehen vor allem bei Alltagsgesten (Kaffee, Tee, Licht im Flur, Ladekabel, Pflanzen gießen, Sofa, Abschiedskuss). Vor dem Schreiben den Bestand je Kategorie lesen und solche Motive meiden.
- Falsche Treffer der Zusatzliste ignorieren: "ill" in still/will, "rough" in through, "pain" in paint, "mess" in messages.
- Offen: zweite Lesung durch einen eigenen texter-Lauf, Nutzer-Stichprobe, Muttersprachler-Check EN, Aufnahmen der 300 neuen Sätze. Vorgänger: [[project-content-v9]], Prüfregeln: [[feedback-review-checks]].
