"""Setzt die rohen Emulator-Screenshots in moderne Store-Bilder (Verlauf, Headline, Geraeterahmen-Schatten).

Aufruf:  python -I store/tools/frame_screenshots.py   (nach capture_screenshots.py)
Eingabe: store/graphics/screenshots/<lang>_<n>_<name>.png
Ausgabe: store/graphics/play-screenshots/<lang>/<n>_<name>.png  (1350x2400, 9:16 wie von Play verlangt)
"""
import os

import numpy as np
from PIL import Image, ImageDraw, ImageFilter, ImageFont

BASE = os.path.join(os.path.dirname(__file__), "..", "graphics")
SRC = os.path.join(BASE, "screenshots")
W, H = 1350, 2400
SS = 2

CAPTIONS = {
    "en": {"1_home": "Kind words,\none tap away", "2_favorites": "Keep your\nfavorites",
           "3_mine": "Add your\nown words", "4_settings": "Gentle reminders,\nyour way"},
    "de": {"1_home": "Nette Worte,\nein Tipp entfernt", "2_favorites": "Behalte deine\nFavoriten",
           "3_mine": "Schreibe deine\neigenen Worte", "4_settings": "Sanfte Erinnerungen,\nnach deinem Takt"},
}


def gradient(w, h, c_a, c_b):
    ys = np.linspace(0, 1, h)[:, None, None]
    xs = np.linspace(0, 1, w)[None, :, None]
    t = np.clip(0.65 * ys + 0.35 * xs, 0, 1)
    arr = np.array(c_a)[None, None, :] * (1 - t) + np.array(c_b)[None, None, :] * t
    return Image.fromarray(arr.astype("uint8"), "RGB")


def font(size):
    path = os.path.join(os.environ.get("WINDIR", "C:/Windows"), "Fonts")
    for n in ("segoeuib.ttf", "arialbd.ttf"):
        try:
            return ImageFont.truetype(os.path.join(path, n), size)
        except OSError:
            pass
    return ImageFont.load_default()


def frame(shot_path, caption, out_path):
    w, h = W * SS, H * SS
    img = gradient(w, h, (255, 150, 60), (240, 40, 105))
    d = ImageDraw.Draw(img)
    d.multiline_text((90 * SS, 120 * SS), caption, font=font(96 * SS), fill=(255, 255, 255), spacing=14 * SS)
    shot = Image.open(shot_path).convert("RGB")
    pw = 880 * SS
    ph = int(shot.height * pw / shot.width)
    shot = shot.resize((pw, ph), Image.LANCZOS)
    mask = Image.new("L", (pw, ph), 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, pw - 1, ph - 1), radius=80 * SS, fill=255)
    x, y = (w - pw) // 2, 470 * SS
    sh = Image.new("L", (w, h), 0)
    sh.paste(mask.point(lambda v: int(v * 0.45)), (x, y + 30 * SS))
    sh = sh.filter(ImageFilter.GaussianBlur(40 * SS))
    img.paste(Image.new("RGB", (w, h), (90, 10, 40)), (0, 0), sh)
    # heller Rand wie Geraetekante
    edge = Image.new("L", (pw, ph), 0)
    ImageDraw.Draw(edge).rounded_rectangle((0, 0, pw - 1, ph - 1), radius=80 * SS, outline=255, width=6 * SS)
    img.paste(Image.new("RGB", (pw, ph), (40, 20, 20)), (x, y), edge)
    img.paste(shot, (x, y), mask)
    img.resize((W, H), Image.LANCZOS).save(out_path)


for lang, caps in CAPTIONS.items():
    out_dir = os.path.join(BASE, "play-screenshots", lang)
    os.makedirs(out_dir, exist_ok=True)
    for key, cap in caps.items():
        frame(os.path.join(SRC, f"{lang}_{key}.png"), cap, os.path.join(out_dir, f"{key}.png"))
print("ok")
