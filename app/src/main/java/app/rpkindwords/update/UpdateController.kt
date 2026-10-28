package app.rpkindwords.update

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class UpdateStatus { NONE, AVAILABLE, DOWNLOADED }

/**
 * Prüft über Google Play auf Updates (nur Versionsabgleich durch Play, die App sendet nichts) und startet
 * den flexiblen Ablauf: Download im Hintergrund, die App bleibt nutzbar. Außerhalb von Play (Sideload,
 * Emulator) schlägt die Prüfung still fehl und es erscheint kein Hinweis.
 */
class UpdateController(context: Context) {
    private val manager = AppUpdateManagerFactory.create(context)
    private var info: AppUpdateInfo? = null
    private val _status = MutableStateFlow(UpdateStatus.NONE)
    val status: StateFlow<UpdateStatus> = _status

    private val listener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) _status.value = UpdateStatus.DOWNLOADED
    }

    fun register() = manager.registerListener(listener)
    fun unregister() = manager.unregisterListener(listener)

    /** Einmaliger Abruf; Aufrufer drosselt (siehe SettingsRepository.shouldCheckUpdate). */
    fun check() {
        manager.appUpdateInfo
            .addOnSuccessListener { i ->
                info = i
                _status.value = when {
                    i.installStatus() == InstallStatus.DOWNLOADED -> UpdateStatus.DOWNLOADED
                    i.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        i.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> UpdateStatus.AVAILABLE
                    else -> UpdateStatus.NONE
                }
            }
            .addOnFailureListener { /* kein Play-Store o. Ä.: ignorieren */ }
    }

    fun startUpdate(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        val i = info ?: return
        runCatching {
            manager.startUpdateFlowForResult(i, launcher, AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build())
        }
    }

    fun completeUpdate() {
        manager.completeUpdate()
    }
}
