package app.rpkindwords.speech

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.core.net.toUri

/**
 * Hilft, fehlende Sprachdaten nachzuladen. Hersteller (z. B. Samsung) behandeln die Standard-Absicht unterschiedlich,
 * darum die Reihenfolge: Einstellungen der Sprachausgabe, Standard-Installation, zuletzt Google-Sprachdienste im Play Store.
 */
object VoiceSetup {
    private const val TTS_SETTINGS = "com.android.settings.TTS_SETTINGS"
    private const val GOOGLE_TTS_PACKAGE = "com.google.android.tts"

    fun open(context: Context): Boolean {
        val attempts = listOf(
            Intent(TTS_SETTINGS),
            Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA),
            Intent(Intent.ACTION_VIEW, "market://details?id=$GOOGLE_TTS_PACKAGE".toUri()),
        )
        for (intent in attempts) {
            try {
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            } catch (_: ActivityNotFoundException) {
            } catch (_: SecurityException) {
            }
        }
        return false
    }
}
