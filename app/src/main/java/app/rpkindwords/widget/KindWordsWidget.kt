package app.rpkindwords.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.Action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.LocalSize
import app.rpkindwords.KindWordsApp
import app.rpkindwords.MainActivity
import app.rpkindwords.R
import app.rpkindwords.data.Languages
import app.rpkindwords.data.LocaleContext
import app.rpkindwords.ui.theme.DarkColors
import app.rpkindwords.ui.theme.LightColors
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

private val SMALL = DpSize(110.dp, 110.dp)
private val WIDE = DpSize(250.dp, 110.dp)
private val LARGE = DpSize(250.dp, 180.dp)

/** Anzeige des Widgets: der Satz in der Inhaltssprache der App, passend zur gewählten Kategorie. */
private class WidgetView(
    val appName: String,
    val text: String?,
    val newLabel: String,
    val shareLabel: String,
    val favoriteLabel: String,
    val emptyLabel: String,
    val shareAction: Action?,
    val favorite: Boolean,
)

object WidgetContent {
    /** Inhaltssprache wie bei den Erinnerungen: gewählte Sprache oder Systemsprache. */
    suspend fun language(app: KindWordsApp): String =
        app.settings.contentLanguageOverride.first() ?: Languages.resolve(Locale.getDefault().language)

    /** Wählt einen neuen Satz (anderen als den aktuellen) und speichert ihn; null, wenn noch keine Inhalte da sind. */
    suspend fun pickNew(app: KindWordsApp, store: WidgetStore, widgetId: Int, current: WidgetData, lang: String): WidgetData? {
        app.messages.ensureSeeded()
        val repo = app.messages
        val next = repo.random(lang, current.category, current.messageId.ifBlank { null })
            ?: repo.random(lang, current.category, null)
            ?: return null
        val text = repo.styled(next, repo.styleProfile()).text
        val day = LocalDate.now().toEpochDay()
        store.setSentence(widgetId, next.id, text, lang, day)
        repo.recordShown(next.id)
        return current.copy(messageId = next.id, text = text, lang = lang, epochDay = day)
    }

    /** Liefert den Satz des Widgets und erneuert ihn bei Tageswechsel, Sprachwechsel oder Kategoriewechsel. */
    suspend fun ensure(app: KindWordsApp, store: WidgetStore, widgetId: Int, lang: String): WidgetData? {
        val data = store.get(widgetId)
        if (!WidgetRules.needsNewSentence(data, LocalDate.now().toEpochDay(), lang)) return data
        return pickNew(app, store, widgetId, data, lang) ?: data.takeIf { it.text.isNotBlank() }
    }
}

class KindWordsWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(setOf(SMALL, WIDE, LARGE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as KindWordsApp
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        suspend fun load(): WidgetView {
            val lang = WidgetContent.language(app)
            val data = runCatching { WidgetContent.ensure(app, WidgetStore(context), widgetId, lang) }.getOrNull()
            val ctx = LocaleContext.wrap(context, lang)
            val share = data?.takeIf { it.text.isNotBlank() }?.let {
                actionStartActivity<WidgetShareActivity>(actionParametersOf(WidgetShareActivity.TEXT to it.text))
            }
            val favorite = data?.messageId?.takeIf { it.isNotBlank() }?.let { runCatching { app.messages.isFavorite(it) }.getOrDefault(false) } ?: false
            return WidgetView(
                appName = ctx.getString(R.string.app_name),
                text = data?.text?.takeIf { it.isNotBlank() },
                newLabel = ctx.getString(R.string.widget_new),
                shareLabel = ctx.getString(R.string.action_share),
                favoriteLabel = ctx.getString(if (favorite) R.string.favorite_remove else R.string.favorite_add),
                emptyLabel = ctx.getString(R.string.widget_empty),
                shareAction = share,
                favorite = favorite,
            )
        }

        val first = load()
        provideContent {
            // Glance hält die Sitzung eine Weile offen; ein Update berechnet dann nur neu zusammen, nicht provideGlance.
            // Deshalb lädt der Inhalt bei jedem Signal (Sprachwechsel, "Neu") selbst neu.
            var view by remember { mutableStateOf(first) }
            LaunchedEffect(Unit) { WidgetRefresh.signal.collect { view = load() } }
            GlanceTheme(colors = ColorProviders(light = LightColors, dark = DarkColors)) { WidgetBody(view) }
        }
    }
}

/** Meldet laufenden Widget-Sitzungen, dass sich Sprache oder Satz geändert haben und neu geladen werden muss. */
object WidgetRefresh {
    val signal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    fun notifyChanged() { signal.tryEmit(Unit) }
}

/**
 * Drei Größenstufen (klein, breit, groß). Die Knopfleiste (Neu, Teilen, Favorit) steht immer unten und hat Vorrang vor dem Satz:
 * klein nur mit Symbolen, breit und groß mit Beschriftung; der App-Name erscheint nur in der großen Stufe.
 */
@Composable
private fun WidgetBody(v: WidgetView) {
    val size = LocalSize.current
    val wide = size.width >= WIDE.width
    val large = size.height >= LARGE.height
    val colors = GlanceTheme.colors
    Box(
        modifier = GlanceModifier.fillMaxSize()
            .background(colors.primaryContainer)
            .cornerRadius(24.dp)
            .padding(if (wide) 12.dp else 10.dp)
            .clickable(actionStartActivity<MainActivity>()),
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            if (large) {
                Text(
                    text = v.appName,
                    maxLines = 1,
                    style = TextStyle(color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold),
                )
                Spacer(GlanceModifier.height(6.dp))
            }
            Box(modifier = GlanceModifier.fillMaxWidth().defaultWeight(), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = v.text ?: v.emptyLabel,
                    maxLines = if (large) 6 else if (wide) 4 else 5,
                    style = TextStyle(
                        color = colors.onPrimaryContainer,
                        fontSize = if (large) 22.sp else if (wide) 16.sp else 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                    ),
                )
            }
            if (v.text != null) {
                Spacer(GlanceModifier.height(6.dp))
                Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    ActionButton(R.drawable.ic_widget_refresh, if (wide) v.newLabel else null, v.newLabel, actionRunCallback<NextSentenceAction>(), GlanceModifier.defaultWeight())
                    Spacer(GlanceModifier.width(if (wide) 8.dp else 4.dp))
                    v.shareAction?.let {
                        ActionButton(R.drawable.ic_widget_share, if (wide) v.shareLabel else null, v.shareLabel, it, GlanceModifier.defaultWeight())
                        Spacer(GlanceModifier.width(if (wide) 8.dp else 4.dp))
                    }
                    ActionButton(
                        if (v.favorite) R.drawable.ic_widget_heart else R.drawable.ic_widget_heart_outline,
                        null, v.favoriteLabel, actionRunCallback<FavoriteAction>(),
                        if (wide) GlanceModifier.width(44.dp) else GlanceModifier.defaultWeight(),
                    )
                }
            }
        }
    }
}

/** Runder Knopf der Leiste: Symbol, auf breiten Widgets mit Beschriftung. */
@Composable
private fun ActionButton(icon: Int, label: String?, description: String, action: Action, modifier: GlanceModifier) {
    val colors = GlanceTheme.colors
    Box(
        modifier = modifier
            .height(34.dp)
            .background(colors.primary)
            .cornerRadius(17.dp)
            .clickable(action),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                provider = ImageProvider(icon),
                contentDescription = description,
                colorFilter = ColorFilter.tint(colors.onPrimary),
                modifier = GlanceModifier.size(18.dp),
            )
            if (label != null) {
                Spacer(GlanceModifier.width(4.dp))
                Text(label, maxLines = 1, style = TextStyle(color = colors.onPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium))
            }
        }
    }
}

/** "Neu": zeigt sofort einen anderen Satz derselben Kategorie. */
class NextSentenceAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: androidx.glance.action.ActionParameters) {
        val app = context.applicationContext as KindWordsApp
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
        val store = WidgetStore(context)
        runCatching { WidgetContent.pickNew(app, store, widgetId, store.get(widgetId), WidgetContent.language(app)) }
        KindWordsWidget().update(context, glanceId)
        WidgetRefresh.notifyChanged()
    }
}

/** Herz: merkt den angezeigten Satz als Favorit vor oder nimmt ihn wieder heraus (gleiche Daten wie die Favoriten in der App). */
class FavoriteAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: androidx.glance.action.ActionParameters) {
        val app = context.applicationContext as KindWordsApp
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
        val id = WidgetStore(context).get(widgetId).messageId
        if (id.isNotBlank()) runCatching { app.messages.setFavorite(id, !app.messages.isFavorite(id)) }
        KindWordsWidget().update(context, glanceId)
        WidgetRefresh.notifyChanged()
    }
}
