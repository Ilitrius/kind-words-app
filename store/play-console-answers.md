# Play Console – Antworten für „App-Inhalte“ (Stand 25.10.2026)

Grundlage: Google-Hilfe „Data safety“ (Erhebung = Übertragung vom Gerät weg; rein lokal verarbeitete Daten müssen nicht angegeben werden) und tatsächlicher Code/Manifest. Du bist für die Richtigkeit verantwortlich – bei neuen Funktionen/SDKs neu prüfen.

Belegt durch das gemergte Release-Manifest: **keine `INTERNET`-Berechtigung**, kein Werbe-ID-Zugriff (`AD_ID` fehlt), keine Analytics-/Werbe-/Crash-SDKs, kein WorkManager. Berechtigungen: nur `POST_NOTIFICATIONS` und `RECEIVE_BOOT_COMPLETED`; Erinnerungen laufen über inexakte Alarme (`setAndAllowWhileIdle`, keine Exact-Alarm-Berechtigung, keine Foreground-Services → keine FGS-Deklaration).

## 1. Datenschutzerklärung
- URL (de): https://ilitrius.github.io/kind-words-app/datenschutz.html
- URL (en): https://ilitrius.github.io/kind-words-app/privacy-policy.html
- Für das Formularfeld eine URL angeben (empfohlen: die englische; die Seite verlinkt auf Deutsch). Seite muss öffentlich erreichbar sein (GitHub Pages: aktiv).

## 2. Data safety (Datensicherheit)
| Frage | Antwort |
|---|---|
| Erhebt oder teilt deine App Nutzerdaten? | **Nein** (nichts verlässt das Gerät) |
| Verschlüsselung bei der Übertragung / Löschanfrage | werden nicht abgefragt, wenn „Nein“ gewählt ist |
| Datentypen | keine ausgewählt |
| Hinweis | Spenden-Link öffnet den Browser; die Zahlung verarbeitet der externe Anbieter direkt (laut Google ausgenommen, App greift nicht darauf zu). Teilen läuft nutzerinitiiert über den Android-Teilen-Dialog. |
| Sicherheitsüberprüfung/Badges | nicht nötig |

## 3. Inhaltsfreigabe (IARC-Fragebogen)
- E-Mail für IARC: rene.puf@gmail.com
- Kategorie: **Dienstprogramm, Produktivität, Kommunikation oder sonstige App** (die App ist kein Spiel, kein Social/Dating, kein Messenger).
- Gewalt, Blut, Angst, sexuelle Inhalte, Nacktheit, Kraftausdrücke/Obszönes: **Nein**
- Drogen, Alkohol, Tabak (Darstellung/Verweis): **Nein**
- Glücksspiel, simuliertes Glücksspiel, Käufe von Zufallsinhalten: **Nein**
- Nutzer-zu-Nutzer-Interaktion in der App / Austausch von Inhalten / Chat: **Nein** (kein Konto, keine App-Kommunikation; Teilen läuft über das System-Teilen an andere Apps)
- Standort mit anderen teilen: **Nein**
- Digitale Käufe: **Nein** (Spende ist freiwillig, nichts wird freigeschaltet, läuft extern im Browser)
- Offene Web-/Internetinhalte: **Nein**
- Erwartetes Ergebnis: niedrigste Altersstufe (z. B. USK 0 / PEGI 3 / Everyone). Das Alter der Zielgruppe (13+) ist davon getrennt und wird unter „Zielgruppe“ gesetzt.
- Grenzfall: Falls der Fragebogen „Teilen von Nutzerinhalten“ so versteht, dass das System-Teilen zählt, ehrlich „Ja, nur an andere Apps über System-Teilen“ wählen; das ändert die Einstufung kaum.

## 4. Zielgruppe und Inhalt
- Altersgruppen: **13–15, 16–17, 18+** (alle unter 13 abwählen)
- Spricht die App Kinder unter 13 an: **Nein**; Aufmachung/Inhalte nicht kindgerecht beworben (keine Comic-Figuren o. Ä.)
- Damit gilt die Families-Richtlinie nicht; Teilnahme am „Für Familien“-Programm: Nein.

## 5. Weitere Deklarationen
| Bereich | Antwort |
|---|---|
| Werbung | **Keine Werbung** |
| App-Zugriff | **Alle Funktionen ohne Anmeldung/Sonderzugang nutzbar** |
| Werbe-ID | **Nein** (App nutzt sie nicht; `AD_ID` nicht im Manifest) |
| Finanzfunktionen / Kredite / Krypto | Nein |
| Gesundheits-Apps | Nein |
| Nachrichten-App / COVID-19 / Behörden-App | Nein |
| Kontolöschung | nicht zutreffend (keine Konten) |
| Datenschutz-/Spenden | Spenden-Link extern, freiwillig; falls Google nachfragt: kein Zugang zu Funktionen, keine digitalen Güter |
| Kategorie im Store | z. B. „Lifestyle“ oder „Soziales/Beziehungen“; Tags frei wählbar |
| Kontakt-E-Mail (öffentlich) | rene.puf@gmail.com |
| Ausrichtung | Deutsch (de-DE) und Englisch (en-US); Standardsprache festlegen |
| Preis | Kostenlos (später nicht auf kostenpflichtig änderbar) |

## 6. Hinweise zum Ablauf
1. Console: App erstellen (Name „Kind Words“, Standardsprache, App, kostenlos).
2. „Richtlinie > App-Inhalte“: Abschnitte 1–5 ausfüllen und absenden.
3. Store-Eintrag: Texte aus `store/listings/`, Grafiken aus `store/graphics/`.
4. Release: Play App Signing akzeptieren, `app-release.aab` und `mapping.txt` hochladen, zunächst interne Tests. Für neue persönliche Entwicklerkonten gelten ggf. Pflicht-Testphasen (geschlossener Test mit Mindestzahl Tester über Mindestdauer) – aktuelle Anforderungen im Konto prüfen.
5. Änderungen am Verhalten (Internet, SDKs, Werbung) → Datenschutzerklärung, Data safety und diese Datei zuerst aktualisieren.
