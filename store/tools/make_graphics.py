"""Erzeugt Play-Store-Grafiken fuer "RP Kind Words": Icon (1024 Master + 512 fuer Play), Feature-Graphic 1024x500 (de/en).

Aufruf:  python -I store/tools/make_graphics.py   (benoetigt Pillow, numpy)
Ausgabe: store/graphics/
Design:  Orange->Rose-Verlauf, Licht-Blobs, weisse Sprechblase mit Tiefe, plastisches Herz (Verlauf, Innenschatten,
         Glanz, Glow), Funkeln. Alles in 3x Supersampling.
"""
import math
import os

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFilter, ImageFont

OUT = os.path.join(os.path.dirname(__file__), "..", "graphics")
os.makedirs(OUT, exist_ok=True)

C1 = (255, 154, 62)    # Orange
C2 = (238, 36, 104)    # Rose
H1 = (255, 128, 84)
H2 = (214, 24, 88)
SS = 3


def gradient(w, h, c_a, c_b, angle_deg=135):
    a = math.radians(angle_deg)
    xs = np.linspace(-0.5, 0.5, w)[None, :]
    ys = np.linspace(-0.5, 0.5, h)[:, None]
    t = np.clip((xs * math.cos(a) + ys * math.sin(a)) + 0.5, 0, 1)[..., None]
    arr = np.array(c_a)[None, None, :] * (1 - t) + np.array(c_b)[None, None, :] * t
    return Image.fromarray(arr.astype("uint8"), "RGB")


def heart_pts(cx, cy, size, n=900):
    pts = []
    for i in range(n):
        t = 2 * math.pi * i / n
        x = 16 * math.sin(t) ** 3
        y = 13 * math.cos(t) - 5 * math.cos(2 * t) - 2 * math.cos(3 * t) - math.cos(4 * t)
        pts.append((cx + x * size / 34, cy - y * size / 34))
    return pts


def mask_heart(w, h, cx, cy, size):
    m = Image.new("L", (w, h), 0)
    ImageDraw.Draw(m).polygon(heart_pts(cx, cy, size), fill=255)
    return m


def mask_bubble(w, h, x0, y0, x1, y1, radius, tail):
    m = Image.new("L", (w, h), 0)
    d = ImageDraw.Draw(m)
    d.rounded_rectangle((x0, y0, x1, y1), radius=radius, fill=255)
    d.polygon(tail, fill=255)
    return m


def tint(base, mask, color, alpha=1.0, blur=0, dy=0):
    m = mask
    if dy:
        sh = Image.new("L", m.size, 0)
        sh.paste(m, (0, dy))
        m = sh
    if blur:
        m = m.filter(ImageFilter.GaussianBlur(blur))
    if alpha < 1:
        m = m.point(lambda v: int(v * alpha))
    base.paste(Image.new("RGB", base.size, color), (0, 0), m)


def sparkle(base, cx, cy, r, alpha=230):
    w, h = base.size
    m = Image.new("L", (w, h), 0)
    d = ImageDraw.Draw(m)
    pts = []
    for i in range(8):
        ang = math.pi / 4 * i - math.pi / 2
        rad = r if i % 2 == 0 else r * 0.22
        pts.append((cx + rad * math.cos(ang), cy + rad * math.sin(ang)))
    d.polygon(pts, fill=alpha)
    m = m.filter(ImageFilter.GaussianBlur(r * 0.03))
    base.paste(Image.new("RGB", (w, h), (255, 255, 255)), (0, 0), m)


def plastic_heart(base, cx, cy, size):
    w, h = base.size
    hm = mask_heart(w, h, cx, cy, size)
    tint(base, hm, (255, 120, 140), 0.55, blur=size * 0.09)                  # Glow
    tint(base, hm, (120, 10, 50), 0.45, blur=size * 0.035, dy=int(size * 0.03))  # Schatten
    base.paste(gradient(w, h, H1, H2, 115), (0, 0), hm)
    # Innenschatten unten rechts (Volumen)
    inv = ImageChops.invert(hm).filter(ImageFilter.GaussianBlur(size * 0.07))
    inner = ImageChops.multiply(inv, hm)
    shifted = Image.new("L", (w, h), 0)
    shifted.paste(inner, (int(-size * 0.006), int(-size * 0.012)))
    tint(base, ImageChops.multiply(shifted, hm), (150, 0, 60), 0.55)
    # Glanz oben links
    gl = Image.new("L", (w, h), 0)
    ImageDraw.Draw(gl).ellipse((cx - size * 0.46, cy - size * 0.52, cx - size * 0.04, cy - size * 0.16), fill=150)
    gl = ImageChops.multiply(gl.filter(ImageFilter.GaussianBlur(size * 0.03)), hm)
    tint(base, gl, (255, 255, 255))
    # kleiner Lichtpunkt
    dot = Image.new("L", (w, h), 0)
    ImageDraw.Draw(dot).ellipse((cx - size * 0.30, cy - size * 0.36, cx - size * 0.22, cy - size * 0.30), fill=235)
    tint(base, ImageChops.multiply(dot.filter(ImageFilter.GaussianBlur(size * 0.008)), hm), (255, 255, 255))


def bubble_with_heart(base, cx, cy, bw, bh, heart_scale=0.8):
    w, h = base.size
    x0, y0, x1, y1 = cx - bw / 2, cy - bh / 2, cx + bw / 2, cy + bh / 2
    tail = [(x0 + bw * 0.14, y1 - bh * 0.05), (x0 + bw * 0.07, y1 + bh * 0.24), (x0 + bw * 0.34, y1 - bh * 0.05)]
    bm = mask_bubble(w, h, x0, y0, x1, y1, bh * 0.34, tail)
    tint(base, bm, (110, 8, 48), 0.40, blur=bh * 0.07, dy=int(bh * 0.06))      # weicher Schatten
    base.paste(gradient(w, h, (255, 255, 255), (255, 230, 236), 95), (0, 0), bm)  # leichter Verlauf
    # feiner Randlicht-Ring oben
    rim = ImageChops.subtract(bm, bm.filter(ImageFilter.GaussianBlur(bh * 0.012)))
    tint(base, rim, (255, 255, 255), 0.8)
    plastic_heart(base, cx, cy + bh * 0.03, bh * heart_scale)
    return (x0, y0, x1, y1)


def background(w, h):
    img = gradient(w, h, C1, C2, 125)
    ov = Image.new("L", (w, h), 0)
    d = ImageDraw.Draw(ov)
    d.ellipse((-w * 0.18, -h * 0.40, w * 0.50, h * 0.55), fill=70)
    d.ellipse((w * 0.58, h * 0.40, w * 1.20, h * 1.35), fill=55)
    ov = ov.filter(ImageFilter.GaussianBlur(min(w, h) * 0.12))
    img.paste(Image.new("RGB", (w, h), (255, 226, 196)), (0, 0), ov)
    # dunkle Vignette fuer Tiefe
    vig = gradient(w, h, (0, 0, 0), (60, 0, 25), 90).convert("L").point(lambda v: int(v * 0.0))
    xs = np.linspace(-1, 1, w)[None, :]
    ys = np.linspace(-1, 1, h)[:, None]
    v = np.clip((xs ** 2 + ys ** 2) / 2.0, 0, 1) ** 1.6 * 70
    vig = Image.fromarray(v.astype("uint8"), "L")
    img.paste(Image.new("RGB", (w, h), (70, 0, 30)), (0, 0), vig)
    return img


def icon():
    n = 1024 * SS
    img = background(n, n)
    x0, y0, x1, y1 = bubble_with_heart(img, n * 0.5, n * 0.47, n * 0.62, n * 0.50)
    sparkle(img, x1 - n * 0.02, y0 + n * 0.015, n * 0.075)
    sparkle(img, x0 - n * 0.045, y0 + n * 0.20, n * 0.04, 200)
    sparkle(img, x1 + n * 0.055, y0 + n * 0.30, n * 0.03, 190)
    master = img.resize((1024, 1024), Image.LANCZOS)
    master.save(os.path.join(OUT, "icon-1024-master.png"))
    master.resize((512, 512), Image.LANCZOS).save(os.path.join(OUT, "icon-512.png"))


def font(size, bold=True):
    fonts = os.path.join(os.environ.get("WINDIR", "C:/Windows"), "Fonts")
    for name in (("segoeuib.ttf", "arialbd.ttf") if bold else ("segoeuisl.ttf", "segoeui.ttf", "arial.ttf")):
        try:
            return ImageFont.truetype(os.path.join(fonts, name), size)
        except OSError:
            continue
    return ImageFont.load_default()


def text_shadow(img, xy, text, fnt, fill, sh_alpha=80):
    w, h = img.size
    sh = Image.new("L", (w, h), 0)
    ImageDraw.Draw(sh).text((xy[0], xy[1] + 5 * SS), text, font=fnt, fill=sh_alpha)
    tint(img, sh, (110, 0, 40), 1.0, blur=7 * SS)
    ImageDraw.Draw(img).text(xy, text, font=fnt, fill=fill)


def mini_bubble(img, x, y, bw, bh, label, fnt, tail_left=True, heart=True):
    w, h = img.size
    tail = ([(x + bw * 0.10, y + bh * 0.9), (x + bw * 0.04, y + bh * 1.22), (x + bw * 0.28, y + bh * 0.9)] if tail_left
            else [(x + bw * 0.72, y + bh * 0.9), (x + bw * 0.96, y + bh * 1.22), (x + bw * 0.90, y + bh * 0.9)])
    bm = mask_bubble(w, h, x, y, x + bw, y + bh, bh * 0.45, tail)
    tint(img, bm, (110, 8, 48), 0.38, blur=bh * 0.10, dy=int(bh * 0.10))
    img.paste(gradient(w, h, (255, 255, 255), (255, 232, 238), 95), (0, 0), bm)
    d = ImageDraw.Draw(img)
    tx = x + bh * 0.42
    d.text((tx, y + bh * 0.5), label, font=fnt, fill=(120, 30, 55), anchor="lm")
    if heart:
        hm = mask_heart(w, h, x + bw - bh * 0.55, y + bh * 0.52, bh * 0.42)
        img.paste(gradient(w, h, H1, H2, 115), (0, 0), hm)


def feature(lang, tagline, bubbles):
    w, h = 1024 * SS, 500 * SS
    img = background(w, h)
    bubble_with_heart(img, 225 * SS, 232 * SS, 330 * SS, 270 * SS)
    sparkle(img, 372 * SS, 80 * SS, 28 * SS)
    sparkle(img, 70 * SS, 110 * SS, 15 * SS, 200)
    text_shadow(img, (415 * SS, 92 * SS), "RP Kind Words", font(76 * SS), (255, 255, 255))
    text_shadow(img, (419 * SS, 196 * SS), tagline, font(34 * SS, bold=False), (255, 240, 232), 55)
    f = font(25 * SS, bold=False)
    mini_bubble(img, 420 * SS, 278 * SS, 355 * SS, 62 * SS, bubbles[0], f, True)
    mini_bubble(img, 585 * SS, 372 * SS, 390 * SS, 62 * SS, bubbles[1], f, False)
    img.resize((1024, 500), Image.LANCZOS).save(os.path.join(OUT, f"feature-graphic-{lang}.png"))


icon()
feature("en", "Small kind words. Big smiles.", ["You make my day", "Proud of you, always"])
feature("de", "Kleine Worte. Große Freude.", ["Du machst meinen Tag", "Stolz auf dich, immer"])
print("ok", sorted(os.listdir(OUT)))
