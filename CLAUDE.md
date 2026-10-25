# Kind Words App – Projektregeln

Android-App (Kotlin, Jetpack Compose, Material 3). Package `app.kindwords`, compileSdk/targetSdk **36**, minSdk 26.
Sprachen: **Englisch (Standard, `values/`) und Deutsch (`values-de/`)**. Zweck/Funktionen der App: siehe `docs/PRODUCT.md` (wird ergänzt).

Wissen: `BRAIN.md` (Entscheidungen/Fakten, beim Start lesen, laufend pflegen) · Aufgaben: `TODO.md` (Status aktuell halten).

## Harte Regeln
- **Keine statischen Texte im Code.** Jeder sichtbare Text liegt in `strings.xml` (beide Sprachen) und wird per `stringResource()` / `getString()` genutzt. Lint (`HardcodedText`, `MissingTranslation`, `ExtraTranslation`) bricht den Build bei Verstößen ab.
- Neue Strings immer gleichzeitig in `values/` und `values-de/`.
- Secrets (Keystore, `keystore.properties`, Service-Account-JSON) niemals committen (siehe `.gitignore`).
- Abhängigkeiten nur über `gradle/libs.versions.toml`.
- Datenschutzerklärung / Über-uns / Impressum liegen in `docs/` (GitHub Pages) und müssen zum realen App-Verhalten (Berechtigungen, SDKs, Datenfluss) und zum Play-Console-Data-Safety-Formular passen.
- Play-Store-Texte liegen in `store/listings/<locale>/` (Titel ≤30, Kurzbeschreibung ≤80, Beschreibung ≤4000 Zeichen).

## Befehle
- Build: `.\gradlew.bat assembleDebug` · Lint: `.\gradlew.bat lintDebug` · Release-Bundle: `.\gradlew.bat bundleRelease`
- SDK: `ANDROID_HOME=D:\Android\Sdk` (`local.properties` ist gitignored), JDK 17.

## Agenten (`.claude/agents/`)
`android-dev`, `i18n-guardian`, `play-release`, `legal-docs`, `qa-tester`. Jeder hat persistentes Projekt-Gedächtnis (`memory: project` → `.claude/agent-memory/<name>/`).

### Lern- und Zusammenarbeitsprotokoll (für alle Agenten)
1. **Start:** eigenes Memory und `.claude/agent-memory/SHARED.md` lesen.
2. **Ende jeder Aufgabe:** Gelerntes (Fallstricke, funktionierende Befehle, Entscheidungen, Korrekturen des Nutzers) in das eigene Memory schreiben; Erkenntnisse, die andere Agenten betreffen, zusätzlich in `SHARED.md` (ein Eintrag = Datum, Thema, Regel, Warum).
3. **Übergabe:** Ergebnis immer mit Format *Was getan · Dateien · offen/Risiken · Hinweis an Agent X* zurückgeben.
4. Veraltete oder falsche Einträge löschen statt anhäufen.
5. Delegation: UI/Logik → `android-dev`; Strings → `i18n-guardian`; Verteilung/Store → `play-release`; Rechtstexte/Webseiten → `legal-docs`; Tests/Verifikation → `qa-tester`.
