---
name: emulator-howto
description: Funktionierende adb/Emulator-Rezepte und Fallstricke für Kind Words (Windows, Git Bash)
metadata:
  type: reference
---

- Git Bash: `export MSYS_NO_PATHCONV=1` sonst werden /sdcard-Pfade verbogen. Screenshot: `adb exec-out screencap -p > datei.png`.
- Emulator: `emulator.exe -avd Pixel_API35`; `adb root` funktioniert (google_apis). Kein bundletool installiert -> Release per `assembleRelease` (signiert, keystore.properties) testen statt AAB.
- Debug- und Release-APK sind unterschiedlich signiert: Wechsel erfordert `adb uninstall app.rpkindwords`.
- Alarm-Test ohne Warten: `adb shell settings put global auto_time 0; adb shell date MMDDhhmmYYYY.ss` kurz vor `dumpsys alarm | grep -A2 "app.rpkindwords}"` (origWhen); Alarm feuert (window +~45 s abwarten), Notification via `dumpsys notification --noredact`. `auto_time 1` danach. Reboot setzt Uhr zurück.
- `am broadcast` an ReminderReceiver (exported=false) funktioniert nicht von der Shell.
- Nach Reboot: Alarm wird neu geplant, aber Standby-Bucket NEVER -> +364 Tage; `am set-standby-bucket app.rpkindwords active`.
- UI: uiautomator dump bricht bei Emojis/manchen Knoten im Parser ab; Toasts nicht im Dump -> Screenshot 0.7 s nach Tap. `adb input text` mangelt Sonderzeichen/% -> Testdaten lieber als JSON per `adb push` nach /sdcard/Download und Import-Picker (Roots-Menü 73,199 -> Downloads 441,650; Picker startet jedes Mal bei "Recent").
- Backup-Testdateien (valid/broken/empty/huge >1 MB/notjson/wrong) lagen in scratchpad; Generator war ein Python-Heredoc (PYTHONUTF8=1 nötig).
- Chrome sperren für "kein Browser": `pm disable-user --user 0 com.android.chrome` (danach `pm enable`).
- Dark/Schrift: `cmd uimode night yes|no`, `settings put system font_scale 2.0`; Rotation: accelerometer_rotation 0 + user_rotation 1. `low_power 1` schaltet im Emulator auch Dunkelmodus ein.
