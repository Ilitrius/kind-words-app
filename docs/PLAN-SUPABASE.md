# Plan: Sätze aus Supabase (Entwurf, 2026-11-26)

Ziel: Sätze ohne App-Update pflegen und erweitern. Die App bleibt offline-fähig; die gebündelten Dateien bleiben der Rückfall.

## Ist-Stand (geprüft)
- Supabase-Projekt `kind-words-app` (ref `jqpmwwjffmtlhrnyatlc`, eu-west-1) ist angelegt, Datenbank leer, kein Code in der App.
- Sätze: `assets/messages/messages_{de,en}.json` (`language`, `version` = 11, `messages[{id,category,text}]`).
- Abgleich existiert schon: `ContentParser` -> `ContentSync.plan` (Insert/Update/Delete, Favoriten und `hidden` bleiben, USER-Sätze unberührt) -> `MessageRepository.ensureSeeded()`, gesteuert über `ContentSource` und `contentVersion(lang)` in DataStore.
- Favoriten hängen an stabilen ids (`de-love-001`), im Backup als id-Liste.
- Vorlesen: mitgelieferte mp3 (`assets/voice/<lang>/<id>-<texthash>.mp3`); bei geändertem Text passt der Hash nicht mehr, dann fällt die App auf die Gerätestimme zurück.

## Kernidee: kein neuer Datenpfad
Supabase wird nur eine zweite `ContentSource`. Der gesamte Abgleich (ids, Favoriten, Version) bleibt unverändert. Ablauf pro Sprache: Remote-Version prüfen -> nur bei höherer Version den Inhalt laden -> durch denselben `ContentParser`/`ContentSync` schicken. Fehler, Timeout oder kein Netz = still weiter mit dem Bestand; die Assets seeden wie bisher bei Erstinstallation.

## Datenbank
```
content_version(language text pk, version int not null, updated_at timestamptz)
messages(id text pk, language text not null, category text not null,
         text text not null, sort int, active bool default true)
```
- RLS an, Policy nur `select` für `anon`; Schreiben nur über Dashboard/Service-Key (nie in der App oder im Repo).
- Gelöschte Sätze: `active = false` (App entfernt sie wie bisher; Favoriten bleiben erhalten).
- Version wird erst nach vollständigem Import hochgesetzt (ein Update-Statement), damit die App nie halbe Stände lädt.
- Import einmalig aus den beiden JSON-Dateien (Skript in `tools/`, ids unverändert übernehmen).

## Stand Datenbank (2026-11-26, umgesetzt)
- Migration `content_schema` angewandt: Tabellen, RLS, `anon` nur `select` (Schreiben als anon liefert 401, geprüft).
- Import v11 erledigt: de 1250 + en 1250 Sätze aktiv, `content_version` = 11/11; 12 Kategorien, keine Leerzeichen-Reste, Sicherheits-Advisors ohne Befund.
- URL `https://jqpmwwjffmtlhrnyatlc.supabase.co`, Lesen per REST mit dem publishable Key (öffentlich gedacht, siehe Supabase-Dashboard).
- **Falle:** PostgREST liefert höchstens 1000 Zeilen je Anfrage; je Sprache sind es 1250. Die App muss in Seiten laden (`Range`/`limit`+`offset`, sortiert nach `sort`) und erst nach vollständigem Laden abgleichen.
- Neuer Import: `tools/content_to_sql.py` erzeugt SQL-Chunks aus den JSON-Dateien (Chunks zuerst, Version zuletzt). Der erste Import lief direkt aus der JSON-Datei auf GitHub `main` per `http`-Erweiterung (danach wieder entfernt).

## App-Seite
- Zugriff über die REST-API mit `HttpURLConnection` oder OkHttp (nur falls schon im Projekt); kein supabase-kt, keine neue große Abhängigkeit. URL + anon-Key stehen im Build (publishable Key, öffentlich gedacht, aber nur Lesen dank RLS).
- Aufruf höchstens einmal pro Tag und beim App-Start, im Hintergrund, kurzer Timeout. Kein Polling, kein Worker.
- Parser bleibt tolerant (kaputte Einträge überspringen). Plausibilitätsgrenzen (Anzahl, Textlänge), damit ein Fehlstand in der DB nicht alles löscht: Wenn die Remote-Menge stark unter dem Bestand liegt, ablehnen.
- Tests: Remote-JSON -> `ContentFile`, Versionsvergleich, Netzfehler = Bestand bleibt, Favoriten überleben Textänderung und Deaktivierung.

## Datenschutz und Store (Pflicht vor dem Release)
- Neu: App ruft zur Laufzeit eine Supabase-URL ab. Übertragen wird technisch IP-Adresse und Anfrage; keine Nutzerdaten, keine Gerätekennung, keine Favoriten.
- `docs/` (Datenschutz de/en, Über uns) anpassen: Supabase als Auftragsverarbeiter (Region EU), Zweck, Rechtsgrundlage, Hinweis, dass die App auch ohne Abruf funktioniert.
- Play Data Safety prüfen: Es werden keine Nutzerdaten gesammelt/geteilt; Eintrag nur ändern, wenn die Formulare es verlangen (Agent `legal-docs` + `play-release`).
- AdMob-Einwilligung (UMP) bleibt getrennt; der Satzabruf ist kein Werbezweck und braucht keine Einwilligung, wird aber in der Datenschutzerklärung genannt.

## Offene Entscheidungen
1. **Entschieden (2026-11-26):** Supabase ist nur Update-Kanal über den Assets; Assets bleiben Rückfall und Erststart.
2. Soll die Stimme für neue/geänderte Sätze mitgeführt werden? Aktuell nur 670 Sätze je Sprache haben eine Aufnahme; Remote-Sätze sprechen mit Gerätestimme, bis `tools/gen_voice.py` und die ElevenLabs-Lizenz geklärt sind. Alternative später: mp3 in Supabase Storage.
3. Kategorien: nur bestehende oder auch neue per Remote? Neue Kategorie braucht UI-Strings im App-Update, daher zunächst nur bestehende.
4. Content-Pflege: Dashboard-Tabelle reicht, oder Import-Skript aus den JSON-Dateien als Quelle der Wahrheit? Empfehlung: JSON im Repo bleibt Quelle, Skript pusht nach Supabase.

## Schritte
1. Entscheidungen 1 bis 4 klären.
2. Schema + RLS per Migration anlegen, Import-Skript, Daten laden, mit Advisors (Sicherheit) prüfen.
3. `RemoteContentSource` + Tageslimit + Plausibilitätsprüfung + Tests (`android-dev`).
4. Datenschutz/Über uns/Data Safety anpassen (`legal-docs`, `play-release`).
5. Prüfung (`qa-tester`): Flugmodus, schlechtes Netz, Version höher/gleich, Favoritenerhalt, Lint, Release-Build mit R8.
6. Release (neue Version, Build hochzählen), BRAIN/TODO/Obsidian nachziehen.
