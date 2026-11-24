# Google Play – Release-Checkliste

Listings: `store/listings/en-US/`, `store/listings/de-DE/` (Titel ≤30, Kurzbeschreibung ≤80, Beschreibung ≤4000 Zeichen).

- [ ] Play-Console-Entwicklerkonto (Anforderungen für neue Konten aktuell prüfen, u. a. Closed-Testing-Pflicht)
- [x] Package-Name `app.rpkindwords` (vom Nutzer bestätigt, final)
- [x] Upload-Keystore: `D:AI_KIkind-words-keys` (außerhalb des Repos, SICHERN!), `keystore.properties` lokal (gitignored)
- [x] `bundleRelease` signiert gebaut (`app/build/outputs/bundle/release/app-release.aab`, versionCode 2 / 1.0.0)
- [ ] In der Play Console: Play App Signing aktivieren, AAB hochladen, Mapping `app/build/outputs/mapping/release/mapping.txt` hochladen
- [ ] Datenschutz-URL: GitHub Pages `docs/datenschutz.html` / `privacy-policy.html`
- [x] Antworten für Data Safety, Content Rating, Zielgruppe, Anzeigen, App-Zugriff: `store/play-console-answers.md` (Eintragen in der Console steht aus)
- [ ] Grafiken: Icon 512×512, Feature-Graphic 1024×500, Telefon-Screenshots (en + de)
- [ ] Tests: intern → geschlossen → Produktion

## Grafiken (erzeugt, `store/graphics/`)
- `icon-512.png` (Play-Icon), `icon-1024-master.png`
- `feature-graphic-en.png`, `feature-graphic-de.png` (1024×500)
- `play-screenshots/<en|de>/1..4_*.png` (1200×2400, gerahmt) · Rohscreenshots in `screenshots/`
- Foldable (Pixel Fold, AVD `Fold_API35`, android-35 google_apis), je 6 Bilder pro Sprache in `play-screenshots-fold/<modus>/<en|de>/`: `square` aufgeklappt 1:1 (2400x2400), `43` aufgeklappt 4:3 (2400x1800), `outer` zugeklappt Aussendisplay 10:16 (1500x2400). Play-Vorgabe: je 1x 1:1 und 4:3 aufgeklappt + 1x 10:16 zugeklappt Pflicht; PNG/JPEG, max. 8 MB, Seiten 960-3840 px.
  Neu erzeugen: Display-IDs per `adb shell dumpsys SurfaceFlinger --display-id` (0 = innen 2208x1840, 1 = aussen 1080x2092). Aufgeklappt: `$env:FOLD_DISPLAY='<innen-id>'`, zugeklappt (`adb emu fold`): `$env:FOLD_DISPLAY='<aussen-id>'; $env:FOLD_OUT='screenshots-fold-outer'; $env:FAB='910,1690'`; dann `python -I store/tools/capture_screenshots.py`. Rahmen: `$env:FOLD='square'` (bzw. `43`, `outer`); `python -I store/tools/frame_screenshots.py`
- Neu erzeugen: `python -I store/tools/make_graphics.py`, `capture_screenshots.py` (Emulator + installierte Debug-App), `frame_screenshots.py`
