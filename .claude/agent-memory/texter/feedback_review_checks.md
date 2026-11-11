---
name: feedback-review-checks
description: Prüfregeln und häufigste Mängel beim Review fremder Satzentwürfe (KI-Klang, Ton-Regel, Dubletten, Anhang-Verweise, Opener-Häufung)
metadata:
  type: feedback
---

Beim Review von Satzentwürfen anderer Agenten immer automatisch UND manuell prüfen; die automatischen Checks fangen nur einen Teil der Mängel.

**Why:** Review von 480 Entwürfen für Content v7 (2026-11-08): Die Prüfskripte der anderen Agenten übersahen "wahre Liebe" (Floskel), "Lazy" (steht in ContentToneTest `words`, fällt also im Test durch) und "leise" (Ton-Regel-Liste in content/notes.md: leer/leise/empty/quiet). Rund 20 % der Sätze mussten überarbeitet oder ersetzt werden.

**How to apply:**
- Immer gegen alle Wortlisten prüfen: ContentToneTest `stems`/`words` 1:1, zusätzlich Ton-Liste aus `.claude/agent-memory/content/notes.md` und die KI-Floskel-Liste aus dem Agenten-Prompt.
- Emoji: Codepoint-Prüfung bis Unicode 10 / Emoji 5.0, keine ZWJ, keine Hauttöne. Das Skript des Hauptagenten warnt fälschlich bei erlaubten Emoji, die nicht auf seiner Liste stehen (z. B. 🍦).
- Häufigste Mängel bisher:
  - Verweise auf Anhänge, die beim Teilen nicht mitgehen ("Sent you a photo", "Noch ein Lied … Hör mal rein").
  - DE/EN-Übersetzungspaare, etwa "so knapp war" und "a bit short with you".
  - Beinahe-Dubletten zu vorhandenen Sätzen, auch über Kategorien hinweg (z. B. "Du lernst jeden Tag dazu").
  - Gehäufte Satzanfänge: Opener je Kategorie zusammen mit den vorhandenen Sätzen zählen.
  - Slogan- oder Spiegelsätze ("Small apology, big love", "Halfway through the week. Halfway to you.").
  - Füllwörter: DE "richtig"/"ganz", EN "best"/"Honestly"/"seriously".
  - Mehr als 2 Doppelpunkt-Pointen je Kategorie.
  - In der Kategorie `emoji` zu viele Fragen.
- Eigene Ersatzsätze geraten leicht zu lang. Die Längenregel aus notes.md je Kategorie prüfen: 25–75 Zeichen, mindestens 50 % unter 60. Das gilt besonders für DE.
- Ersatzsätze erzeugen neue Ideen-Dubletten innerhalb der Kategorie, z. B. zweimal "etwas in deine Tasche gesteckt" oder ein viertes Essens-Angebot in apology. Die fertige Kategorie deshalb noch einmal als Ganzes lesen.
- Wochentagsbezüge ("It's Wednesday") nur einbauen, wenn der Satz sonst keinen Sinn ergibt. Wochentagsneutral formulieren ist sicherer.
- Vergleiche für emoji/ascii mit den rohen Zeichenketten machen. Ähnlichkeitswerte nach Normalisierung sind dort wertlos (`<3 ~ <3` ergibt 1.00).
- Eingabedateien anderer Agenten können sich während des Reviews ändern. Vorher einen Snapshot mit Hash anlegen und im Ergebnis angeben. Eigene Skripte `texter_*.py` nennen.
- Ergebnis als Tabelle führen (Status ok/überarbeitet/ersetzt, vorher/nachher, Grund) und die Zählung daraus berechnen. Siehe [[project-content-v7]].
- Befunde im EN-Bestand bei v8 (2026-11-11), beim Verfeinern gezielt danach suchen:
  - Germanismen, also aus dem Deutschen übernommene Wendungen: "from the heart" (von Herzen), "saved my day" (statt "saved the day"), "working on you" (statt "working for you"), "I want us to be good", "I want you to feel good".
  - Wörter, die gleichwertig zu den deutschen Verbotswörtern sind: "glowing" entspricht "strahlen" und kann zudem nach Schwangerschaft klingen.
  - Emoji-Sätze in derselben Kategorie, die eine Dublette eines älteren Satzes sind (v5/v6 *-021..027 im Vergleich zu *-001..020, z. B. "Thanks for listening 👂💛").
  - Neckereien wirken als Sachverhalt, ohne dass ein Wort aus der Liste vorkommt: "stole the blanket … Forgiven", "remote control dictatorship", "half as chaotic".
- Geschlechtsneutralität bei Emoji: Kein 💑 💏 👫 👬 👭 💃 🕺 🏃 🙋 💁. Ältere Android-Versionen zeigen sie vermutlich als Mann-Frau-Figuren. Das ist nicht verifiziert, deshalb gilt die Regel als Vorsicht. 💑 stand in emoji-003 und emoji-030.
- Kaomoji, die negativ wirken, findet kein Wortfilter: `>_<`, `o_O`, `-_-`, `T_T`, `;_;`, `¯\_(ツ)_/¯`. Gute neutrale Alternativen: `(^///^)` (verlegen), `(✿◠‿◠)`, `(っ^_^)っ` (Umarmung), `c[_]` (Tasse), `{{{hugs}}}`.
- Ergebnisdateien mit einem Python-Skript bauen. `old` per id aus dem Snapshot holen, nie von Hand abtippen. ascii enthält Backslashes (`\(^o^)/`), die ein von Hand geschriebenes JSON kaputt machen. Spätere Korrekturen mit dem Edit-Tool im Build-Skript machen. Ein Heredoc-Ersetzen mit `\U…`-Escapes scheiterte.
- In thanks beginnt fast alles mit "Thanks for" (17 von 47). Bei neuen Sätzen das Danke in den Satz oder ans Ende stellen. Die Regel "mindestens 50 % unter 60 Zeichen" knapp im Blick behalten, denn genau sie fiel in thanks auf 9/20.
