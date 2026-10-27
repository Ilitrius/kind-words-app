package app.rpkindwords.update

import android.app.Activity
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.rpkindwords.R
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

enum class UpdateState { None, Available, Downloaded }

/**
 * Flexibler Play-In-App-Update-Flow. Prüft nur bei App-Start/Resume (höchstens alle 6 Stunden),
 * ohne Polling und ohne Hintergrunddienst. Fehlen die Play-Dienste oder ist die App nicht über
 * Google Play installiert, passiert nichts (alle Fehler werden still verworfen).
 */
class InAppUpdater(private val activity: ComponentActivity) {
    var state by mutableStateOf(UpdateState.None)
        private set

    private val manager: AppUpdateManager? =
        runCatching { AppUpdateManagerFactory.create(activity.applicationContext) }.getOrNull()
    private var pending: AppUpdateInfo? = null
    private var lastCheck = -MIN_INTERVAL_MS
    private var listening = false

    private val launcher = activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        // Nutzer hat abgelehnt: bis zum nächsten App-Start nicht mehr nerven.
        if (result.resultCode != Activity.RESULT_OK && state == UpdateState.Available) state = UpdateState.None
    }

    private val listener = InstallStateUpdatedListener { install ->
        when (install.installStatus()) {
            InstallStatus.DOWNLOADED -> { state = UpdateState.Downloaded; stopListening() }
            InstallStatus.FAILED, InstallStatus.CANCELED -> { state = UpdateState.None; stopListening() }
            else -> Unit
        }
    }

    /** In onResume aufrufen. */
    fun check() {
        val m = manager ?: return
        if (state == UpdateState.Downloaded) return
        val now = SystemClock.elapsedRealtime()
        if (now - lastCheck < MIN_INTERVAL_MS) return
        lastCheck = now
        runCatching {
            m.appUpdateInfo
                .addOnSuccessListener { info ->
                    when {
                        info.installStatus() == InstallStatus.DOWNLOADED -> state = UpdateState.Downloaded
                        info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                            info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                            pending = info
                            if (state == UpdateState.None) state = UpdateState.Available
                        }
                    }
                }
                .addOnFailureListener { /* keine Play-Dienste / nicht aus Play installiert: ignorieren */ }
        }
    }

    fun startUpdate() {
        val m = manager ?: return
        val info = pending ?: return
        runCatching {
            if (!listening) { m.registerListener(listener); listening = true }
            m.startUpdateFlowForResult(info, launcher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
        }.onFailure { stopListening(); state = UpdateState.None }
    }

    fun completeUpdate() {
        runCatching { manager?.completeUpdate() }
    }

    /** In onDestroy aufrufen. */
    fun release() = stopListening()

    private fun stopListening() {
        if (listening) { runCatching { manager?.unregisterListener(listener) }; listening = false }
    }

    private companion object {
        const val MIN_INTERVAL_MS = 6 * 60 * 60 * 1000L
    }
}

/** Zeigt Hinweis "Update verfügbar" (Aktualisieren) bzw. "Bereit" (Neu starten) als Snackbar. */
@Composable
fun UpdateSnackbars(updater: InAppUpdater?, host: SnackbarHostState) {
    if (updater == null) return
    val available = stringResource(R.string.update_available)
    val ready = stringResource(R.string.update_ready)
    val update = stringResource(R.string.update_action)
    val restart = stringResource(R.string.update_restart)
    LaunchedEffect(updater.state) {
        when (updater.state) {
            UpdateState.Available ->
                if (host.showSnackbar(available, update, duration = SnackbarDuration.Long) == SnackbarResult.ActionPerformed) updater.startUpdate()
            UpdateState.Downloaded ->
                if (host.showSnackbar(ready, restart, duration = SnackbarDuration.Indefinite) == SnackbarResult.ActionPerformed) updater.completeUpdate()
            UpdateState.None -> Unit
        }
    }
}
