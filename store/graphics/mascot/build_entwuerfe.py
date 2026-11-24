# Erzeugt entwuerfe.html: Gecko-Maskottchen "Kiko" in mehreren Varianten (SVG, animiert).
# Aufruf: python build_entwuerfe.py
import html
import pathlib

WARM = dict(body="#FFC27A", belly="#FFF1DC", spot="#C98A4B", ring="#F2C94C", eye="#3A0A1A", cheek="#F28CA5",
            accent="#6EC9D6", line="#7A2A3D", shade="#E9A55C", toe="#F28CA5")
MINT = dict(body="#8FD9A8", belly="#E3F7E8", spot="#5BB57E", ring="#F2C94C", eye="#1F3A2A", cheek="#F6A3B5",
            accent="#FFB26B", line="#2D5A3F", shade="#6CBF8A", toe="#F6A3B5")


def head(uid, glasses=False, sleepy=False, cap=False):
    eyes = ""
    # Augenwülste mit türkisen Tupfern darüber
    for cx in (106, 194):
        eyes += f'<circle cx="{cx}" cy="98" r="33" class="fb{"" if sleepy else " sh"}"/>'
        for dx, dy in ((-14, -26), (0, -32), (14, -26)):
            eyes += f'<circle cx="{cx + dx}" cy="{98 + dy}" r="3.4" class="fa"/>'
    if sleepy:
        for cx in (106, 194):
            eyes += f'<path d="M{cx-23} 100 Q{cx} 118 {cx+23} 100" class="ln" fill="none" stroke-width="5" stroke-linecap="round"/>'
    else:
        for cx in (106, 194):
            eyes += (f'<circle cx="{cx}" cy="98" r="27" class="fr"/><circle cx="{cx}" cy="98" r="22" class="fe"/>'
                     f'<circle cx="{cx-7}" cy="90" r="7.5" fill="#fff"/><circle cx="{cx+8}" cy="107" r="3.6" fill="#fff" opacity=".9"/>')
        for cx in (106, 194):
            eyes += f'<circle class="lid fb" cx="{cx}" cy="98" r="33"/>'
    gl = ""
    if glasses:
        gl += (f'<defs><clipPath id="gl{uid}L"><circle cx="106" cy="98" r="31"/></clipPath>'
               f'<clipPath id="gl{uid}R"><circle cx="194" cy="98" r="31"/></clipPath></defs>')
        for cx, k in ((106, "L"), (194, "R")):
            gl += (f'<circle cx="{cx}" cy="98" r="31" fill="#1b1624" opacity=".94" stroke="#D9A441" stroke-width="4"/>'
                   f'<g clip-path="url(#gl{uid}{k})"><rect class="glint" x="{cx-8}" y="60" width="9" height="80" '
                   f'fill="#fff" opacity=".55" transform="rotate(25 {cx} 98)"/></g>')
        gl += '<path d="M137 96 Q150 88 163 96" fill="none" stroke="#D9A441" stroke-width="4" stroke-linecap="round"/>'
        gl += '<path d="M75 94 L60 88 M225 94 L240 88" stroke="#D9A441" stroke-width="4" stroke-linecap="round"/>'
    hat = ""
    if cap:
        hat = ('<path d="M72 74 Q114 14 204 28 Q246 38 232 74 Q150 56 72 74Z" fill="#6E7BC9"/>'
               '<path d="M64 78 Q150 54 236 78" fill="none" stroke="#fff" stroke-width="15" stroke-linecap="round"/>'
               '<circle cx="222" cy="30" r="12" fill="#fff"/>')
    if sleepy:
        mouth = '<path d="M124 158 Q150 172 176 158" class="ln" fill="none" stroke-width="4.5" stroke-linecap="round"/>'
    else:
        mouth = '<path d="M106 152 Q150 186 194 152" class="ln" fill="none" stroke-width="4.5" stroke-linecap="round"/>'
    face = ('<circle cx="143" cy="136" r="2.3" class="lnf"/><circle cx="157" cy="136" r="2.3" class="lnf"/>'
            '<circle cx="86" cy="152" r="12" class="fc" opacity=".6"/><circle cx="214" cy="152" r="12" class="fc" opacity=".6"/>'
            + mouth)
    spots = ('<circle cx="150" cy="60" r="6" class="fs" opacity=".5"/><circle cx="170" cy="68" r="4.5" class="fs" opacity=".5"/>'
             '<circle cx="130" cy="66" r="4" class="fs" opacity=".5"/>')
    return (f'<g class="head"><g transform="rotate(-7 150 120)">'
            f'<ellipse cx="150" cy="118" rx="92" ry="74" class="fb sh"/>{spots}{eyes}{face}{gl}{hat}</g></g>')


def toes(cx, cy, n=3, spread=14):
    return "".join(
        f'<circle cx="{cx + (i - (n - 1) / 2) * spread:.1f}" cy="{cy + (4 if 0 < i < n - 1 else 0)}" r="5.2" class="ft"/>'
        for i in range(n))


def foot(cx, cls):
    return f'<g class="{cls}"><ellipse cx="{cx}" cy="278" rx="27" ry="13" class="fb sh"/>' + toes(cx, 285) + '</g>'


def tail(uid):
    p = "M190 255 C250 262 285 230 272 190 C266 172 282 166 288 186 C300 238 250 292 185 284 Z"
    bands = "".join(
        f'<ellipse cx="{x}" cy="{y}" rx="9" ry="15" transform="rotate({r} {x} {y})" class="fs" opacity=".8"/>'
        for x, y, r in ((222, 272, 25), (256, 258, 50), (280, 222, 85)))
    return (f'<g class="tail"><clipPath id="tc{uid}"><path d="{p}"/></clipPath><path d="{p}" class="fb sh"/>'
            f'<g clip-path="url(#tc{uid})">{bands}</g></g>')


def body(uid):
    return (tail(uid) +
            '<g class="breathe"><ellipse cx="150" cy="232" rx="64" ry="52" class="fb sh"/>'
            '<ellipse cx="150" cy="242" rx="42" ry="36" class="fl"/>'
            '<circle cx="120" cy="212" r="5" class="fs" opacity=".5"/><circle cx="182" cy="216" r="4" class="fs" opacity=".5"/></g>')


def arm(path, hx, hy, cls=""):
    return (f'<g class="{cls}"><path d="{path}" fill="none" stroke-width="22" stroke-linecap="round" class="sb"/>'
            f'<ellipse cx="{hx}" cy="{hy}" rx="15" ry="11" class="fb sh"/>{toes(hx, hy + 9, 3, 11)}</g>')


def svg(label, colors, inner):
    st = ";".join(f"--{k}:{v}" for k, v in colors.items())
    return f'<svg class="gk" viewBox="0 0 300 300" style="{st}" role="img" aria-label="Kiko {label}">{inner}</svg>'


SHADOW = '<ellipse cx="150" cy="293" rx="105" ry="8" class="gshadow"/>'
ARMS_DOWN = arm("M96 208 Q72 228 80 252", 80, 254) + arm("M204 208 Q228 228 220 252", 220, 254)


def v_wave(c):
    wave = ('<g class="wave"><path d="M204 208 Q242 192 252 160" fill="none" stroke-width="22" stroke-linecap="round" class="sb"/>'
            '<ellipse cx="253" cy="152" rx="15" ry="11" class="fb sh"/>' + toes(253, 143, 3, 11) + '</g>')
    return svg("winkt", c, SHADOW + body("w") + foot(106, "foot-a") + foot(194, "foot-b")
               + arm("M96 208 Q72 228 80 252", 80, 254) + wave + head("w"))


def v_heart(c):
    arms = arm("M96 208 Q92 238 128 244", 130, 246) + arm("M204 208 Q208 238 172 244", 170, 246)
    heart = ('<g class="heart"><path d="M150 264 C116 238 128 212 150 227 C172 212 184 238 150 264Z" fill="#DE1E5A"/>'
             '<path d="M136 228 q7 -7 14 0" fill="none" stroke="#fff" stroke-width="3" opacity=".6" stroke-linecap="round"/></g>')
    return svg("herz", c, SHADOW + body("h") + foot(106, "foot-a") + foot(194, "foot-b") + arms + heart + head("h"))


def v_cool(c):
    return svg("cool", c, SHADOW + body("c") + foot(106, "foot-a") + foot(194, "foot-b") + ARMS_DOWN + head("c", glasses=True))


def v_mint(c):
    return svg("mint", c, SHADOW + body("m") + foot(106, "foot-a") + foot(194, "foot-b") + ARMS_DOWN + head("m"))


def v_peek(c):
    ledge = '<rect x="14" y="184" width="272" height="120" rx="20" class="ledge"/><rect x="14" y="184" width="272" height="10" rx="5" class="ledge2"/>'
    hands = ""
    for hx, cls in ((84, "tap-a"), (216, "tap-b")):
        hands += (f'<g class="{cls}"><ellipse cx="{hx}" cy="186" rx="19" ry="11" class="fb sh"/>'
                  + "".join(f'<circle cx="{hx + (i - 1) * 13}" cy="{196 + (0 if i == 1 else -2)}" r="5.4" class="ft"/>' for i in range(3))
                  + '</g>')
    return svg("lugt", c, '<g transform="translate(0,-4)">' + head("p") + '</g>' + ledge + hands)


def v_sleep(c):
    zz = "".join(
        f'<text class="zz" x="{x}" y="70" style="animation-delay:{d}s" font-size="{s}" font-weight="800" '
        f'font-family="Arial,sans-serif" fill="#8E99E0">z</text>' for x, d, s in ((226, 0, 26), (246, 1.2, 34), (266, 2.4, 42)))
    return svg("schlaeft", c, SHADOW + body("s") + foot(106, "") + foot(194, "") + ARMS_DOWN + head("s", sleepy=True, cap=True) + zz)


def v_icon(c, uid, size):
    st = ";".join(f"--{k}:{v}" for k, v in c.items())
    return (f'<svg class="gk icon" width="{size}" height="{size}" viewBox="0 0 300 300" style="{st}" role="img" aria-label="Kiko Symbol {size}">'
            f'<defs><linearGradient id="ig{uid}" x1="1" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#F0426F"/>'
            f'<stop offset="1" stop-color="#C71857"/></linearGradient></defs>'
            f'<rect width="300" height="300" rx="66" fill="url(#ig{uid})"/>'
            f'<g transform="translate(150 150) scale(1.38) translate(-150 -112)">' + head("i" + uid) + '</g></svg>')


cards = [
    ("1", "Kiko winkt", "Standardfigur, warm", "Einführung, Willkommen", v_wave(WARM), "Blinzeln · Füße wechseln · Hand winkt · Schwanz wedelt"),
    ("2", "Kiko mit Herz", "Warm, Herz pocht", "Favoriten, Spendenbitte", v_heart(WARM), "Blinzeln · Herz pocht · Füße wechseln · Schwanz wedelt"),
    ("3", "Kiko cool", "Mit Sonnenbrille", "Humor, Sommer, Store-Grafik", v_cool(WARM), "Glanz auf den Gläsern · Füße wechseln · Schwanz wedelt"),
    ("4", "Kiko mint", "Andere Farbe (Taggecko)", "Hebt sich stärker vom Rose ab", v_mint(MINT), "Blinzeln · Füße wechseln · Schwanz wedelt"),
    ("5", "Kiko lugt", "Über die Kante", "Titelzeile, Widget-Rand, Laden", v_peek(WARM), "Blinzeln · Finger trommeln"),
    ("6", "Kiko schläft", "Schlafmütze, Zzz", "Gute Nacht, Dunkelmodus", v_sleep(WARM), "Atmen · Zzz schweben · Schwanz wedelt"),
]
card_html = "".join(
    f'<article class="card"><div class="stage">{s}</div><h2><span class="n">{n}</span>{html.escape(t)}</h2>'
    f'<p class="sub">{html.escape(sub)}</p><p class="use">Einsatz: {html.escape(use)}</p><p class="anim">Animation: {html.escape(an)}</p></article>'
    for n, t, sub, use, s, an in cards)
icons = ('<section class="icons"><h2><span class="n">7</span>Kopf-Symbol (Logo, Titelzeile)</h2>'
         '<p class="sub">Blinzelt, hier in vier Größen zum Prüfen der Lesbarkeit: 120, 64, 40 Pixel und mint.</p>'
         f'<div class="row">{v_icon(WARM, "a", 120)}{v_icon(WARM, "b", 64)}{v_icon(WARM, "c", 40)}{v_icon(MINT, "d", 64)}</div></section>')

CSS = """
:root{--bg:#FFF8F6;--card:#fff;--ink:#3A0A1A;--mute:#7A5A63;--line:#F0D9DD;--accent-ui:#DE1E5A;--ledge:#F3D9C4;--ledge2:#E7C3A8;color-scheme:light}
body.dark{--bg:#1B1315;--card:#26191C;--ink:#FFE9EC;--mute:#C9A7AF;--line:#44303A;--ledge:#4A3A42;--ledge2:#5B4750;color-scheme:dark}
@media (prefers-color-scheme:dark){body:not(.light){--bg:#1B1315;--card:#26191C;--ink:#FFE9EC;--mute:#C9A7AF;--line:#44303A;--ledge:#4A3A42;--ledge2:#5B4750;color-scheme:dark}}
*{box-sizing:border-box}
body{margin:0;background:var(--bg);color:var(--ink);font:16px/1.5 system-ui,-apple-system,"Segoe UI",Roboto,sans-serif}
header,main{max-width:1100px;margin:0 auto;padding:0 16px}
header{padding-top:28px}
h1{font-size:28px;margin:0 0 6px}
.lead{color:var(--mute);margin:0 0 16px;max-width:62ch}
.bar{display:flex;gap:8px;flex-wrap:wrap;margin:12px 0 24px}
button{font:inherit;border:1px solid var(--line);background:var(--card);color:var(--ink);padding:8px 14px;border-radius:999px;cursor:pointer}
button:hover{border-color:var(--accent-ui)}
.grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(300px,1fr));gap:16px}
.card{background:var(--card);border:1px solid var(--line);border-radius:20px;padding:14px 14px 18px}
.stage{background:radial-gradient(circle at 50% 42%,rgba(255,194,122,.22),transparent 70%);border-radius:14px}
.gk{width:100%;height:auto;display:block;overflow:visible}
.gk.icon{width:auto;height:auto}
h2{font-size:18px;margin:10px 0 2px;display:flex;align-items:center;gap:8px}
.n{display:inline-grid;place-items:center;width:26px;height:26px;border-radius:50%;background:var(--accent-ui);color:#fff;font-size:14px;font-weight:700}
.sub{margin:0}
.use,.anim{margin:2px 0 0;color:var(--mute);font-size:14px}
.icons{margin:24px 0 40px;background:var(--card);border:1px solid var(--line);border-radius:20px;padding:16px}
.row{display:flex;gap:20px;align-items:flex-end;flex-wrap:wrap;margin-top:12px}
footer{max-width:1100px;margin:0 auto;padding:0 16px 40px;color:var(--mute);font-size:14px}
.fb{fill:var(--body)}.fl{fill:var(--belly)}.fs{fill:var(--spot)}.fr{fill:var(--ring)}.fe{fill:var(--eye)}.fc{fill:var(--cheek)}.fa{fill:var(--accent)}.ft{fill:var(--toe)}
.ln{stroke:var(--line)}.lnf{fill:var(--line)}.sb{stroke:var(--body)}.sh{stroke:var(--shade);stroke-width:2}
.gshadow{fill:#000;opacity:.09}
body.dark .gshadow{fill:#fff;opacity:.06}
.ledge{fill:var(--ledge)}.ledge2{fill:var(--ledge2)}
.lid{transform-box:fill-box;transform-origin:50% 0;transform:scaleY(0);animation:blink 4.4s infinite}
@keyframes blink{0%,90%,100%{transform:scaleY(0)}93%,96%{transform:scaleY(1)}}
.foot-a,.foot-b{transform-box:fill-box;transform-origin:50% 100%;animation:step 2.8s ease-in-out infinite}
.foot-b{animation-delay:-1.4s}
@keyframes step{0%,45%,100%{transform:translateY(0) rotate(0)}15%{transform:translateY(-10px) rotate(-6deg)}30%{transform:translateY(0) rotate(0)}}
.tail{transform-box:view-box;transform-origin:190px 262px;animation:sway 3.4s ease-in-out infinite alternate}
@keyframes sway{from{transform:rotate(-5deg)}to{transform:rotate(7deg)}}
.head{transform-box:view-box;transform-origin:150px 190px;animation:bob 3.6s ease-in-out infinite alternate}
@keyframes bob{from{transform:rotate(-1.5deg) translateY(0)}to{transform:rotate(1.5deg) translateY(-3px)}}
.wave{transform-box:view-box;transform-origin:204px 208px;animation:wave .9s ease-in-out infinite alternate}
@keyframes wave{from{transform:rotate(-16deg)}to{transform:rotate(12deg)}}
.heart{transform-box:fill-box;transform-origin:50% 60%;animation:pulse 1.3s ease-in-out infinite}
@keyframes pulse{0%,100%{transform:scale(1)}30%{transform:scale(1.14)}60%{transform:scale(.98)}}
.glint{animation:glint 3.6s ease-in-out infinite}
@keyframes glint{0%,55%{transform:translateX(-50px)}100%{transform:translateX(60px)}}
.breathe{transform-box:view-box;transform-origin:150px 284px;animation:breath 3.2s ease-in-out infinite alternate}
@keyframes breath{from{transform:scale(1,1)}to{transform:scale(1.015,1.035)}}
.tap-a,.tap-b{animation:tap 1.2s ease-in-out infinite}
.tap-b{animation-delay:-.6s}
@keyframes tap{0%,60%,100%{transform:translateY(0)}25%{transform:translateY(-6px)}}
.zz{animation:zfloat 3.6s ease-in infinite;opacity:0}
@keyframes zfloat{0%{transform:translate(0,30px);opacity:0}20%{opacity:.9}100%{transform:translate(14px,-26px);opacity:0}}
.paused *{animation-play-state:paused !important}
@media (prefers-reduced-motion:reduce){.gk *{animation:none !important}}
"""

page = f"""<!doctype html>
<html lang="de"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Kiko Entwürfe</title>
<style>{CSS}</style></head><body>
<header>
<h1>Kiko, der Gecko: Entwürfe zum Auswählen</h1>
<p class="lead">Sechs Varianten und ein Kopf-Symbol, als Vektor gezeichnet, damit sie in der App scharf und klein bleiben. Sag mir die Nummern, die Dir gefallen, und was Du ändern willst (Augen, Farbe, Schwanz, Gesicht).</p>
<div class="bar"><button id="pause">Animation anhalten</button><button id="theme">Hell / Dunkel wechseln</button></div>
</header>
<main><div class="grid">{card_html}</div>{icons}</main>
<footer>Hinweis: Bewegung entfällt, wenn Dein Gerät "Bewegung reduzieren" eingeschaltet hat.</footer>
<script>
const b=document.body;
document.getElementById('pause').onclick=e=>{{b.classList.toggle('paused');e.target.textContent=b.classList.contains('paused')?'Animation starten':'Animation anhalten'}};
document.getElementById('theme').onclick=()=>{{const dark=b.classList.contains('dark')||(!b.classList.contains('light')&&matchMedia('(prefers-color-scheme:dark)').matches);b.classList.toggle('dark',!dark);b.classList.toggle('light',dark)}};
</script></body></html>"""

out = pathlib.Path(__file__).with_name("entwuerfe.html")
out.write_text(page, encoding="utf-8")
print(out, out.stat().st_size)
