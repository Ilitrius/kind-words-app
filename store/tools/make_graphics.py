"""Erzeugt Play-Store-Grafiken: Icon (1024 Master + 512 fuer Play) und Feature-Graphic 1024x500 (de/en).

Aufruf:  python -I store/tools/make_graphics.py   (benoetigt Pillow, numpy)
Ausgabe: store/graphics/
Design:  warmer Orange->Rose-Verlauf, weisse Sprechblase, Verlaufs-Herz mit Glanz, weiche Schatten.
"""
import math
import os

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFilter, ImageFont

OUT = os.path.join(os.path.dirname(__file__), "..", "graphics")
os.makedirs(OUT, exist_ok=True)

C1 = (255, 150, 60)    # Orange
C2 = (240, 40, 105)    # Rose
H1 = (255, 112, 60)
H2 = (222, 30, 90)
SS = 3                 # Supersampling


def gradient(w, h, c_a, c_b, angle_deg=135):
    a = math.radians(angle_deg)
    xs = np.linspace(-0.5, 0.5, w)[None, :]
    ys = np.linspace(-0.5, 0.5, h)[:, None]
    t = np.clip((xs * math.cos(a) + ys * math.sin(a)) + 0.5, 0, 1)[..., None]
    arr = np.array(c_a)[None, None, :] * (1 - t) + np.array(c_b)[None, None, :] * t
    return Image.fromarray(arr.astype("uint8"), "RGB")


def heart_pts(cx, cy, size, n=720):
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


def shadow(base, mask, dy, blur, alpha):
    sh = Image.new("L", mask.size, 0)
    sh.paste(mask.point(lambda v: int(v * alpha)), (0, dy))
    sh = sh.filter(ImageFilter.GaussianBlur(blur))
    dark = Image.new("RGB", base.size, (90, 10, 40))
    base.paste(dark, (0, 0), sh)


def paint(base, mask, fill_img):
    base.paste(fill_img, (0, 0), mask)


def glossy_heart(base, cx, cy, size):
    w, h = base.size
    hm = mask_heart(w, h, cx, cy, size)
    shadow(base, hm, int(size * 0.03), size * 0.04, 0.35)
    fill = gradient(w, h, H1, H2, 120)
    paint(base, hm, fill)
    # Glanz: heller, unscharfer Bogen oben links, nur innerhalb des Herzens
    gl = Image.new("L", (w, h), 0)
    ImageDraw.Draw(gl).ellipse((cx - size * 0.46, cy - size * 0.50, cx - size * 0.02, cy - size * 0.12), fill=110)
    gl = gl.filter(ImageFilter.GaussianBlur(size * 0.035))
    gl = ImageChops.multiply(gl, hm)
    base.paste(Image.new("RGB", (w, h), (255, 255, 255)), (0, 0), gl)


def bubble_with_heart(base, cx, cy, bw, bh):
    """Sprechblase mit Schwanz unten links, Herz mittig."""
    w, h = base.size
    x0, y0, x1, y1 = cx - bw / 2, cy - bh / 2, cx + bw / 2, cy + bh / 2
    tail = [(x0 + bw * 0.14, y1 - bh * 0.05), (x0 + bw * 0.07, y1 + bh * 0.24), (x0 + bw * 0.34, y1 - bh * 0.05)]
    bm = mask_bubble(w, h, x0, y0, x1, y1, bh * 0.34, tail)
    shadow(base, bm, int(bh * 0.05), bh * 0.06, 0.38)
    paint(base, bm, Image.new("RGB", (w, h), (255, 255, 255)))
    glossy_heart(base, cx, cy + bh * 0.03, bh * 0.80)


def background(w, h):
    img = gradient(w, h, C1, C2, 125)
    # weiche, transparente Lichtkreise (modernes Bokeh)
    ov = Image.new("L", (w, h), 0)
    d = ImageDraw.Draw(ov)
    d.ellipse((-w * 0.15, -h * 0.35, w * 0.45, h * 0.55), fill=60)
    d.ellipse((w * 0.62, h * 0.45, w * 1.15, h * 1.3), fill=45)
    ov = ov.filter(ImageFilter.GaussianBlur(min(w, h) * 0.12))
    img.paste(Image.new("RGB", (w, h), (255, 220, 190)), (0, 0), ov)
    return img


def icon():
    n = 1024 * SS
    img = background(n, n)
    bubble_with_heart(img, n * 0.5, n * 0.46, n * 0.62, n * 0.50)
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


def text_with_shadow(img, xy, text, fnt, fill, sh_alpha=70):
    w, h = img.size
    sh = Image.new("L", (w, h), 0)
    ImageDraw.Draw(sh).text((xy[0], xy[1] + 4 * SS), text, font=fnt, fill=sh_alpha)
    sh = sh.filter(ImageFilter.GaussianBlur(6 * SS))
    img.paste(Image.new("RGB", (w, h), (120, 0, 40)), (0, 0), sh)
    ImageDraw.Draw(img).text(xy, text, font=fnt, fill=fill)


def feature(lang, tagline):
    w, h = 1024 * SS, 500 * SS
    img = background(w, h)
    bubble_with_heart(img, 235 * SS, 235 * SS, 330 * SS, 270 * SS)
    text_with_shadow(img, (430 * SS, 130 * SS), "Kind Words", font(92 * SS), (255, 255, 255))
    text_with_shadow(img, (436 * SS, 262 * SS), tagline, font(40 * SS, bold=False), (255, 240, 232), 50)
    img.resize((1024, 500), Image.LANCZOS).save(os.path.join(OUT, f"feature-graphic-{lang}.png"))


icon()
feature("en", "Small kind words. Big smiles.")
feature("de", "Kleine Worte. Große Freude.")
print("ok", sorted(os.listdir(OUT)))
