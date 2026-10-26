package app.rpkindwords

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import app.rpkindwords.data.LocaleContext
import app.rpkindwords.data.SettingsRepository
import app.rpkindwords.ui.MainScreen
import app.rpkindwords.ui.theme.KindWordsTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    /** Oberfläche in der gewählten Sprache anzeigen (Einstellungen > Sprache), sonst Systemsprache. */
    override fun attachBaseContext(newBase: Context) {
        val lang = runBlocking { SettingsRepository(newBase).contentLanguageOverride.first() }
        super.attachBaseContext(LocaleContext.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KindWordsTheme { MainScreen(vm) }
        }
    }
}
