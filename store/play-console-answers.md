# Play Console – Antworten für „App-Inhalte“ (Stand 25.10.2026)

Grundlage: Google-Hilfe „Data safety“ (Erhebung = Übertragung vom Gerät weg; rein lokal verarbeitete Daten müssen nicht angegeben werden) und tatsächlicher Code/Manifest. Du bist für die Richtigkeit verantwortlich – bei neuen Funktionen/SDKs neu prüfen.

STAND BIS BUILD 11 (ab Build 12 gilt der Nachtrag am Ende dieser Datei, INTERNET ist dann im Manifest): Belegt durch das gemergte Release-Manifest: **keine `INTERNET`-Berechtigung**, kein Werbe-ID-Zugriff (`AD_ID` fehlt), keine Analytics-/Werbe-/Crash-SDKs, kein WorkManager. Berechtigungen: nur `POST_NOTIFICATIONS` und `RECEIVE_BOOT_COMPLETED`; Erinnerungen laufen über inexakte Alarme (`setAndAllowWhileIdle`, keine Exact-Alarm-Berechtigung, keine Foreground-Services → keine FGS-Deklaration).

## 1. Datenschutzerklärung
- URL (de): https://ilitrius.github.io/kind-words-app/datenschutz.html
- URL (en): https://ilitrius.github.io/kind-words-app/privacy-policy.html
- Für das Formularfeld eine URL angeben (empfohlen: die englische; die Seite verlinkt auf Deutsch). Seite muss öffentlich erreichbar sein (GitHub Pages: aktiv).

> **Ab Build 17 (AdMob) gelten die Abschnitte 2 und 5 NICHT mehr in der Form unten, sondern der Nachtrag 2026-11-23 am Dateiende.** Die Tabellen unten zeigen den Stand bis Build 16 und bleiben nur als Verlauf stehen.

## 2. Data safety (Datensicherheit)
| Frage | Antwort |
|---|---|
| Erhebt oder teilt deine App Nutzerdaten? | **Nein** (nichts verlässt das Gerät) |
| Verschlüsselung bei der Übertragung / Löschanfrage | werden nicht abgefragt, wenn „Nein“ gewählt ist |
| Datentypen | keine ausgewählt |
| Hinweis | In-App-Updates (`com.google.android.play:app-update`): Abfrage läuft über die Play-Store-App, die App sendet/erhebt selbst nichts, keine INTERNET-Berechtigung nötig (nach jedem Build im gemergten Manifest prüfen!) – daher weiterhin „Nein“. Spenden-Link öffnet den Browser; die Zahlung verarbeitet der externe Anbieter direkt (laut Google ausgenommen, App greift nicht darauf zu). Teilen läuft nutzerinitiiert über den Android-Teilen-Dialog. |
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

> **Nachtrag 2026-10-27 (Build 6, vor Upload prüfen):** Neu sind die Play-In-App-Update-Bibliothek (`com.google.android.play:app-update`; der Play Store fragt die Version ab, die App sendet keine Daten) und Export/Import per Dateiauswahl (lokal). Data Safety: weiterhin „keine Daten erhoben/geteilt“ – im gemergten Manifest prüfen, ob die Bibliothek `INTERNET`/`ACCESS_NETWORK_STATE` hinzufügt (`aapt dump permissions` am AAB); falls ja, Datenschutz-Abschnitt 4 und diese Datei anpassen.

## 4. Zielgruppe und Inhalt
- Altersgruppen: **13–15, 16–17, 18+** (alle unter 13 abwählen)
- Spricht die App Kinder unter 13 an: **Nein**; Aufmachung/Inhalte nicht kindgerecht beworben (keine Comic-Figuren o. Ä.)
- Damit gilt die Families-Richtlinie nicht; Teilnahme am „Für Familien“-Programm: Nein.

## 5. Weitere Deklarationen
| Bereich | Antwort |
|---|---|
| Werbung | **Ja, die App enthält Werbung** (ab Build 17, AdMob-Banner; siehe Nachtrag ganz unten) |
| App-Zugriff | **Alle Funktionen ohne Anmeldung/Sonderzugang nutzbar** |
| Werbe-ID | **Ja** (ab Build 17; Zweck: Werbung oder Marketing; `AD_ID` kommt mit dem Werbe-SDK ins Manifest) |
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

> **Nachtrag 2026-11-06 (Build 10):** Neu in den Einstellungen: "App bewerten" (öffnet Play Store/Browser) und "Feedback senden" (E-Mail-App, ACTION_SENDTO), plus lokale Einführung beim ersten Start. Keine neue Bibliothek, keine neue Berechtigung, keine Datenerhebung durch die App; Data Safety unverändert "keine Daten". Datenschutz Abschnitt 12a ergänzt. Keine In-App-Review-API (bewusst, siehe Play-Richtlinie zu Bewertungsaufforderungen).

> **Nachtrag 2026-11-08 (Build 11):** Lernen auf dem Gerät (lokale Gewichte aus Teilen/Kopieren/Favorit/Überspringen, DataStore, nicht in der Sicherung) und "Diesen Satz vorschlagen" (E-Mail-Entwurf per ACTION_SENDTO, nutzerinitiiert). Keine neue Berechtigung/Bibliothek, keine Datenerhebung durch die App; Data Safety unverändert. Datenschutz Abschnitte 12b/12c ergänzt.


> **WICHTIG, Nachtrag 2026-11-08 (Build 12, VOR dem Upload erledigen): Vorlesen + Übersetzung (Google ML Kit).**
> - Manifest enthält jetzt `INTERNET` und `ACCESS_NETWORK_STATE` (durch `com.google.mlkit:translate`). Der `MlKitInitProvider` ist per `tools:node="remove"` entfernt: das SDK startet nur nach Einwilligung (Einstellungen → Vorlesen → "Vor dem Vorlesen übersetzen"), nie beim App-Start.
> - **Data Safety muss geändert werden:** "Erhebt deine App Nutzerdaten?" → **Ja** (nur wenn die Nutzerin/der Nutzer die optionale Übersetzung einschaltet). Entwurf aus der ML-Kit-Datenoffenlegung (https://developers.google.com/ml-kit/android-data-disclosure, Stand 14.05.2025), **von dir zu prüfen, du bist für die Angaben verantwortlich**:
>   - Datentypen: *App-Info und -Leistung* (Diagnose / sonstige Leistungsdaten), *Geräte- oder andere Kennungen* (Kennung pro Installation), Geräteinformationen (Hersteller, Modell, Android-Version) als Teil der Diagnose.
>   - Zweck: Analysen / App-Funktionen (Diagnose und Nutzungsanalyse des SDK).
>   - Weitergabe an Dritte: laut Google **nein** (SDK-Daten gehen nicht an Dritte); Verarbeitung durch Google als SDK-Anbieter.
>   - Verschlüsselung bei der Übertragung: **Ja** (HTTPS laut Google).
>   - Datenerhebung ist **optional** (Nutzer kann die Funktion ablehnen); Löschung: Widerruf in der App stoppt das SDK, Daten bei Google nicht löschbar durch die App (Datenschutzerklärung 12d).
> - Sätze und Texte verlassen das Gerät nicht (On-Device-Übersetzung); Modelle laden nur auf Knopfdruck (WLAN-Standard).
> - Datenschutzerklärung de/en Abschnitt 12d, Store-Beschreibung de/en und In-App-Einwilligungstext sind angepasst. Ohne Aktualisierung der Data Safety nicht hochladen (Richtlinienverstoß-Risiko).


> **WICHTIG, Nachtrag 2026-11-23 (Build 17, Werbung per Google AdMob). Gilt ab dem Upload von Build 17 und ersetzt Data Safety, "Werbung" und "Werbe-ID" oben.**
>
> Verhalten laut Plan (`docs/PLAN-WERBUNG.md`): ein adaptiver Banner, nur auf Home und Favoriten, kein Interstitial, keine Anzeige im Onboarding. Einwilligungsabfrage über Google UMP (EWR/UK/CH), Widerruf in den Einstellungen ("Datenschutzoptionen für Werbung"). `MobileAdsInitProvider` ist entfernt, das SDK startet erst, wenn `canRequestAds()` wahr ist. Altersangaben und `tagForChildDirectedTreatment` werden nicht gesetzt; Zielgruppe bleibt 13+, kein Kinderprogramm. Manifest: `INTERNET`, `ACCESS_NETWORK_STATE`, `AD_ID` (Werbe-ID) und ggf. `ACCESS_ADSERVICES_*`-Berechtigungen vom SDK (nach dem Bau mit `aapt dump permissions` am AAB prüfen).
>
> ### Vor dem Upload in der Play Console erledigen (Reihenfolge)
> 1. **App-Inhalte > Werbung:** "Ja, meine App enthält Werbung".
> 2. **App-Inhalte > Werbe-ID:** "Ja, die App nutzt die Werbe-ID"; Zweck: **Werbung oder Marketing** (kein Analyse-/Entwicklerzweck angeben, außer das gebaute Manifest zeigt es anders).
> 3. **App-Inhalte > Datensicherheit** neu ausfüllen (Entwurf unten) und absenden. Erst danach den Build hochladen; das Manifest enthält sonst `AD_ID`, ohne dass es deklariert ist, und der Upload wird blockiert.
> 4. **Zielgruppe:** bleibt 13+; bestätigen, dass die App kein Kinderprogramm ist (Altersgruppen unter 13 abgewählt). Nicht an "Für Familien" teilnehmen.
> 5. **Store-Eintrag:** `store/listings/de-DE` und `en-US` `full_description.txt` ersetzen (neue Datenschutz-Absätze, Hinweis "enthält Werbung"); Play zeigt "Enthält Werbung" ohnehin automatisch.
> 6. **Datenschutz-URL** bleibt; vorher prüfen, dass `datenschutz.html`/`privacy-policy.html` (Stand 23.11.2026, Abschnitt 5b) live auf GitHub Pages sind.
> 7. **AdMob:** Datenschutz-Nachricht (Einwilligung, EWR/UK/CH) unter "Datenschutz und Mitteilungen" anlegen UND veröffentlichen, sonst zeigt UMP nichts; App-ID und Banner-Block-ID für Release eintragen; `app-ads.txt` (https://ilitrius.github.io/app-ads.txt) erreichbar halten. AdMob-App erst nach der Play-Freigabe mit Play verknüpfen.
> 8. Eigene Klicks auf echte Anzeigen vermeiden (Kontosperre); Debug nur mit Test-IDs.
>
> ### Data Safety, Entwurf (von dir zu prüfen, du bist für die Angaben verantwortlich)
> - "Erhebt oder teilt deine App erforderliche Nutzerdaten?" **Ja**. Datenerhebung ist teils optional: Werbedaten in EWR/UK/CH nur mit Einwilligung (Antwort bei "erforderlich/optional": **Nutzer kann wählen**, für die Werbe-Datentypen "optional" wählen, soweit die Console es zulässt; sonst "erforderlich").
> - Verschlüsselung bei der Übertragung: **Ja**. Löschanfrage: Nutzer können keine Löschung der Daten bei Google über die App anfordern; Verweis auf Datenschutzerklärung Abschnitt 5b (Widerruf, Werbe-ID zurücksetzen).
>
> | Datentyp (Kategorie > Typ) | Erhoben | Geteilt | Zweck | Optional |
> |---|---|---|---|---|
> | Geräte- oder andere Kennungen > Geräte- oder andere Kennungen (Werbe-ID, Kennung pro Installation) | ja | **ja, mit Google (AdMob)** | Werbung oder Marketing; (Analysen/Fehlerbehebung für ML Kit, siehe Nachtrag Build 12) | ja |
> | Standort > Ungefährer Standort (aus IP-Adresse abgeleitet) | ja | **ja, mit Google** | Werbung oder Marketing | ja |
> | App-Aktivitäten > App-Interaktionen (Anzeige geladen/angetippt) | ja | **ja, mit Google** | Werbung oder Marketing | ja |
> | App-Info und -Leistung > Diagnose, Absturzprotokolle, sonstige Leistungsdaten (Ladezeiten, Fehler, Geräteinfos) | ja | **ja, mit Google** | Werbung oder Marketing; Analysen | ja |
>
> - Nicht angeben (verlassen das Gerät nie): Sätze, Favoriten, Einstellungen, eigene Texte, Lernen auf dem Gerät.
> - "Geteilt": Google als Werbe-SDK-Anbieter gilt laut Google-Hilfe ("Data safety") als Dritter, mit dem Daten **geteilt** werden, wenn das SDK die Daten nicht ausschließlich im Auftrag der App verarbeitet (bei AdMob der Fall); daher "Geteilt: Ja". Aktuelle Google-Angaben "Datenoffenlegung Google Mobile Ads SDK" (https://developers.google.com/admob/android/privacy/play-data-disclosure) vor dem Absenden gegenprüfen und ggf. Datentypen angleichen.
> - Das ML-Kit-Verhalten aus dem Nachtrag Build 12 bleibt zusätzlich bestehen (Diagnose/Kennung, optional, nur nach Einwilligung); die Zeilen oben decken diese Typen bereits ab.
>
> ### Zielgruppe-/Richtlinien-Risiken
> - Google Play "Families"-Richtlinie greift nur bei Kindern als Zielgruppe; hier 13+. Trotzdem: 13 bis 15-Jährige gehören zur Zielgruppe; die DSGVO-Einwilligung ist in DE erst ab 16 allein wirksam. In der Datenschutzerklärung ist das benannt; Entwickler entscheidet, ob die Zielgruppe auf 16+ angehoben wird (sauberste Lösung, vermeidet das Problem).
> - Ads-Richtlinie: keine Anzeigen im Onboarding, kein Banner neben Teilen-/Spenden-Schaltflächen (Fehlklick-Gefahr).
