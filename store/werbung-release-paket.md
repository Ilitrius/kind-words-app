# Werbung: Texte und Schritte für den Release (Entwurf, 2026-11-23)

**Nicht live schalten, bevor der Werbe-Release veröffentlicht wird.** `docs/` ist GitHub Pages und geht beim Merge sofort live; die Texte unten deshalb erst am Release-Tag in `docs/`, `store/listings/` und die Console übernehmen. Der Code (Banner, Einwilligung, Einstellungen, Test-IDs) liegt auf dem Branch `ccr-345fd84b-1leqix` und darf erst nach Ende der AdMob-Kontosperre und mit echten IDs in `main`.

## 1. Datenschutzerklärung (de, Textbausteine; en analog)
- **Kurzfassung:** "Die App zeigt ein dezentes Werbebanner über Google AdMob (im EWR/UK/Schweiz nur nach Ihrer Einwilligung, siehe Abschnitt 12f). Ihre Sätze, Favoriten und Einstellungen werden nicht übertragen. Keine Analyse- oder Tracking-Dienste, kein Benutzerkonto." Den Satz "Die App besitzt kein Werbung ..." und die Aussagen "keine Werbung/kein Tracking" in Kurzfassung, Abschnitt 5, 10 und 12d ersetzen.
- **Abschnitt 4 (Berechtigungen):** ergänzen: "Für die Werbung nutzt die App Netzwerkzugriff (Internet, Netzwerkstatus), die Werbe-ID (AD_ID) und eine Berechtigung zum Lesen des einfachen Telefonstatus (READ_BASIC_PHONE_STATE, vom Werbe-SDK mitgebracht). Ihre Sätze, Favoriten und Einstellungen werden dabei nicht übertragen."
- **Abschnitt 5:** "Die App zeigt ein Werbebanner von Google AdMob (Google Ireland Limited, Gordon House, Barrow Street, Dublin 4, Irland / Google LLC, USA). Zur Auslieferung und Messung kann Google insbesondere Ihre IP-Adresse, Geräteinformationen, die Werbe-ID und Interaktionen mit der Anzeige verarbeiten und Informationen auf Ihrem Gerät speichern oder auslesen; eine Übermittlung in Drittländer (z. B. USA) ist möglich. Ihre Sätze, Favoriten und Einstellungen werden nicht an AdMob übermittelt. Die Anzeigen sind auf Inhalte bis zur Stufe 'Teen' begrenzt."
- **Neuer Abschnitt 12f "Werbung und Einwilligung":** "In den ersten drei Tagen nach der Installation zeigt die App keine Werbung und fragt keine Einwilligung ab. Danach: Im EWR, im Vereinigten Königreich und in der Schweiz wird Werbung erst angefragt, nachdem Sie im Google-Einwilligungsformular (Google User Messaging Platform) Ihre Auswahl getroffen haben. Rechtsgrundlage ist Ihre Einwilligung (Art. 6 Abs. 1 lit. a DSGVO, § 25 TDDDG). Sie können sie jederzeit mit Wirkung für die Zukunft ändern oder widerrufen: Einstellungen, 'Werbe-Datenschutzeinstellungen'. Ohne Einwilligung wird keine personalisierte Werbung angezeigt. Die Werbe-ID können Sie in den Android-Einstellungen zurücksetzen oder löschen. Weitere Informationen: policies.google.com/technologies/ads. Die Werbung erscheint nur auf den Bildschirmen 'Vorschläge' und 'Favoriten', nie im Widget, in Benachrichtigungen oder in den Einstellungen."
- **Abschnitt 10 (Alter):** "Die App ist für Nutzerinnen und Nutzer ab 16 Jahren vorgesehen. Sie enthält keine nicht jugendfreien Inhalte, ein Werbebanner (siehe Abschnitt 12f) und keine Kommunikation mit anderen Nutzern."
- **Rechtsgrundlage (Abschnitt 7):** Verweis auf Abschnitt 5/12f für die Verarbeitung durch AdMob.
- Stand-Datum anpassen. Auch `docs/about.html` und `docs/ueber-uns.html` ("Keine Werbung, kein Tracking, kein Konto") anpassen: "Kostenlos, mit dezentem Banner, ohne Konto".

## 2. Store-Texte (de/en)
- `full_description.txt`: Zeilen "Kein Konto, keine Werbung, kein Tracking" ersetzen durch "Kein Konto. Kostenlos, mit einem dezenten Werbebanner (in der EU nur mit deiner Einwilligung)"; "Das Widget ... zeigt keine Werbung" bleibt (stimmt weiter). Absatz "Kind Words ist kostenlos. Wenn es dir gefällt ..." ergänzen: "Die App finanziert sich über ein dezentes Banner".
- Neuigkeiten: "Neu: ein dezentes Werbebanner finanziert die App; in den ersten drei Tagen bleibt sie werbefrei, in der EU nur nach deiner Einwilligung."
- Längen prüfen (Titel <=30, Kurz <=80, Beschreibung <=4000).

## 3. Play Console (am Release-Tag)
- App-Inhalte, **Anzeigen**: "Ja, enthält Werbung". **Werbe-ID**: Ja, Zweck Werbung/Marketing (AD_ID kommt vom SDK).
- **Data Safety** (zusätzlich zur bestehenden optionalen ML-Kit-Angabe): erhoben und geteilt durch Google AdMob: ungefährer Standort (IP), Geräte- oder andere Kennungen (Werbe-ID), App-Interaktionen, App-Leistung/Diagnose; Zweck Werbung/Marketing (+ Analytik, Betrugsprävention); an Dritte geteilt: Ja; verschlüsselt übertragen: Ja; Erhebung nicht optional (Anzeigen). Eigene Daten der App weiter "nicht erhoben".
- **Zielgruppe:** nur 16-17 und 18+ (13-15 abwählen); weiterhin nicht kinderorientiert.
- **IARC-Fragebogen:** Werbung angeben. Kategorie weiterhin niedrig.
- Versionsangabe: z. B. 1.3.0, `mapping.txt` hochladen. Beta-Phase: erst im geschlossenen Test mit Werbung prüfen.

## 4. AdMob-Konsole (erst nach Ende der Kontosperre)
1. Konto-Status prüfen (Sperre beendet?). Sperr-Mail lesen, Auflagen beachten.
2. App "RP Kind Words" (`app.rpkindwords`) anlegen (verknüpfbar, da im Play Store), Banner-Anzeigenblock anlegen; **echte IDs** in `app/build.gradle.kts` (`release`) eintragen (App-ID `ca-app-pub-...~...`, Banner `ca-app-pub-.../...`). Debug bleibt auf Test-IDs.
3. Datenschutz & Mitteilungen: Einwilligungsnachricht EWR/UK/CH (Sprachen de und en, Datenschutz-URL `https://ilitrius.github.io/kind-words-app/privacy-policy.html`) veröffentlichen.
4. Blockierungssteuerung: Anzeigenkategorien für Erwachsene, Dating, Glücksspiel, Alkohol und politische Werbung sperren (die App ist 16+, Beziehungsthema).
5. `app-ads.txt` liegt bereits im Root der Entwickler-Website (`google.com, pub-9574494584284932, DIRECT, f08c47fec0942fa0`); Website in der Play Console hinterlegt.

## 5. Eigene Sicherheit gegen Kontosperre
- Debug-Build für alle Tests (nur Test-IDs). Eigenes Gerät in `res/values/ads.xml` (`admob_test_device_ids`) eintragen: ID erscheint in Logcat beim ersten Anzeigenabruf des Debug-Builds ("setTestDeviceIds"). Dann zeigt es auch im Release nur Test-Anzeigen.
- Nie auf echte Anzeigen klicken (auch nicht Familie, Freunde, Tester bitten), keine Aufforderungen zum Klicken in der App.
- Banner mit Abstand (24 dp plus Innenabstand) zu "Neuer Vorschlag", "Teilen" und Favorit.

## 6. Test-Checkliste (Emulator)
- Debug-Build: erste 3 Tage keine Werbung (Systemzeit/Datenspeicher zum Test: Eintrag `first_run_at` oder App-Daten löschen und Uhr 3 Tage vor stellen); danach Test-Banner auf Vorschläge/Favoriten, nicht auf "Eigene" und "Einstellungen".
- Einwilligungsformular (`DEBUG_GEOGRAPHY_EEA` mit Test-Geräte-ID) und "Werbe-Datenschutzeinstellungen" in den Einstellungen.
- Kein Absturz beim Kaltstart; Release-Build (R8) starten; Widget und Benachrichtigung unverändert ohne Werbung.
- Update von 1.1.0: Favoriten bleiben, werbefreier Beginn gilt ab dem Update.
