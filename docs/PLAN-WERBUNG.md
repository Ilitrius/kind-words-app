# Plan: Werbung per AdMob (adaptiver Banner)

Entscheidung (2026-11-23): ein adaptiver Banner unten, kein Interstitial, keine Start-Anzeige. Native Karte und belohnte Anzeige bleiben spätere Optionen.
`app-ads.txt` liegt schon unter https://ilitrius.github.io/app-ads.txt (Publisher `pub-2109805482260954`).

## Nötig vom Nutzer
- AdMob: App für `app.rpkindwords` anlegen (App-ID `ca-app-pub-2109805482260954~…`) und Anzeigenblock "Banner" erstellen (`ca-app-pub-2109805482260954/…`).
- AdMob-App erst nach Veröffentlichung mit Play verknüpfen und `app-ads.txt` prüfen lassen.
- AdMob: Datenschutz-Nachricht (Einwilligung, EWR/UK/CH) im Reiter "Datenschutz und Mitteilungen" anlegen und veröffentlichen, sonst zeigt UMP nichts.
- Entscheiden: Banner auf allen vier Tabs oder nur Home und Favoriten (Empfehlung: Home und Favoriten, nicht Einstellungen und Eigene Sätze).

## Schritt 1 - Code (android-dev)
1. `gradle/libs.versions.toml`: `play-services-ads` und `user-messaging-platform` (neueste stabile Versionen prüfen, `minSdk` 26 passt).
2. Manifest: `<meta-data com.google.android.gms.ads.APPLICATION_ID>`; `MobileAdsInitProvider` per `tools:node="remove"` entfernen, damit das SDK erst nach der Einwilligung startet (wie bei ML Kit). `INTERNET`/`ACCESS_NETWORK_STATE` kommen mit dem SDK.
3. Neues Paket `ads/`:
   - `AdsConsent` (UMP: `requestConsentInfoUpdate` bei jedem Start, Formular bei Bedarf, `canRequestAds()`), danach einmalig `MobileAds.initialize`.
   - `AdBanner` (Compose, `AndroidView` mit `AdView`, adaptive Breite aus `LocalConfiguration`, `DisposableEffect` mit `destroy()`, Pause/Resume per Lifecycle).
4. `MainScreen.kt`: Banner im `bottomBar` direkt über der `NavigationBar` (Column), nur wenn `canRequestAds`, nicht im Onboarding, nicht solange der Spendendialog offen ist; Inhalt rückt nach oben (Scaffold-Padding).
5. Einstellungen: Eintrag "Datenschutzoptionen für Werbung" (UMP `showPrivacyOptionsForm`), nur wenn `privacyOptionsRequirementStatus == REQUIRED`.
6. IDs: Debug = Googles Test-IDs, Release = echte IDs über `buildConfigField`/`manifestPlaceholders` (keine echten IDs im Debug, nie auf echte Anzeigen klicken). Testgerät-ID nur im Debug.
7. Anzeigenanfrage ohne personalisierte Pflicht: Altersangaben nicht setzen (Zielgruppe 13+, kein Kinderprogramm); `tagForChildDirectedTreatment` unset.
8. Akkuschonend: Banner-Aktualisierung in AdMob auf 60 s oder länger, Anzeige pausiert im Hintergrund (Lifecycle).
9. R8: Release-APK prüfen, Keep-Regeln nur wenn nötig.

## Schritt 2 - Texte und Rechtliches (i18n-guardian, legal-docs, texter)
- `donate_text`, `donate_prompt_text` (`values/` + `values-de/`): "ohne Werbung" entfernen/umformulieren.
- `store/listings/*/full_description.txt` Zeile 32 ("keine Werbung, kein Tracking") und Datenschutz-Absatz; ggf. Hinweis "enthält Werbung" (Play zeigt es ohnehin).
- `docs/datenschutz.html`, `privacy-policy.html`, `ueber-uns.html`, `about.html`: AdMob/Google als Werbedienst, Werbe-ID, Einwilligung (UMP), Widerruf in den Einstellungen, Datenarten, Rechtsgrundlage, Drittland (USA).
- `store/play-console-answers.md`: "Werbung: ja"; Data Safety: Geräte-/andere IDs, ungefährer Standort, App-Interaktionen, Diagnose, Leistung; geteilt mit Dritten (Google), Zweck Werbung/Marketing; Werbe-ID-Erklärung: ja, Zweck Werbung.
- `docs/APP-PLAN.md` Pflichtanforderungen (Energie, Datenschutz) und `BRAIN.md` aktualisieren.

## Schritt 3 - Prüfung (qa-tester)
- Debug mit Test-IDs: Banner erscheint, Layout hell/dunkel, große Schrift, Querformat, Foldable (Banner nicht über Notch).
- Einwilligung: EWR-Fall simulieren (`ConsentDebugSettings`, Geografie EEA), Ablehnen -> keine Anzeige/nicht personalisiert, Widerruf in den Einstellungen.
- Release-APK (R8) im Emulator: kein Absturz, Banner lädt mit Test-ID-Variante; Update 1.0.16 -> neue Version, Favoriten bleiben.
- Manifest-Berechtigungen und Data Safety gegenprüfen (`aapt dump permissions`).
- Lint, Unit-Tests (`--rerun-tasks`), Ton-/Texttests.

## Schritt 4 - Release (play-release, Nutzer)
- Version 1.0.17 (Build 17), AAB + `mapping.txt`.
- Nutzer VOR Upload: Data Safety und Werbe-ID-Angabe in der Play Console ändern, Store-Text ersetzen, Datenschutz-Seite live (Pages) prüfen.
- Nach Freigabe: AdMob-App mit Play verknüpfen, `app-ads.txt` in AdMob prüfen (dauert oft Tage).

## Risiken
- Banner kostet Platz auf kleinen Bildschirmen (ca. 50-60 dp).
- Kein Banner ohne Einwilligung im EWR; ohne AdMob-Datenschutznachricht kommt keine Abfrage.
- Policy: keine Anzeigen im Onboarding, keine Banner direkt neben Teilen-/Spenden-Schaltflächen (Fehlklicks).
- Eigene Klicks auf echte Anzeigen können das AdMob-Konto sperren.
