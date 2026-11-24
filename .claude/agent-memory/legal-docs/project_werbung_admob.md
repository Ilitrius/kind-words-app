---
name: project-werbung-admob
description: Rechtstexte für AdMob-Banner (Build 17): was die Seiten behaupten, welche Annahmen der Code einhalten muss, offene Rechtsrisiken
metadata:
  type: project
---

Stand 2026-11-23: datenschutz.html / privacy-policy.html (Abschnitt 5b, Anker `#werbung` / `#ads`), ueber-uns/about, Store-Beschreibungen und `store/play-console-answers.md` (Nachtrag am Ende) beschreiben AdMob-Banner (nur Home + Favoriten), UMP-Einwilligung EWR/UK/CH, Widerruf "Datenschutzoptionen für Werbung", Datenarten, Drittland USA, Art. 6 Abs. 1 lit. a DSGVO + § 25 TDDDG.

**Why:** Ads kommen mit Build 17; vorher hieß es überall "keine Werbung".

**How to apply:**
- Annahmen in den Texten, die der Code einhalten muss: kein Altersflag, SDK erst nach `canRequestAds()`, Menüeintrag nur wenn UMP ihn verlangt, keine Banner außer Home/Favoriten. Bei Abweichung Texte anpassen.
- Beim Prüfen der Seiten immer auch auf veraltete Aussagen suchen: "keine Internetberechtigung" stand bis 2026-11-23 fälschlich in Abschnitt 4 (seit Build 12 falsch); "10 Kategorien" (jetzt 12); TTS-Absatz nannte nur Gerätestimme (seit 1.0.16 gebündelte Aufnahmen).
- Offene Rechtsrisiken (beim Nutzer): Einwilligungsalter DE 16 vs. Zielgruppe 13+ (sauberste Lösung 16+); BayLDA-Anschrift und Google-Links von mir aus dem Gedächtnis, vor Veröffentlichung prüfen; Rechtsgrundlage lit. f für die UMP-Statusabfrage ist meine Einschätzung.
- Nutzerhinweis immer: keine Rechtsberatung, vor Veröffentlichung prüfen.
