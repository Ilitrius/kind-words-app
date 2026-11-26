# Erzeugt die Kiko-Posen 3 bis 8 als SVG (Stil wie die imagine-Bilder Pose 1 und 2).
# Aufruf: python build_posen.py  ->  kiko_poseN_*.svg und posen_vorschau.html
import pathlib

OUT = pathlib.Path(__file__).parent
LINE, BODY, BELLY, SPOT = "#7A2A3D", "#FFC27A", "#FFF1DC", "#E0955A"
PINK, TEAL, EYE, RING, ROSE = "#F28CA5", "#4ECDC4", "#5E1220", "#E3B866", "#DE1E5A"
OW = 3.6  # Konturstärke

STYLE = f"""<style>
.o{{stroke:{LINE};stroke-width:{OW};stroke-linejoin:round;stroke-linecap:round}}
.n{{stroke:none}}
</style>"""


def arm(p0, c, p1, hand=True, toes=True):
    """Arm als dicke Linie von p0 über Kontrollpunkt c nach p1, mit Hand."""
    d = f"M{p0[0]} {p0[1]} Q{c[0]} {c[1]} {p1[0]} {p1[1]}"
    s = (f'<path d="{d}" fill="none" stroke="{LINE}" stroke-width="36" stroke-linecap="round"/>'
         f'<path d="{d}" fill="none" stroke="{BODY}" stroke-width="29" stroke-linecap="round"/>')
    if hand:
        s += hand_at(p1, toes)
    return s


def hand_at(p, toes=True):
    """Weiche Pfote: runder Ballen mit drei Zehenrundungen."""
    x, y = p
    s = ""
    for dx, dy in ((-10, -13), (0, -17), (10, -13)):
        s += f'<circle cx="{x + dx}" cy="{y + dy}" r="7.5" fill="{BODY}" class="o" stroke-width="3"/>'
    s += f'<circle cx="{x}" cy="{y}" r="16.5" fill="{BODY}"/>'
    s += f'<path d="M{x - 16.5} {y - 5} A16.5 16.5 0 1 0 {x + 16.5} {y - 5}" fill="none" class="o" stroke-width="3"/>'
    if toes:
        for dx, dy in ((-10, -13), (0, -17), (10, -13)):
            s += f'<circle cx="{x + dx}" cy="{y + dy - 3}" r="3.2" fill="{PINK}"/>'
    return s


def tail(path_d, w=34):
    return (f'<path d="{path_d}" fill="none" stroke="{LINE}" stroke-width="{w + 7}" stroke-linecap="round"/>'
            f'<path d="{path_d}" fill="none" stroke="{BODY}" stroke-width="{w}" stroke-linecap="round"/>'
            f'<path d="{path_d}" fill="none" stroke="{SPOT}" stroke-width="{w}" stroke-dasharray="13 21" stroke-dashoffset="6"/>')


TAIL_SIT = "M255 322 C335 338 372 290 338 248 C326 233 308 240 316 256"


def torso(cy=292):
    s = f'<ellipse cx="200" cy="{cy}" rx="70" ry="68" fill="{BODY}" class="o"/>'
    s += f'<ellipse cx="200" cy="{cy + 10}" rx="42" ry="50" fill="{BELLY}"/>'
    for x, y, r in ((150, cy - 30, 7), (160, cy + 12, 6), (248, cy - 24, 6), (240, cy + 22, 5)):
        s += f'<circle cx="{x}" cy="{y}" r="{r}" fill="{SPOT}"/>'
    return s


def feet(cy=352):
    s = ""
    for cx in (135, 265):
        s += f'<ellipse cx="{cx}" cy="{cy}" rx="36" ry="21" fill="{BODY}" class="o"/>'
        for dx in (-17, 0, 17):
            s += f'<circle cx="{cx + dx}" cy="{cy + 10}" r="5" fill="{PINK}"/>'
    return s


def eye(cx, cy, kind="open", look=(0, 0)):
    lx, ly = look
    if kind == "happy":
        return f'<path d="M{cx - 22} {cy + 8} Q{cx} {cy - 18} {cx + 22} {cy + 8}" fill="none" class="o" stroke-width="4.5"/>'
    if kind == "closed":
        return f'<path d="M{cx - 22} {cy - 2} Q{cx} {cy + 18} {cx + 22} {cy - 2}" fill="none" class="o" stroke-width="4.5"/>'
    return (f'<circle cx="{cx}" cy="{cy}" r="36" fill="{RING}"/>'
            f'<circle cx="{cx + lx}" cy="{cy + ly}" r="32" fill="{EYE}"/>'
            f'<circle cx="{cx + lx - 10}" cy="{cy + ly - 11}" r="10" fill="#fff"/>'
            f'<circle cx="{cx + lx + 11}" cy="{cy + ly + 11}" r="4.6" fill="#fff"/>')


def head(tilt=-8, eyes="open", look=(0, 0), tongue=False, glasses=False, cap=False, mouth="smile", cy=160):
    g = f'<g transform="rotate({tilt} 200 {cy + 40}) translate(0 {cy - 160})">'
    g += f'<clipPath id="hc{tilt}{eyes}"><ellipse cx="200" cy="160" rx="100" ry="82"/></clipPath>'
    g += f'<ellipse cx="200" cy="160" rx="100" ry="82" fill="{BODY}"/>'
    g += f'<g clip-path="url(#hc{tilt}{eyes})"><ellipse cx="200" cy="262" rx="150" ry="82" fill="{BELLY}"/></g>'
    for x, y, r in ((165, 108, 11), (215, 100, 9), (250, 120, 7), (120, 150, 8), (282, 150, 9)):
        g += f'<circle cx="{x}" cy="{y}" r="{r}" fill="{SPOT}"/>'
    for cx in (150, 250):
        for dx, dy in ((-18, -46), (0, -51), (18, -46)):
            g += f'<circle cx="{cx + dx}" cy="{150 + dy}" r="4.4" fill="{TEAL}"/>'
    g += f'<ellipse cx="118" cy="192" rx="19" ry="13" fill="{PINK}" opacity=".85"/>'
    g += f'<ellipse cx="282" cy="192" rx="19" ry="13" fill="{PINK}" opacity=".85"/>'
    g += eye(150, 150, eyes, look) + eye(250, 150, eyes, look)
    g += f'<circle cx="190" cy="176" r="2.6" fill="{LINE}"/><circle cx="210" cy="176" r="2.6" fill="{LINE}"/>'
    if mouth == "smile":
        g += f'<path d="M160 190 Q200 222 240 190" fill="none" class="o" stroke-width="3.6"/>'
    else:  # kleiner, fragender Mund
        g += f'<path d="M178 198 Q200 212 222 198" fill="none" class="o" stroke-width="4.5"/>'
    if tongue:
        g += f'<path d="M205 207 Q217 205 219 217 Q221 229 209 228 Q199 226 205 207Z" fill="#EE7A8A" class="o" stroke-width="3.5"/>'
    g += f'<ellipse cx="200" cy="160" rx="100" ry="82" fill="none" class="o"/>'
    if glasses:
        for cx in (150, 250):
            g += f'<circle cx="{cx}" cy="150" r="41" fill="#35222C" fill-opacity=".9" stroke="#D9A93A" stroke-width="6"/>'
            g += f'<path d="M{cx - 24} 134 Q{cx - 10} 120 {cx + 8} 124" fill="none" stroke="#fff" stroke-opacity=".6" stroke-width="5" stroke-linecap="round"/>'
        g += '<path d="M190 148 Q200 142 210 148" fill="none" stroke="#D9A93A" stroke-width="6"/>'
    if cap:
        g += (f'<path d="M104 118 C118 52 212 30 272 82 C306 112 334 150 352 176 C312 164 282 146 252 132 '
              f'C204 112 150 118 104 118Z" fill="#F0617F" class="o"/>'
              f'<path d="M106 120 C150 112 204 110 252 132" fill="none" stroke="{BELLY}" stroke-width="15" stroke-linecap="round"/>'
              f'<circle cx="352" cy="178" r="16" fill="{BELLY}" class="o"/>')
    return g + "</g>"


def svg(body, w=400, h=400):
    return (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {w} {h}" width="{w}" height="{h}">{STYLE}{body}</svg>')


def star(x, y, r=9, c="#F2C94C"):
    return (f'<path d="M{x} {y - r} Q{x + 2} {y - 2} {x + r} {y} Q{x + 2} {y + 2} {x} {y + r} '
            f'Q{x - 2} {y + 2} {x - r} {y} Q{x - 2} {y - 2} {x} {y - r}Z" fill="{c}" class="o" stroke-width="2.5"/>')


def pose3():  # lugt über die Kante
    s = tail("M338 292 C386 302 384 346 348 366")
    s += head(tilt=-6, cy=165)
    s += (f'<rect x="36" y="262" width="328" height="70" rx="14" fill="#F3DCC8" class="o"/>'
          f'<rect x="36" y="262" width="328" height="16" rx="8" fill="#FBEBDD" class="n"/>'
          f'<path d="M36 318 H364" stroke="{LINE}" stroke-width="0"/>')
    s += hand_at((128, 262)) + hand_at((272, 262))
    return svg(s)


def pose4():  # schreibt mit Stift
    s = tail(TAIL_SIT) + torso() + feet()
    s += arm((150, 270), (110, 320), (150, 338))
    s += (f'<g transform="rotate(-6 200 330)"><rect x="150" y="296" width="110" height="78" rx="6" fill="#FFFDF8" class="o"/>'
          f'<path d="M168 322 H240 M168 340 H226 M168 358 H236" stroke="{TEAL}" stroke-width="3" stroke-linecap="round"/></g>')
    s += arm((250, 270), (292, 300), (236, 334), hand=False)
    s += (f'<g transform="rotate(38 236 334)"><rect x="228" y="268" width="14" height="66" rx="3" fill="#F2C94C" class="o" stroke-width="3.5"/>'
          f'<rect x="228" y="260" width="14" height="14" rx="3" fill="{PINK}" class="o" stroke-width="3.5"/>'
          f'<path d="M228 334 L235 350 L242 334Z" fill="#E8C9A0" class="o" stroke-width="3"/></g>')
    s += hand_at((240, 322), toes=False)
    s += head(tilt=8, look=(0, 7), tongue=True)
    return svg(s)


def pose5():  # schläfrig mit Mütze und Mond
    s = tail(TAIL_SIT) + torso() + feet()
    s += head(tilt=-6, eyes="closed", cap=True)
    s += (f'<path d="M222 252 A46 46 0 0 0 222 344 A72 72 0 0 1 222 252Z" fill="#F2C94C" class="o"/>')
    s += arm((152, 276), (170, 318), (208, 304)) + arm((248, 276), (252, 322), (222, 322))
    s += star(70, 110, 11) + star(335, 78, 9) + star(60, 250, 8) + star(345, 230, 10)
    return svg(s)


def pose6():  # leere Hände, fragend, Gedankenblase mit Herz
    s = tail(TAIL_SIT) + torso() + feet()
    s += arm((148, 272), (96, 296), (74, 312)) + arm((252, 272), (304, 296), (326, 312))
    s += head(tilt=10, mouth="o")
    s += (f'<circle cx="334" cy="150" r="5" fill="#fff" class="o" stroke-width="3"/>'
          f'<circle cx="348" cy="124" r="8" fill="#fff" class="o" stroke-width="3"/>'
          f'<ellipse cx="338" cy="62" rx="52" ry="40" fill="#fff" class="o"/>'
          f'<path d="M338 86 C300 62 318 38 338 54 C358 38 376 62 338 86Z" fill="none" stroke="{ROSE}" stroke-width="5" stroke-linejoin="round"/>')
    return svg(s)


def pose7():  # jubelt
    sh = f'<ellipse cx="200" cy="388" rx="100" ry="9" fill="{LINE}" opacity=".18"/>'
    s = sh + '<g transform="translate(0 -22)">'
    s += tail(TAIL_SIT) + torso() + feet()
    s += arm((148, 266), (92, 240), (66, 188)) + arm((252, 266), (308, 240), (334, 188))
    s += head(tilt=-6, eyes="happy", tongue=True)
    s += "</g>"
    cols = ["#F0617F", "#F2C94C", TEAL, "#FFA654"]
    conf = ((40, 70, 0), (92, 36, 30), (150, 22, 60), (250, 26, 100), (310, 40, 20), (362, 82, 70),
            (30, 140, 45), (372, 150, 10), (58, 250, 80), (344, 262, 35))
    for i, (x, y, r) in enumerate(conf):
        c = cols[i % 4]
        if i % 2:
            s += f'<rect x="{x}" y="{y}" width="14" height="7" rx="2" fill="{c}" transform="rotate({r} {x} {y})"/>'
        else:
            s += f'<circle cx="{x}" cy="{y}" r="6" fill="{c}"/>'
    return svg(s)


def pose8():  # Sonnenbrille auf einem Stein
    s = (f'<path d="M62 392 C52 340 100 318 200 318 C300 318 348 340 338 392Z" fill="#D9C2AE" class="o"/>'
         f'<path d="M110 340 Q150 330 190 336" fill="none" stroke="#C4AC96" stroke-width="5" stroke-linecap="round"/>')
    s = tail("M268 318 C346 332 380 286 346 244 C334 229 316 236 324 252") + s
    s += f'<g transform="translate(0 -34)">' + torso(300) + "</g>"
    s += f'<ellipse cx="140" cy="326" rx="34" ry="18" fill="{BODY}" class="o"/><ellipse cx="260" cy="326" rx="34" ry="18" fill="{BODY}" class="o"/>'
    for cx in (140, 260):
        for dx in (-16, 0, 16):
            s += f'<circle cx="{cx + dx}" cy="336" r="4.6" fill="{PINK}"/>'
    s += arm((150, 262), (120, 296), (148, 300)) + arm((250, 262), (284, 292), (262, 300))
    s += head(tilt=-6, glasses=True, cy=150)
    return svg(s)


POSEN = [(3, "kante", "Lugt über die Kante", pose3), (4, "stift", "Schreibt mit Stift", pose4),
         (5, "nacht", "Schläfrig, Mütze und Mond", pose5), (6, "fragend", "Leere Hände, fragend", pose6),
         (7, "jubel", "Jubelt", pose7), (8, "brille", "Sonnenbrille", pose8)]

if __name__ == "__main__":
    cards = ""
    for n, slug, title, fn in POSEN:
        data = fn()
        (OUT / f"kiko_pose{n}_{slug}.svg").write_text(data, encoding="utf-8")
        cards += f'<figure>{data}<figcaption>Pose {n}: {title}</figcaption></figure>'
    (OUT / "posen_vorschau.html").write_text(
        '<!doctype html><html lang="de"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">'
        '<title>Kiko Posen</title><style>body{margin:0;font-family:system-ui,sans-serif;background:#fff5f5;color:#3a0a1a}'
        'h1{margin:16px}.g{display:flex;flex-wrap:wrap;gap:14px;margin:0 16px 16px}figure{margin:0;background:#fff;border-radius:16px;padding:10px;flex:1 1 280px;max-width:380px}'
        'svg{width:100%;height:auto}figcaption{text-align:center}</style></head><body><h1>Kiko: Posen 3 bis 8 (Vektor)</h1>'
        f'<div class="g">{cards}</div></body></html>', encoding="utf-8")
    print("ok")
