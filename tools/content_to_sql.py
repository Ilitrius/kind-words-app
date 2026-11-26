"""Erzeugt SQL, das die gebündelten Sätze (assets/messages/*.json) nach Supabase importiert.

Aufruf: python tools/content_to_sql.py <ausgabeordner>
Schreibt je Sprache Chunk-Dateien (messages_<lang>_<n>.sql) und zuletzt version_<lang>.sql.
Reihenfolge einhalten: erst alle Chunks, dann die Version (App sieht nie einen halben Stand).
Enthält keine Schlüssel; Ausführung über Supabase SQL-Editor/MCP.
"""
import json
import pathlib
import sys

ASSETS = pathlib.Path(__file__).resolve().parent.parent / "app/src/main/assets/messages"
CHUNK = 200


def q(s: str) -> str:
    return "'" + s.replace("'", "''") + "'"


def main(out: pathlib.Path) -> None:
    out.mkdir(parents=True, exist_ok=True)
    for f in sorted(ASSETS.glob("messages_*.json")):
        data = json.loads(f.read_text(encoding="utf-8"))
        lang, version, msgs = data["language"], data["version"], data["messages"]
        for n, i in enumerate(range(0, len(msgs), CHUNK)):
            rows = ",\n".join(
                f"({q(m['id'])},{q(lang)},{q(m['category'])},{q(m['text'])},{i + k},true)"
                for k, m in enumerate(msgs[i:i + CHUNK])
            )
            (out / f"messages_{lang}_{n:02d}.sql").write_text(
                "insert into public.messages (id,language,category,text,sort,active) values\n" + rows +
                "\non conflict (id) do update set language=excluded.language, category=excluded.category,"
                " text=excluded.text, sort=excluded.sort, active=true;\n", encoding="utf-8")
        ids = ",".join(q(m["id"]) for m in msgs)
        (out / f"version_{lang}.sql").write_text(
            f"update public.messages set active=false where language={q(lang)} and id not in ({ids});\n"
            f"insert into public.content_version (language,version) values ({q(lang)},{version})\n"
            f"on conflict (language) do update set version=excluded.version, updated_at=now();\n", encoding="utf-8")
        print(lang, version, len(msgs))


if __name__ == "__main__":
    main(pathlib.Path(sys.argv[1]))
