# Google Play – Release-Checkliste

Listings: `store/listings/en-US/`, `store/listings/de-DE/` (Titel ≤30, Kurzbeschreibung ≤80, Beschreibung ≤4000 Zeichen).

- [ ] Play-Console-Entwicklerkonto (Anforderungen für neue Konten aktuell prüfen, u. a. Closed-Testing-Pflicht)
- [x] Package-Name `app.rpkindwords` (vom Nutzer bestätigt, final)
- [ ] Upload-Keystore erzeugen, außerhalb des Repos sichern, `keystore.properties` lokal (gitignored)
- [ ] Play App Signing aktivieren, `bundleRelease` bauen, Mapping-Datei hochladen
- [ ] Datenschutz-URL: GitHub Pages `docs/datenschutz.html` / `privacy-policy.html`
- [ ] Data-Safety-Formular, Content Rating, Zielgruppe, Anzeigen-Angabe, App-Zugriff
- [ ] Grafiken: Icon 512×512, Feature-Graphic 1024×500, Telefon-Screenshots (en + de)
- [ ] Tests: intern → geschlossen → Produktion

## Grafiken (erzeugt, `store/graphics/`)
- `icon-512.png` (Play-Icon), `icon-1024-master.png`
- `feature-graphic-en.png`, `feature-graphic-de.png` (1024×500)
- `play-screenshots/<en|de>/1..4_*.png` (1200×2400, gerahmt) · Rohscreenshots in `screenshots/`
- Neu erzeugen: `python -I store/tools/make_graphics.py`, `capture_screenshots.py` (Emulator + installierte Debug-App), `frame_screenshots.py`
