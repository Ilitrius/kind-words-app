# Plan: Werbung in Kind Words (Entwurf, 2026-11-23)

Ziel: die App über Werbung mitfinanzieren, ohne den warmen Charakter der App und das Vertrauen der Nutzer zu beschädigen. Grundlage: das erprobte Werbepaket aus RPCT (`rpct-app/docs/werbepaket/`: `README.md`, `AdsController.kt.txt`, `AdBanner.kt.txt`, Rezept `reference-admob-ads-playbook`).

## 0. Voraussetzungen und Stopper (zuerst klären)
1. **AdMob-Kontosperre.** Das AdMob-Konto ist bereits wegen zu vieler Eigenklicks gesperrt (RPCT, Beginn angenommen 22.11.; Erinnerung 21.12.). Die Sperre betrifft das Konto, nicht nur eine App. Kind Words geht mit echten Anzeigen erst live, wenn die Sperre beendet ist und das Konto wieder normal läuft. Bis dahin darf nur mit Googles Test-IDs entwickelt werden. Neue App im selben Konto anlegen: erst nach Ende der Sperre.
2. **Zielgruppe 13+ (Entscheidung nötig).** Aktuell sind 13-15, 16-17 und 18+ gewählt. Werbung für Minderjährige braucht Zusatzregeln (unter 16 im EWR: keine personalisierte Werbung ohne Zustimmung der Eltern; Kennzeichnung per Request-Konfiguration). Einfachster Weg: Zielgruppe auf 16+ (oder 18+) anheben, oder 13+ behalten und dann ausschließlich nicht personalisierte Anzeigen mit Kennzeichnung "unter dem Einwilligungsalter" ausliefern (weniger Ertrag, mehr Aufwand).
3. **Der Datenschutz-Versprechen ändert sich.** Heute versprechen App, Store-Text, Datenschutz und Spendentext "keine Werbung, kein Tracking" (`donate_text`, `donate_prompt_text`, Store-Beschreibung de/en, Datenschutz Abschnitte 4/5/10, about/ueber-uns). Alles muss zusammen mit dem Release geändert werden, sonst Richtlinienverstoß.

## 1. Wo Werbung erscheinen soll (Vorschlag, Entscheidung nötig)
- **Nur ein adaptives Banner** am unteren Rand der Hauptbildschirme "Vorschläge" und "Favoriten". Kein Interstitial, keine Vollbild-Anzeige, keine Belohnungs-Videos, keine Anzeige beim Start.
- **Nie:** im Widget, in der Benachrichtigung, im Onboarding, in den Einstellungen, beim Vorlesen/Teilen-Ablauf.
- **Abstand:** deutlicher Abstand zu "Neuer Vorschlag", "Teilen", Favoritenstern (Play-Richtlinie gegen versehentliche Klicks; außerdem schützt es vor Kontosperre).
- **Sanfter Start:** in den ersten 3 Tagen (oder bis zum ersten Teilen) keine Werbung, damit der erste Eindruck warm bleibt. Aus Nutzersicht freundlich, richtlinienkonform.
- Zusätzlich eine Alternative zum Weglassen: die Spenden-Karte bleibt; ein späteres "Werbefrei"-Angebot (In-App-Kauf) wäre eine eigene Entscheidung (Play Billing, Datenschutz).

## 2. Technik (aus dem RPCT-Paket)
- Next-Gen SDK `com.google.android.libraries.ads.mobile.sdk:ads-mobile-sdk` (laut Paket 1.6.0, vor dem Einbau Version prüfen), UMP kommt mit; Version nur über `gradle/libs.versions.toml`.
- IDs je Build-Typ: **Debug = Test-IDs**, Release = echte IDs per `resValue`. Manifest: `APPLICATION_ID` als meta-data.
- `AdsController` (UMP-Einwilligung, danach `MobileAds.initialize`, Banner erst nach Init) und `AdBanner` (adaptiv); Aufruf in `MainActivity` **nach dem Onboarding**, Banner im `MainScreen`-Scaffold.
- **Datenschutzoptionen** (Pflicht in EWR/UK): Zeile in den Einstellungen, "Werbe-Datenschutzeinstellungen", über `showPrivacyOptionsForm`.
- **Eigene Absicherung gegen Eigenklicks:** das eigene Gerät als Testgerät eintragen (Test-Geräte-ID aus Logcat, `RequestConfiguration`), sodass es auch im Release-Build nur Test-Anzeigen bekommt. Dazu in der Doku: im Release-Build nie auf Anzeigen klicken, auch Bekannte nicht klicken lassen.
- Neue Strings de/en (Lint erzwingt beides); keine hartkodierten Texte.
- Manifest-Merge prüfen: `AD_ID`, `INTERNET`, `ACCESS_NETWORK_STATE`, `WAKE_LOCK`. Die Übersetzungseinwilligung (ML Kit) bleibt getrennt von der Werbeeinwilligung.
- R8-Release, App-Größe und Start prüfen; Ablauf auf dem Emulator: Test-Banner, Einwilligungsformular (`DEBUG_GEOGRAPHY_EEA`), Datenschutzoptionen.

## 3. Rechtliches und Store (zusammen mit dem Release live schalten)
- Datenschutz de/en: neuer Abschnitt "Werbung (Google AdMob) und Einwilligung" (Textbausteine im RPCT-Paket, Abschnitt 4), Berechtigungen, Altersangabe, Aussagen "keine Werbung/kein Tracking" ersetzen; Stand-Datum. Zeitpunkt: erst mit dem Release live (Pages-Seite darf Werbung nicht beschreiben, solange die Live-App keine hat; die bestehende Seite geht mit dem Merge sofort live, deshalb Änderung auf eigenem Branch bis zum Release halten).
- Play Console: Anzeigen "Ja", Werbe-ID "Ja" (Zweck Werbung), Data Safety (ungefährer Standort, Geräte-/andere IDs, App-Interaktionen, Diagnose; erhoben und geteilt), Zielgruppe, IARC-Frage zu Werbung, Store-Texte ohne "keine Werbung".
- AdMob-Konsole: App anlegen, Banner-Block anlegen, Einwilligungsnachricht (EWR/UK/CH, de und en, Datenschutz-URL) veröffentlichen; `app-ads.txt` liegt bereits (`google.com, pub-9574494584284932, DIRECT, f08c47fec0942fa0`), Entwickler-Website in der Console ist gesetzt.
- Themenprüfung: Beziehungs-/Liebesinhalte sind unkritisch für AdMob, aber die Anzeigenkategorien prüfen (z. B. Dating-/Erwachsenenanzeigen blockieren, weil die App ab 13 ist).

## 4. Ablauf und Reihenfolge
1. Entscheidungen aus Abschnitt 0 und 1 (René).
2. Entwicklung auf eigenem Branch `feature/admob` (nur Test-IDs), Tests und Emulator. Kann jetzt beginnen, wird aber noch nicht gemergt.
3. Nach Ende der AdMob-Sperre: Konto prüfen, App und Banner-Block anlegen, echte IDs eintragen.
4. Rechtstexte und Store-Eintrag vorbereiten (Branch), Version (z. B. 1.3.0) bauen, geschlossener Test, dann Veröffentlichung; Datenschutz-Seite und Console-Änderungen am selben Tag.
5. Nach dem Start: Einnahmen, Absturzrate (Play Vitals), Bewertungen beobachten. Wenn die Bewertungen leiden, Banner-Position oder -Verzögerung anpassen.

## 5. Risiken
- **Kontosperre erneut** durch Eigenklicks: Testgerät eintragen, Debug-Build, keine Klicks im Release.
- **Vertrauensverlust:** die App warb mit "ohne Werbung"; Spendentext und Store-Texte ehrlich anpassen ("kostenlos, finanziert sich über ein dezentes Banner").
- **Ertrag:** bei wenigen Nutzern gering; erst mit mehr Nutzern relevant. Keine Zahlen versprechen.
- **Policy:** versehentliche Klicks, Mindestabstand, keine Anzeigen im Widget/Benachrichtigung, Einwilligung vor der ersten Anfrage.
- **Stabilität:** zusätzliches SDK, größerer Start, mehr Berechtigungen; Release-Test mit R8 ist Pflicht.

## Offene Entscheidungen
1. Zielgruppe: 16+/18+ anheben oder 13+ mit nur nicht personalisierter Werbung?
2. Banner nur auf Vorschläge/Favoriten, werbefreier Start (3 Tage): einverstanden?
3. Entwicklung jetzt (Test-IDs, nicht mergen) oder erst nach Ende der Sperre?
