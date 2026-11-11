---
name: project-content-v8
description: Content v8 (Nov 2026) - Bestand verfeinern + je Kategorie 20 neue Sätze, ids 048-067 bzw. emoji/ascii 051-070; parallel von mehreren texter-Instanzen erstellt
metadata:
  type: project
---

Content v8, Auftrag des Nutzers vom 2026-11-11: "Verfeinere die vorhandenen Sätze. Prüfe diese auf Sinn und Logik. Die Sätze sollen alle natürlich wirken. Füge für jede Kategorie 20 neue Sätze ein."

Der Hauptagent hat die Arbeit aufgeteilt, je Sprache und Kategoriegruppe eine texter-Instanz. Jede schreibt nur eine Ergebnisdatei ins Session-Scratchpad (`content-v8/<lang>-<A|B>.json` mit `revised` und `new`), der Hauptagent führt zusammen. Unter app/, store/ und docs/ fassen die Instanzen nichts an.
- Neue ids: Textkategorien `-048..067`, emoji und ascii `-051..070`. Danach sind es 67 bzw. 70 je Kategorie, 810 je Sprache. `version` geht auf 8.
- EN Teil B (compliment, humor, thanks, apology, emoji, ascii) am 2026-11-11 geliefert. 288 geprüft, 42 geändert (davon 6 ersetzt), 120 neu. Snapshot-Hash der Eingabe: sha256 cab326ea….

**Why:** Der Nutzer möchte mehr Sätze, und alle sollen natürlich wirken.

**How to apply:**
- Nach dem Einbau `ContentToneTest` und `realAssetsAreValid` laufen lassen.
- Store-Texte nennen die Anzahl der Sätze ("rund 330"). Das muss nachgezogen werden.
- Offen: love-026 enthält 👫 (Mann und Frau). Das ist nicht geschlechtsneutral, gemeldet an die love-Instanz bzw. den Hauptagenten.
- Prüfregeln: [[feedback-review-checks]]. Vorgänger: [[project-content-v7]].
