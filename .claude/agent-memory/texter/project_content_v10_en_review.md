---
name: project-content-v10-en-review
description: Zweite Lesung EN Content v10 (2026-11-23) - 300 neue Sätze geprüft, 78 geändert; welche Mängel ein Fremd-Review fand, Skripte
metadata:
  type: project
---

Zweite Lesung der 300 neuen EN-Sätze (ids 068-097, 10 Textkategorien) durch eine frische texter-Instanz, 2026-11-23. Ergebnis: 222 ok, 56 überarbeitet (gleiche Idee), 22 ersetzt (neue Idee); version 10 und ids unverändert, nur `text` von ids >= 068. Hash vorher d09c8bf0…, nachher 4bdca7e8….

**Why:** Der Erstautor prüft gegen den Bestand, aber nicht auf Situations-Spezifik und Motiv-Häufung über Kategorien.

**How to apply (Mängel, die das Erstautor-Skript durchließ):**
- Zu spezifische Situationen: exam, thesis, wedding toast, business idea, pottery, students, new city, hotel room, flight, timezone (noon here), balcony garden, parents, surprise party, glasses, crossword, "on stage". Encouragement war am stärksten betroffen (13 von 30).
- Motiv-Häufung über alle Kategorien zählen (Regex über new ids): coffee war 11x neu (jetzt 4), hug 16x, charger 3x, pizza 2x. Ein Satz pro Motiv pro Kategorie reicht.
- Beinahe-Dubletten zu alten Sätzen mit derselben Idee bei anderer Wortwahl (difflib > 0,64 meist echt): "first one to say good morning", "as kind to you as you are", "since you came into my life", "warmed your side of the bed", "hugging your pillow", "green lights", "umbrella", "finish line", "heater bathroom". Vor jedem Ersatzsatz das Kernwort im ganzen Bestand greppen.
- Opener-Häufung: appreciation/compliment/thanks fast nur "You ...", apology/missyou "I ...", morning "I hope" (5x). Ein Drittel anders beginnen.
- Gendergefärbte Gesten meiden ("walk on the street side of the sidewalk").
- Gegen Ersatzsatz-Dubletten in der eigenen Kategorie die Kategorie nach jeder Runde neu lesen (z. B. "full of" zweimal, "save a few stories" vs. "hear about your day").
- Skripte: Scratchpad `s/texter_review_check.py` (Tonlisten, KI-Wörter, Emoji-Position, difflib 0,58, 4-Gramme, Opener/Kosename je Kategorie) und `s/texter_review_apply*.py` (Mapping id->Text, Protokoll rows.json).
- Offen: Muttersprachler-Check EN, Tests (ContentToneTest/ContentSyncTest) durch Hauptagent. Vorgänger: [[project-content-v10]], Regeln: [[feedback-review-checks]].
