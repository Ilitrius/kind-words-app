package app.rpkindwords

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import app.rpkindwords.data.LocaleContext
import app.rpkindwords.data.SettingsRepository
import app.rpkindwords.ui.MainScreen
import app.rpkindwords.update.UpdateController
import app.rpkindwords.ui.theme.KindWordsTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()
    private val updates by lazy { UpdateController(this) }
    private val updateLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { /* Status kommt über den Install-Listener */ }

    /** Oberfläche in der gewählten Sprache anzeigen (Einstellungen > Sprache), sonst Systemsprache. */
    override fun attachBaseContext(newBase: Context) {
        val lang = runBlocking { SettingsRepository(newBase).contentLanguageOverride.first() }
        super.attachBaseContext(LocaleContext.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updates.register()
        lifecycleScope.launch {
            if (SettingsRepository(applicationContext).shouldCheckUpdate()) updates.check()
        }
        setContent {
            val updateStatus by updates.status.collectAsStateWithLifecycle()
            KindWordsTheme {
                MainScreen(
                    vm,
                    updateStatus = updateStatus,
                    onUpdate = { updates.startUpdate(updateLauncher) },
                    onRestartForUpdate = { updates.completeUpdate() },
                )
            }
        }
    }

    override fun onDestroy() {
        updates.unregister()
        super.onDestroy()
    }
}
