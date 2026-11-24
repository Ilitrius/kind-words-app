package app.rpkindwords.widget

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.glance.action.ActionParameters
import app.rpkindwords.reminder.ShareIntents

/**
 * Unsichtbare Zwischenstation für "Teilen" im Widget: Glance hängt an jeden Intent eine eigene Daten-URI an, damit
 * wäre ein Chooser-Intent direkt im Widget nicht startbar (ActivityNotFoundException). Hier wird der Teilen-Dialog
 * aus einer normalen Activity gestartet und die Activity sofort wieder beendet.
 */
class WidgetShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.getStringExtra(TEXT.name)?.takeIf { it.isNotBlank() }?.let {
            startActivity(Intent.createChooser(ShareIntents.send(it), null))
        }
        finish()
    }

    companion object {
        val TEXT = ActionParameters.Key<String>("share_text")
    }
}
