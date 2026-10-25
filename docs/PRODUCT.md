# Produkt-Briefing: Kind Words

## Zweck
Die App schlägt nette Worte oder kleine, liebevolle Sätze vor, die man seinem Partner / seiner Partnerin persönlich sagt oder per Messenger schickt. Außerdem erinnert sie – zufällig oder nach Einstellung – am Tag daran, es zu tun.

## Funktionen (MVP)
1. **Vorschlag des Tages / Zufallsvorschlag:** ein Satz groß dargestellt, „Neuer Vorschlag“-Button, Filter nach Kategorie.
2. **Teilen:** Satz per Android-Share-Dialog in jeden Messenger schicken (keine eigene Messenger-Anbindung, keine Berechtigung nötig); Kopieren in die Zwischenablage.
3. **Favoriten** speichern.
4. **Eigene Sätze** hinzufügen, bearbeiten, löschen (Datenbank ist editierbar).
5. **Erinnerungen:** Modus *Zufällig* (z. B. 1–3× pro Tag innerhalb eines Zeitfensters) oder *Feste Uhrzeiten* (Wochentage wählbar); Benachrichtigung zeigt direkt einen Vorschlag mit Aktion „Teilen“. Berechtigung `POST_NOTIFICATIONS` (Android 13+) wird erst bei Aktivierung erfragt.
6. **Sprachen:** UI und Inhalte Deutsch + Englisch (Sprache folgt dem System, per-App-Sprache einstellbar). Architektur erlaubt weitere Sprachen.

## Datenbank der Texte
- Lokal: Room/SQLite. Erststart-Befüllung aus `app/src/main/assets/messages/messages_<lang>.json`.
- Tabelle `messages`: `id` (String, stabil), `language`, `category`, `text`, `source` (`BUNDLED` | `USER`), `favorite`, `hidden`, `updatedAt`.
- Gebündelte Texte werden bei App-Updates per `id` aktualisiert/ergänzt (Content-Version), Nutzertexte (`USER`) und Favoriten bleiben unangetastet.
- Später erweiterbar: Nachladen neuer Pakete (z. B. JSON von GitHub Pages) – dann Datenschutz/Data Safety anpassen (Internetzugriff!).
- **UI-Texte** bleiben in `strings.xml`; die Nettigkeiten sind **Inhalte** und liegen in der Datenbank.

## JSON-Format der Inhalte
```json
{ "language": "de", "version": 1,
  "messages": [ { "id": "de-love-001", "category": "love", "text": "…" } ] }
```
Kategorien: `love`, `appreciation`, `morning`, `goodnight`, `encouragement`, `missyou`, `compliment`, `humor`, `thanks`, `apology`.
Regeln für Texte: kurz (max. ~160 Zeichen, WhatsApp-tauglich), geschlechtsneutral (kein „mein Schatz/Held“ ohne Alternative), keine Namen, keine Floskeln aus Massenzitaten mit Urheberrecht, authentisch statt wörtlich übersetzt, kein Kitsch-Overload, Mischung aus ernst und humorvoll.

## Datenschutz (Ziel)
Alles lokal, kein Konto, keine Werbung, kein Tracking, kein Internet im MVP. Nur Berechtigung: Benachrichtigungen. Teilen läuft über den Android-Share-Dialog (Nutzer wählt Ziel).

## Offen
- Monetarisierung (kostenlos? Spenden-Link wie RPCT?)
- Zielgruppe ab 18 oder alle
- Design/Farben (aktuell warmes Orange, Dynamic Color ab Android 12)
