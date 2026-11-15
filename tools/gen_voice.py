"""Erzeugt Aufnahmen der mitgelieferten Sätze mit ElevenLabs und legt sie unter app/src/main/assets/voice/<lang>/ ab.

Aufruf (Schlüssel nur aus der Umgebung, nie im Repo): 
  ELEVENLABS_API_KEY=... python -I tools/gen_voice.py <de|en> <voice_id> [--max-chars N] [--limit N] [--cats love,morning]
Dateiname: <id>-<hash8>.mp3 (Hash wie SpeechText.clipHash). Vorhandene Dateien werden übersprungen, Aufnahmen zu geänderten
oder gelöschten Sätzen werden entfernt. Ohne --max-chars wird abgebrochen (Schutz vor unbeabsichtigtem Verbrauch).
"""
import hashlib, json, os, sys, urllib.request

args = sys.argv[1:]
lang, voice = args[0], args[1]
opt = lambda n, d=None: args[args.index(n) + 1] if n in args else d
max_chars = opt("--max-chars"); limit = int(opt("--limit", "100000")); cats = opt("--cats")
if max_chars is None:
    sys.exit("--max-chars angeben (Zeichenbudget, 1 Zeichen = 1 Credit im Modell multilingual_v2)")
max_chars = int(max_chars)
key = os.environ["ELEVENLABS_API_KEY"]
root = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
msgs = json.load(open(os.path.join(root, f"app/src/main/assets/messages/messages_{lang}.json"), encoding="utf-8"))["messages"]
msgs = [m for m in msgs if m["category"] not in ("emoji", "ascii") and (not cats or m["category"] in cats.split(","))]
out = os.path.join(root, "app/src/main/assets/voice", lang); os.makedirs(out, exist_ok=True)
h = lambda t: hashlib.sha1(t.encode("utf-8")).hexdigest()[:8]
wanted = {f"{m['id']}-{h(m['text'])}.mp3": m for m in msgs}
for f in os.listdir(out):
    if f not in wanted: os.remove(os.path.join(out, f)); print("entfernt", f)
used = n = 0
for name, m in wanted.items():
    path = os.path.join(out, name)
    if os.path.exists(path): continue
    if used + len(m["text"]) > max_chars or n >= limit: break
    body = json.dumps({"text": m["text"], "model_id": "eleven_multilingual_v2", "language_code": lang,
                       "voice_settings": {"stability": 0.6, "similarity_boost": 0.75, "style": 0.1, "speed": 0.92}}).encode()
    r = urllib.request.Request(f"https://api.elevenlabs.io/v1/text-to-speech/{voice}?output_format=mp3_44100_64", data=body,
                               headers={"xi-api-key": key, "Content-Type": "application/json"})
    open(path, "wb").write(urllib.request.urlopen(r, timeout=60).read())
    used += len(m["text"]); n += 1
print(f"{n} Aufnahmen erzeugt, {used} Zeichen verbraucht, {len(os.listdir(out))}/{len(wanted)} vorhanden")
