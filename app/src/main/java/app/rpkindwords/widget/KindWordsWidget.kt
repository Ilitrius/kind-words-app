package app.rpkindwords.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity as actionStartIntent
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
import app.rpkindwords.reminder.ShareIntents
import app.rpkindwords.ui.theme.DarkColors
import app.rpkindwords.ui.theme.LightColors
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.flow.first

private val SMALL = DpSize(110.dp, 110.dp)
private val WIDE = DpSize(250.dp, 110.dp)

/** Anzeige des Widgets: der Satz in der Inhaltssprache der App, passend zur gewählten Kategorie. */
private class WidgetView(val text: String?, val newLabel: String, val shareLabel: String, val emptyLabel: String, val shareAction: Action?)

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
    override val sizeMode = SizeMode.Responsive(setOf(SMALL, WIDE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as KindWordsApp
        val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val lang = WidgetContent.language(app)
        val data = runCatching { WidgetContent.ensure(app, WidgetStore(context), widgetId, lang) }.getOrNull()
        val ctx = LocaleContext.wrap(context, lang)
        val share = data?.takeIf { it.text.isNotBlank() }?.let {
            actionStartIntent(Intent.createChooser(ShareIntents.send(it.text), null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        val view = WidgetView(
            text = data?.text?.takeIf { it.isNotBlank() },
            newLabel = ctx.getString(R.string.widget_new),
            shareLabel = ctx.getString(R.string.action_share),
            emptyLabel = ctx.getString(R.string.widget_empty),
            shareAction = share,
        )
        provideContent {
            GlanceTheme(colors = ColorProviders(light = LightColors, dark = DarkColors)) { WidgetBody(view) }
        }
    }
}

@Composable
private fun WidgetBody(v: WidgetView) {
    val wide = LocalSize.current.width >= WIDE.width
    val colors = GlanceTheme.colors
    Box(
        modifier = GlanceModifier.fillMaxSize()
            .background(colors.primaryContainer)
            .cornerRadius(24.dp)
            .padding(if (wide) 16.dp else 12.dp)
            .clickable(actionStartActivity<MainActivity>()),
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Box(modifier = GlanceModifier.fillMaxWidth().defaultWeight(), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = v.text ?: v.emptyLabel,
                    maxLines = if (wide) 5 else 6,
                    style = TextStyle(
                        color = colors.onPrimaryContainer,
                        fontSize = if (wide) 18.sp else 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                    ),
                )
            }
            if (v.text != null) {
                Spacer(GlanceModifier.height(8.dp))
                Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    ChipButton(v.newLabel, actionRunCallback<NextSentenceAction>())
                    Spacer(GlanceModifier.width(8.dp))
                    v.shareAction?.let { ChipButton(v.shareLabel, it) }
                }
            }
        }
    }
}

@Composable
private fun ChipButton(label: String, action: Action) {
    Box(
        modifier = GlanceModifier
            .background(GlanceTheme.colors.primary)
            .cornerRadius(16.dp)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clickable(action),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = TextStyle(color = GlanceTheme.colors.onPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium))
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
    }
}
