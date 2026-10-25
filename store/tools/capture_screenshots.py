"""Nimmt Play-Store-Screenshots (de/en) per adb auf einem laufenden Emulator auf.

Voraussetzung: Emulator laeuft, Debug-APK ist installiert (app.rpkindwords).
Aufruf: python -I store/tools/capture_screenshots.py
Ausgabe: store/graphics/screenshots/<lang>_<n>_<name>.png (rohe 1080x2400 Screenshots)
Hinweis: Buttons werden ueber ihre (lokalisierte) Beschriftung gefunden, nicht ueber Koordinaten.
"""
import os
import re
import subprocess
import time
import xml.etree.ElementTree as ET

ADB = r"D:\Android\Sdk\platform-tools\adb.exe"
PKG = "app.rpkindwords"
OUT = os.path.join(os.path.dirname(__file__), "..", "graphics", "screenshots")
os.makedirs(OUT, exist_ok=True)

L = {
    "en": dict(new="New suggestion", fav_add="Add to favorites", nav_fav="Favorites", nav_mine="My words",
               nav_set="Settings", add="Add", save="Save", cat="Love",
               own=["You%smake%smy%sday.", "Thanks%sfor%sbeing%syou."]),
    "de": dict(new="Neuer Vorschlag", fav_add="Zu Favoriten hinzufügen", nav_fav="Favoriten", nav_mine="Eigene Sätze",
               nav_set="Einstellungen", add="Hinzufügen", save="Speichern", cat="Liebe",
               own=["Du%sbringst%smich%szum%sLachen.", "Du%smachst%smeinen%sTag%sheller."]),
}


def adb(*a, binary=False):
    r = subprocess.run([ADB, *a], capture_output=True)
    return r.stdout if binary else r.stdout.decode("utf-8", "replace")


def nodes():
    adb("shell", "uiautomator", "dump", "/sdcard/ui.xml")
    xml = adb("exec-out", "cat", "/sdcard/ui.xml")
    return list(ET.fromstring(xml).iter("node"))


def find(label, last=False):
    for _ in range(6):  # uiautomator dump ist gelegentlich leer/zu frueh
        try:
            hits = [n for n in nodes() if label in (n.get("text"), n.get("content-desc"))]
            if hits:
                m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", hits[-1 if last else 0].get("bounds"))
                x0, y0, x1, y1 = map(int, m.groups())
                return (x0 + x1) // 2, (y0 + y1) // 2
        except ET.ParseError:
            pass
        time.sleep(1)
    raise RuntimeError(f"nicht gefunden: {label}")


def tap(label, wait=1.2, last=False):
    x, y = find(label, last)
    adb("shell", "input", "tap", str(x), str(y))
    time.sleep(wait)


def shot(lang, n, name):
    time.sleep(0.8)
    png = adb("exec-out", "screencap", "-p", binary=True)
    with open(os.path.join(OUT, f"{lang}_{n}_{name}.png"), "wb") as f:
        f.write(png)


def run(lang):
    s = L[lang]
    adb("shell", "pm", "clear", PKG)
    adb("shell", "pm", "grant", PKG, "android.permission.POST_NOTIFICATIONS")
    adb("shell", "cmd", "locale", "set-app-locales", PKG, "--locales", lang)
    time.sleep(2)
    adb("shell", "am", "force-stop", PKG)
    adb("shell", "am", "start", "-n", f"{PKG}/.MainActivity")
    time.sleep(5)
    # Favoriten anlegen
    tap(s["cat"])
    for _ in range(3):
        tap(s["new"])
        try:  # Vorschlag kann zufaellig schon Favorit sein
            tap(s["fav_add"])
        except RuntimeError:
            pass
    tap(s["new"])
    shot(lang, 1, "home")
    # Favoriten
    tap(s["nav_fav"], 1.5, last=True)
    shot(lang, 2, "favorites")
    # Eigene Saetze
    tap(s["nav_mine"], 1.5, last=True)
    for text in s["own"]:
        adb("shell", "input", "tap", "910", "2010")
        time.sleep(1.5)
        adb("shell", "input", "text", text)
        time.sleep(0.5)
        tap(s["save"])
    shot(lang, 3, "mine")
    # Einstellungen
    tap(s["nav_set"], 1.5, last=True)
    shot(lang, 4, "settings")


for lg in ("en", "de"):
    run(lg)
print("ok", sorted(os.listdir(OUT)))
