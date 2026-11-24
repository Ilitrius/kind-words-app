# Maskottchen: der Gecko (Entwurf v2, 2026-11-24)

Ziel: eine kleine, liebe Figur, die die Sätze "überbringt". Sie gibt der App ein Gesicht, ohne Text zu ersetzen (Texte bleiben in `strings.xml`).

## Was die Referenzfotos zeigen (René, 2026-11-24)

Sechs Fotos als Vorbild: Wüstengecko (Namib), weißer Leopardgecko mit Zunge, orange Leopardgecko mit Kopfneigung, Leopardgecko auf Fels, Gecko mit runder Sonnenbrille, Hausgecko an der Wand und Taggecko am Blatt. Gemeinsam wirkt "liebenswert" durch:

| Merkmal | Gesehen bei | Übernahme ins Maskottchen |
|---|---|---|
| **Riesige runde Augen**, dunkel mit Glanzpunkt, goldener Ring | Wüstengecko, Leopardgecko orange | Augen sind das Wichtigste: etwa ein Drittel der Kopfbreite, goldener Ring, ein großer und ein kleiner Glanzpunkt |
| **Dauerlächeln** (Mundlinie bis in die Wange) | Leopardgecko weiß/orange | feine, leicht nach oben gezogene Mundlinie, immer freundlich |
| **Kopf leicht geneigt**, Blick zum Betrachter | orange Leopardgecko | Standardblick: Kopf 10 bis 15 Grad geneigt, schaut nach oben zum Betrachter |
| **Weiche, helle Haut**, rosa Zehen | Wüstengecko, weißer Gecko | rosa Zehenspitzen, rosa Wangen, heller Bauch |
| **Warme Farben**: Aprikose bis Honiggelb, braune Bänder am Schwanz | orange Leopardgecko, Fels | Körper warm, Schwanz mit weichen Bändern |
| **Türkis-Tupfer** am Auge | Leopardgecko auf Fels | kleines türkises Markenzeichen über dem Auge (dezent) |
| **Zunge zum Auge/Mund** | weißer Gecko | Pose "Freude": kleine rosa Zungenspitze |
| **Kleines Accessoire** wirkt sofort witzig | Sonnenbrille | Zubehör-Set: Sonnenbrille, Schlafmütze, Herz, Stift |
| **Spähen über Kante / Klettern** | Taggecko am Blatt, Hausgecko | Pose "lugt über die Kante" für Titelzeile und Widget-Rand |

Nicht übernehmen: realistische Höcker und Schuppen (zu unruhig bei 40 dp), dünne lange Beine (stattdessen kurz und rundlich), Fotohintergründe.

## Figur

- **Art:** junger Leopardgecko im Plüschstil, freundlich und etwas neugierig, nie frech oder neckend (passt zum Satzstil der App: nichts Neckendes).
- **Name (Vorschlag):** **Kiko**. Kurz, weich, in Deutsch und Englisch gleich sprechbar. Alternativen: Mochi, Pippa. Vor der Entscheidung Namen und Marke kurz prüfen (Play Store, Markenregister).
- **Körper:** rundlich, kurze dicke Beine, breiter Kopf, dicker Schwanz in weicher Kurve (Leopardgeckos speichern Fett im Schwanz, das wirkt niedlich). Hinweis: echte Leopardgeckos können nicht an Wänden haften; im Maskottchen bleiben kleine Zehenpolster als Stilmittel ("klebt" am Rand).
- **Farben (warm, aus den Fotos; passen zum Rose/Orange der App):**
  - Körper: Aprikose `#FFC27A`, Bauch Creme `#FFF1DC`
  - Schwanzbänder und Flecken: Honigbraun `#C98A4B` (nur wenige, weiche Flecken)
  - Augen: sehr dunkles Weinrot `#3A0A1A`, Ring Gold `#F2C94C`
  - Wangen und Zehenspitzen: Rosa `#F28CA5`; Herz in App-Rose `#DE1E5A`
  - Markenzeichen: Türkis `#6EC9D6` als kleine Tupfer über dem Auge
- **Alternative Farbvariante (Taggecko vom Blatt):** Mint `#8FD9A8`. Hebt sich stärker vom Rose ab, wirkt aber weniger wie die Vorbilder. Empfehlung: warme Variante, auf cremefarbenem Untergrund (Karte, Widget) verwenden, damit er sich abhebt.
- **Rolle:** hält ein Herz oder eine Sprechblase und sitzt gern auf Kanten.

## Posen (Mindestsatz)

| Pose | Einsatz in der App |
|---|---|
| 1 Frontal, Kopf geneigt, lächelt | Standardbild; Icon; Einführung Seite 1 (winkt mit einer Hand) |
| 2 Mit Herz in beiden Händen | Logo, Favoriten, Spendenbitte |
| 3 Lugt über eine Kante | Titelzeile, Widget-Rand, Ladezustand |
| 4 Schreibt mit Stift | "Meine Worte" (leerer Zustand) |
| 5 Schläfrig mit Mütze, Mond | Gute-Nacht-Sätze, Dunkelmodus-Grafik |
| 6 Leere Hände, schaut fragend | Leerer Zustand Favoriten, fehlende Sprachdaten |
| 7 Jubelt, Zungenspitze, Konfetti | Store-Grafiken, Spendendank |
| 8 Mit Sonnenbrille | Sommer/Humor-Kategorie, Store-Grafik |

Später möglich: Animation (Blinzeln, Schwanzwedeln) als Lottie oder Compose-Animation; erst nach den Standbildern.

## Prompts für Bildgenerator

Zuerst die **Figurenbeschreibung** festlegen und bei jedem Prompt unverändert voranstellen. Das hält das Aussehen zwischen den Bildern gleich.

**Figurenbeschreibung (Master):**
```
Kiko, a cute chubby baby leopard gecko mascot, soft apricot-orange body with a cream belly, a few soft honey-brown spots and soft bands on the thick tail, very large round dark wine-colored eyes with a thin golden ring and two white highlights, tiny turquoise dots above the eyes, rosy-pink cheeks and pink toe tips, short chubby legs, a gentle permanent smile, head slightly tilted looking up at the viewer, plush-toy proportions, flat vector illustration, soft rounded shapes, thin dark wine-red details, warm pastel palette, centered, plain white background, sticker style, high resolution
```

**Negativ (falls das Werkzeug es erlaubt):**
```
realistic, photo, scary, teeth, sharp claws, rough scales, text, letters, watermark, logo, human hands, extra fingers, dark background, busy background, 3D render
```

**Pro Pose (an den Master anhängen):**

1. Frontal / winkend
   `front view, head tilted, smiling, waving with one hand, other hand on the ground, full body`
2. Herz
   `holding a small glowing coral-pink heart with both hands against its belly, eyes softly happy, full body, front view`
3. Lugt über Kante
   `peeking over a flat ledge edge with only head and two hands visible, curious big eyes looking at the viewer, tail hanging behind`
4. Schreibt
   `holding a tiny pencil and writing on a small note, tip of the tongue visible in concentration, three-quarter view`
5. Schläfrig
   `wearing a little nightcap, half-closed sleepy eyes, hugging a small crescent moon pillow, a few small stars around, soft evening mood`
6. Fragend
   `both hands open and empty, head tilted, gentle curious expression, tiny heart outline in a thought bubble`
7. Jubelnd
   `arms raised in joy, tiny pink tongue tip showing, small confetti in coral and orange, slightly jumping`
8. Sonnenbrille
   `wearing small round gold-rimmed sunglasses, relaxed smile, sitting on a small rock, summer mood`

**Icon-Variante (für Launcher und Titelzeile, nur Kopf):**
```
Kiko the baby leopard gecko, head and shoulders only, head tilted, big golden-ringed eyes, gentle smile, tiny turquoise dots, flat vector, centered on a plain background, app icon style, readable at 48 px
```

## Konsistenz und Qualität

- Erstes Bild mit dem Master-Prompt mehrfach erzeugen und **eines auswählen**; dieses als Referenzbild für alle weiteren Posen mitgeben (Referenzbild/"Character reference", je nach Werkzeug).
- Alle Posen auf derselben Leinwand (z. B. 1024 x 1024), gleiche Figurenhöhe.
- Danach als SVG nachzeichnen (Vectorizer oder von Hand), damit die Grafiken scharf und klein bleiben; als Vector-Drawable oder `ImageVector` einbinden. Rasterbilder (PNG/WebP) nur für Store-Grafiken.
- Vor dem Einbau prüfen: Erkennbar bei 40 dp (Titelzeile)? Gut lesbar auf hellem und dunklem Untergrund?
- Dateien klein halten (die APK ist bereits groß, siehe Stimm-Dateien).

## Rechtliches und Ablage

- **Die Referenzfotos nicht als Referenzbild in den Generator laden**: Sie stammen aus dem Netz (eines trägt ein Dreamstime-Wasserzeichen) und sind urheberrechtlich geschützt. Sie dienen hier nur als Anregung für die Merkmale oben. Als Referenz für weitere Posen nur das **selbst erzeugte** Gecko-Bild verwenden.
- Nutzungsbedingungen des Bildgenerators prüfen: kommerzielle Nutzung erlaubt? Urheberschaft an KI-Bildern ist unsicher; für eine Marke besser eine **eigene Nachzeichnung** oder Illustratorin/Illustrator beauftragen und Rechte schriftlich festhalten.
- Keine bekannten Figuren als Vorbild nennen (z. B. Geico-Gecko), um Ähnlichkeit zu vermeiden.
- Datenschutz unverändert: reine Grafik, keine Daten. Im Store-Listing später "Maskottchen" erwähnen.
- Ablage: Originale in `store/graphics/mascot/` (Quellen), App-Grafiken in `app/src/main/res/drawable/`.

## Einbau (Reihenfolge)

1. Entwurf und Prompts freigeben, Namen wählen, Farbvariante (warm oder mint) entscheiden.
2. Posen 1 und 2 erzeugen; Titelzeilen-Symbol und Einführung testen.
3. Restliche Posen, leere Zustände, Widget-Vorschau.
4. Neues App-Symbol entscheiden (Gecko-Kopf in der Sprechblase oder wie bisher) und Store-Grafiken anpassen.

## Erzeugt (2026-11-24)

- Pose 1 (frontal, winkend) mit imagine, Modell `imagine-art-2.0` (Free-Plan; das Standardmodell nano-banana-pro ist nicht freigeschaltet). Datei: `store/graphics/mascot/kiko_pose1_v1.jpg` (2048 x 2048, weißer Grund). Der Aufruf mit `count: 3` lieferte nur ein Bild.
- Nächste Posen: dieses Bild als Referenz (`image_url`) mitgeben. Nutzungsrechte des Free-Plans vor kommerzieller Nutzung prüfen (siehe Abschnitt Rechtliches).
- Pose 2 (Herz) erzeugt: `store/graphics/mascot/kiko_pose2_herz_v1.png` (1536 x 1536), mit Pose 1 als Referenz. Abweichungen: ein Auge zwinkert, Zungenspitze sichtbar, Hände sehen etwas zackig aus. Poses 3 (Kante) und 4 (Stift) offen: Free-Plan hat 0 Credits (2026-11-24).

## Entscheidung (René, 2026-11-24)

- **Pose 1 und 2 sind die imagine-Bilder** (`kiko_pose1_v1.jpg`, `kiko_pose2_herz_v1.png`) und gelten als Vorlage für den Stil.
- **Alle weiteren Posen zeichnet Claude als Vektor** im gleichen Stil: `store/graphics/mascot/build_posen.py` erzeugt `kiko_pose3_kante.svg` bis `kiko_pose8_brille.svg` und `posen_vorschau.html`. Die älteren Vektor-Entwürfe (`entwuerfe.html`) sind damit überholt.
- Offen: Pose 1 und 2 ebenfalls als Vektor nachzeichnen (für Android), Feinschliff der Posen 3 bis 8, Umwandlung in Vector-Drawables.

## Export für die App (2026-11-24)

- `store/graphics/mascot/export/`: alle 8 Posen als 512 x 512 WebP mit transparentem Hintergrund (20 bis 41 KB je Datei, zusammen etwa 250 KB): `kiko_pose1_winken`, `kiko_pose2_herz`, `kiko_pose3_kante` bis `kiko_pose8_brille`.
- Pose 1 und 2 stammen aus den imagine-Bildern (weißer Grund per Flood-Fill entfernt, geprüft auf hellem und dunklem Untergrund). Pose 3 bis 8 sind die SVGs, im Browser gerendert (kein SVG-Rasterizer installiert).
- Noch nicht in `app/src/main/res/` kopiert: dafür `drawable-nodpi/` verwenden und die Dateinamen kleinschreiben mit Unterstrich (z. B. `kiko_pose1_winken.webp`).

## Eingebaut (2026-11-24, Branch feature/nav-slide-title-icon)

- WebP-Dateien in `app/src/main/res/drawable-nodpi/` (`kiko_pose1_winken` bis `kiko_pose8_brille`), Composable `Mascot` in `ui/Mascot.kt` (dekorativ, ohne Beschreibung).
- Einführung: Seite 1 Pose 1 (winkt), Seite 2 Pose 2 (Herz), Seite 3 Pose 7 (jubelt), je 200 dp.
- Leerer Zustand Favoriten: Pose 6 (fragend); "Meine Worte" leer: Pose 4 (Stift), je 160 dp.
- Geprüft auf dem Emulator (Pixel API 35, Debug-Build, Englisch); Dunkelmodus, Deutsch und kleine Displays noch nicht.
- Noch ohne Einsatz: Pose 3 (Kante), 5 (Nacht), 8 (Brille). Titelzeile und App-Symbol bleiben vorerst wie bisher.
- Titelzeile (alle Bildschirme mit `ScreenTitle`): rechts Pose 2 (`MascotOffering` in `ui/Mascot.kt`), 56 dp. Das Herz wird "nach vorn gestreckt" durch Wachsen der ganzen Figur um 24 % (Ankerpunkt unten Mitte, 0,8 s), 0,4 s halten, 0,8 s zurück, 0,6 s Pause. Bei ANIMATOR_DURATION_SCALE = 0 bleibt das Bild ruhig. Grenze: Die Figur ist ein Rasterbild, Arme und Herz bewegen sich nicht einzeln. Echte Armbewegung ginge nur mit einer Vektorversion mit getrenntem Herz.
