package app.rpkindwords.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import app.rpkindwords.R
import app.rpkindwords.data.LocaleContext
import app.rpkindwords.data.MessageRepository
import app.rpkindwords.data.SettingsRepository
import app.rpkindwords.ui.categoryLabel
import app.rpkindwords.ui.theme.KindWordsTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/** Wird beim Hinzufügen (und beim Neukonfigurieren) des Widgets gezeigt: Kategorie wählen. */
class WidgetConfigActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val lang = runBlocking { SettingsRepository(newBase).contentLanguageOverride.first() }
        super.attachBaseContext(LocaleContext.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Wie MainActivity randlos auf allen Android-Versionen; Insets behandelt ConfigScreen selbst.
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        setResult(RESULT_CANCELED)
        val widgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) { finish(); return }
        val store = WidgetStore(applicationContext)
        val initial = runBlocking { store.get(widgetId).category }
        setContent {
            KindWordsTheme {
                ConfigScreen(initial) { chosen ->
                    lifecycleScope.launch {
                        store.setCategory(widgetId, chosen)
                        runCatching {
                            val glanceId = GlanceAppWidgetManager(applicationContext).getGlanceIdBy(widgetId)
                            KindWordsWidget().update(applicationContext, glanceId)
                        }
                        setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                        finish()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@androidx.compose.runtime.Composable
private fun ConfigScreen(initial: String?, onDone: (String?) -> Unit) {
    var selected by remember { mutableStateOf(initial) }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.displayCutout))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.widget_config_title), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.widget_config_hint), style = MaterialTheme.typography.bodyMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selected == null, onClick = { selected = null }, label = { Text(stringResource(R.string.category_all)) })
                MessageRepository.categories.forEach { c ->
                    FilterChip(selected = selected == c, onClick = { selected = c }, label = { Text(categoryLabel(c)) })
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onDone(selected) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.widget_config_done)) }
        }
    }
}
