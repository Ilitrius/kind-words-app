---
name: feedback-second-reading
description: Zweite Lesung fremder Sätze (Content v10 DE, 2026-11-23) - was beim strengen Review auffiel, Ablauf und Fallen beim Ersetzen
metadata:
  type: feedback
---

Zweite Lesung der 300 neuen DE-Sätze v10 (ids 068-097): 229 ok, 28 überarbeitet, 43 ersetzt (24 %). Die meisten Treffer kamen nicht vom Prüfskript, sondern beim Lesen.

**Why:** Der Autor prüft vor allem Wortlisten und Länge. Zu spezielle Situationen und gehäufte Motive findet nur ein Leser, der den ganzen Bestand im Blick hat.

**How to apply:**
- Häufigster Mangel (gut die Hälfte): Die Sätze setzen eine sehr spezielle Lage voraus. Beispiele: Fahrstunde, 5-km-Lauf, Klavier, Spanisch, Chefin (nicht neutral), Umzug, Oma, Schwester, Eltern, Saugroboter, Balkon, Garten, See, Kirschbäume, Strandfoto, unsere Bank im Park, Pflanze, Wohnzimmer streichen, Jahrestag vergessen, Zeitverschiebung. Am stärksten betroffen ist encouragement (13 von 30), die Kategorie lädt zu konkreten Anlässen ein.
- Zweithäufigster Mangel: gesättigte Motive. Kaffee kam 8x neu vor, Zettel, Sprachnachricht, Pizza, Tee/Balkon, Pulli- und Kissen-Duft. Ein Motiv-Zähler (Stichwort alt gegen neu) vor dem Lesen spart Zeit.
- Beinahe-Dubletten fand difflib > 0,6 zusammen mit gemeinsamen 4-Grammen zuverlässig (z. B. Fahrrad flottgemacht gegen thanks-038, mit jedem Tag sicherer gegen encouragement-046, zum Lachen gebracht gegen thanks-013).
- Die eigenen Ersatzsätze erzeugen sofort neue Dubletten. In dieser Runde waren es 14 von 71 im ersten Entwurf, etwa "Ich liebe dich, Schatz, einfach so" (love-010), "Daumen drücken" (encouragement-013), "Egal was heute passiert, am Abend warte ich" (encouragement-033), "Dein Tempo" (encouragement-060). Deshalb nach jedem Ersetzen die Ähnlichkeitsprüfung erneut laufen lassen, bis nichts mehr auffällt. Das waren hier drei Runden.
- Auf Satzbau-Serien in neuen Sätzen achten. Dreimal "Du hast heute so …, Schatz," kam vor, außerdem "Decke" dreimal und "gemütlich" in Häufung.
- Kosename: Ersatzsätze verschieben den Anteil. Je Kategorie nachzählen. In love stieg er auf 11/30 und musste wieder auf 9 sinken.
- Mein Zusatz-Wortcheck mit Präfix-Treffer (" ne", " würd") meldet würde/neben/neue fälschlich. Kurzformen nur als ganzes Wort prüfen.
- Skripte: `texter_r2_sim.py <datei>` (Ähnlichkeit plus 4-Gramm gegen den Gesamtbestand), `texter_r2_check.py` (Snapshot-Vergleich: alte ids/Texte unverändert, Format byte-gleich, Ton-Stämme, Emoji-Position, Kosename und Opener je Kategorie), `texter_r2_changes.json` (id: Status, neuer Text, Grund). Alles liegt im Scratchpad.
