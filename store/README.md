# Google Play – Release-Checkliste

Listings: `store/listings/en-US/`, `store/listings/de-DE/` (Titel ≤30, Kurzbeschreibung ≤80, Beschreibung ≤4000 Zeichen).

- [ ] Play-Console-Entwicklerkonto (Anforderungen für neue Konten aktuell prüfen, u. a. Closed-Testing-Pflicht)
- [x] Package-Name `app.rpkindwords` (vom Nutzer bestätigt, final)
- [x] Upload-Keystore: `D:AI_KIkind-words-keys` (außerhalb des Repos, SICHERN!), `keystore.properties` lokal (gitignored)
- [x] `bundleRelease` signiert gebaut (`app/build/outputs/bundle/release/app-release.aab`, versionCode 1 / 1.0.0)
- [ ] In der Play Console: Play App Signing aktivieren, AAB hochladen, Mapping `app/build/outputs/mapping/release/mapping.txt` hochladen
- [ ] Datenschutz-URL: GitHub Pages `docs/datenschutz.html` / `privacy-policy.html`
- [ ] Data-Safety-Formular, Content Rating, Zielgruppe, Anzeigen-Angabe, App-Zugriff
- [ ] Grafiken: Icon 512×512, Feature-Graphic 1024×500, Telefon-Screenshots (en + de)
- [ ] Tests: intern → geschlossen → Produktion

## Grafiken (erzeugt, `store/graphics/`)
- `icon-512.png` (Play-Icon), `icon-1024-master.png`
- `feature-graphic-en.png`, `feature-graphic-de.png` (1024×500)
- `play-screenshots/<en|de>/1..4_*.png` (1200×2400, gerahmt) · Rohscreenshots in `screenshots/`
- Neu erzeugen: `python -I store/tools/make_graphics.py`, `capture_screenshots.py` (Emulator + installierte Debug-App), `frame_screenshots.py`
