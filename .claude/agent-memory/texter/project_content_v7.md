---
name: project-content-v7
description: Content v7 (Nov 2026) - je Sprache 240 neue Sätze (20 je Kategorie), ids ab 028 bzw. emoji/ascii ab 031; Stand des Reviews
metadata:
  type: project
---

Content v7: je Sprache 240 neue Einträge, 20 je Kategorie. Vergeben werden die ids `<lang>-<kat>-028..047` (Textkategorien) und `-031..050` (emoji, ascii). Die `version` in den JSON-Dateien geht auf 7. Danach sind es 570 Einträge je Sprache.

Stand 2026-11-08: Die Entwürfe hat der Hauptagent erstellt. Der texter hat sie geprüft (380 ok, 70 überarbeitet, 30 ersetzt).
Stand 2026-11-11: v7 ist eingebaut. `messages_en.json` hat version 7 mit 570 Einträgen, ids bis -047 bzw. emoji/ascii bis -050. Nachfolger: [[project-content-v8]].

**Why:** Nutzerwunsch aus TODO.md: "mehr nette und liebe Worte" (commit 2eb664f).

**How to apply:**
- Wer die Sätze einbaut, vergibt die ids der Reihe nach in der Reihenfolge der Listen.
- Danach `ContentToneTest` und `realAssetsAreValid` laufen lassen.
- In den Store-Texten die Anzahl der Sätze anpassen (bisher "rund 330").
- Prüfregeln: [[feedback-review-checks]].
