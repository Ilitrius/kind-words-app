package app.rpkindwords.speech

import android.content.Context
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

    /** Lokale, installierte Stimmen der Sprache, stabil sortiert. */
    fun voices(lang: String, result: (List<String>) -> Unit) = ensure {
        val list = try { tts?.voices.orEmpty().filter { usable(it, lang) }.map { it.name }.sorted() } catch (_: Exception) { emptyList() }
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
            val voices = try { t.voices.orEmpty().filter { usable(it, lang) } } catch (_: Exception) { emptyList() }
            val chosen = voices.firstOrNull { it.name == voiceName } ?: voices.firstOrNull()
            if (chosen == null) { onProblem?.invoke(Problem.MISSING_VOICE_DATA); return@ensure }
            t.voice = chosen
            t.setSpeechRate(rate)
            t.setPitch(pitch)
            if (t.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "kindwords") == TextToSpeech.ERROR) onProblem?.invoke(Problem.FAILED)
        }
    }

    fun stop() { if (ready) tts?.stop() }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
        pending.clear()
    }

    companion object {
        /** Warm und ruhig: etwas tiefer als normal. */
        const val DEFAULT_PITCH = 0.95f
    }
}
