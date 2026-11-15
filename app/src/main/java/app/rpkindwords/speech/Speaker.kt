package app.rpkindwords.speech

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.Locale

/**
 * Liest Sätze mit der Sprachausgabe des Geräts vor. Nur lokale Stimmen (keine Netzwerk-Stimmen, damit kein Text das Gerät verlässt).
 * Alle Aufrufe auf dem Hauptthread.
 */
class Speaker(context: Context) {
    enum class Problem { UNAVAILABLE, MISSING_VOICE_DATA, NOTHING_TO_READ, FAILED, TRANSLATION_MODEL_MISSING, TRANSLATION_FAILED }

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var ready = false
    private var failedInit = false
    private val pending = ArrayDeque<() -> Unit>()

    private var player: MediaPlayer? = null

    /**
     * Spielt eine mitgelieferte, vorab eingesprochene Aufnahme des Satzes ab (assets/voice/<lang>/<id>-<hash>.mp3, offline).
     * Der Hash gehört zum Satztext: wird der Satz geändert, passt die alte Aufnahme nicht mehr und die Gerätestimme springt ein.
     * Gibt false zurück, wenn es keine passende Aufnahme gibt.
     */
    fun playClip(lang: String, id: String, text: String): Boolean {
        val path = "voice/$lang/$id-${SpeechText.clipHash(text)}.mp3"
        return try {
            stop()
            appContext.assets.openFd(path).use { fd ->
                val mp = MediaPlayer()
                mp.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).setUsage(AudioAttributes.USAGE_MEDIA).build())
                mp.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                mp.setOnCompletionListener { it.release(); if (player === it) player = null }
                mp.setOnErrorListener { m, _, _ -> m.release(); if (player === m) player = null; true }
                mp.prepare()
                mp.start()
                player = mp
            }
            true
        } catch (_: java.io.IOException) {
            false
        } catch (_: RuntimeException) {
            false
        }
    }

    private fun stopClip() { try { player?.release() } catch (_: RuntimeException) {}; player = null }

    var onProblem: ((Problem) -> Unit)? = null

    fun report(problem: Problem) { onProblem?.invoke(problem) }

    private fun ensure(block: () -> Unit) {
        if (ready) { block(); return }
        if (failedInit) { onProblem?.invoke(Problem.UNAVAILABLE); return }
        pending.addLast(block)
        if (tts == null) {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    ready = true
                    while (pending.isNotEmpty()) pending.removeFirst().invoke()
                } else {
                    failedInit = true
                    pending.clear()
                    onProblem?.invoke(Problem.UNAVAILABLE)
                }
            }
        }
    }

    private fun usable(v: Voice, lang: String) =
        v.locale.language == lang && !v.isNetworkConnectionRequired && !v.features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)

    /** Beste Qualität zuerst (natürlichere Stimmen vorn), bei Gleichstand nach Name, damit die Reihenfolge stabil bleibt. */
    private fun ranked(all: Collection<Voice>, lang: String) =
        all.filter { usable(it, lang) }.sortedWith(compareByDescending<Voice> { it.quality }.thenBy { it.name })

    /** Lokale, installierte Stimmen der Sprache, beste zuerst. */
    fun voices(lang: String, result: (List<String>) -> Unit) = ensure {
        val list = try { ranked(tts?.voices.orEmpty(), lang).map { it.name } } catch (_: Exception) { emptyList() }
        result(list)
    }

    fun speak(text: String, lang: String, voiceName: String?, rate: Float, pitch: Float = DEFAULT_PITCH) {
        val clean = SpeechText.clean(text)
        if (!SpeechText.speakable(clean)) { onProblem?.invoke(Problem.NOTHING_TO_READ); return }
        ensure {
            val t = tts ?: return@ensure
            val set = t.setLanguage(Locale.forLanguageTag(lang))
            if (set == TextToSpeech.LANG_MISSING_DATA || set == TextToSpeech.LANG_NOT_SUPPORTED) {
                onProblem?.invoke(Problem.MISSING_VOICE_DATA)
                return@ensure
            }
            val voices = try { ranked(t.voices.orEmpty(), lang) } catch (_: Exception) { emptyList() }
            val chosen = voices.firstOrNull { it.name == voiceName } ?: voices.firstOrNull()
            if (chosen == null) { onProblem?.invoke(Problem.MISSING_VOICE_DATA); return@ensure }
            t.voice = chosen
            t.setSpeechRate(rate)
            t.setPitch(pitch)
            // Satzweise mit kurzer Pause, das klingt ruhiger und natürlicher als ein durchgehender Fluss
            val parts = clean.split(Regex("""(?<=[.!?…])\s+""")).filter { it.isNotBlank() }
            var failed = false
            parts.forEachIndexed { i, part ->
                val mode = if (i == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                if (i > 0) t.playSilentUtterance(SENTENCE_PAUSE_MS, TextToSpeech.QUEUE_ADD, "kindwords-pause-$i")
                if (t.speak(part, mode, null, "kindwords-$i") == TextToSpeech.ERROR) failed = true
            }
            if (failed) onProblem?.invoke(Problem.FAILED)
        }
    }

    fun stop() { stopClip(); if (ready) tts?.stop() }

    fun shutdown() {
        stopClip()
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
        pending.clear()
    }

    companion object {
        /** Warm und ruhig: etwas tiefer als normal. */
        const val DEFAULT_PITCH = 0.95f
        private const val SENTENCE_PAUSE_MS = 350L
    }
}
