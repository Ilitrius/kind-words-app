package app.rpkindwords.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class KindWordsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = KindWordsWidget()

    /** Entfernte Widgets räumen ihre gespeicherten Daten auf. */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try { WidgetStore(context.applicationContext).remove(appWidgetIds) } finally { pending.finish() }
        }
    }
}

/** Zweiter und dritter Eintrag in der Widget-Auswahl: dieselbe Anzeige, andere Startgröße (breit 4x2, groß 4x3). */
class KindWordsWidgetWideReceiver : KindWordsWidgetReceiver()
class KindWordsWidgetLargeReceiver : KindWordsWidgetReceiver()
