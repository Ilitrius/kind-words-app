package app.rpkindwords.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.widgetDataStore: DataStore<Preferences> by preferencesDataStore("widgets")

/** Kategorie und aktueller Satz je Widget-ID, lokal in einer eigenen DataStore-Datei. */
class WidgetStore(private val context: Context) {
    private fun cat(id: Int) = stringPreferencesKey("cat_$id")
    private fun msg(id: Int) = stringPreferencesKey("msg_$id")
    private fun text(id: Int) = stringPreferencesKey("text_$id")
    private fun lang(id: Int) = stringPreferencesKey("lang_$id")
    private fun day(id: Int) = longPreferencesKey("day_$id")

    suspend fun get(id: Int): WidgetData {
        val p = context.widgetDataStore.data.first()
        return WidgetData(
            category = WidgetRules.sanitizeCategory(p[cat(id)]),
            messageId = p[msg(id)] ?: "",
            text = p[text(id)] ?: "",
            lang = p[lang(id)] ?: "",
            epochDay = p[day(id)] ?: -1L,
        )
    }

    /** Neue Kategorie: der gespeicherte Satz wird verworfen, damit sofort ein passender kommt. */
    suspend fun setCategory(id: Int, category: String?) {
        context.widgetDataStore.edit { p ->
            val c = WidgetRules.sanitizeCategory(category)
            if (c == null) p.remove(cat(id)) else p[cat(id)] = c
            p.remove(msg(id)); p.remove(text(id)); p.remove(lang(id)); p.remove(day(id))
        }
    }

    suspend fun setSentence(id: Int, messageId: String, sentence: String, language: String, epochDay: Long) {
        context.widgetDataStore.edit { p ->
            p[msg(id)] = messageId
            p[text(id)] = sentence
            p[lang(id)] = language
            p[day(id)] = epochDay
        }
    }

    suspend fun remove(ids: IntArray) {
        context.widgetDataStore.edit { p ->
            for (id in ids) {
                p.remove(cat(id)); p.remove(msg(id)); p.remove(text(id)); p.remove(lang(id)); p.remove(day(id))
            }
        }
    }
}
