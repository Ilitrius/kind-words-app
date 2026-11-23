package app.rpkindwords.ads

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.rpkindwords.R
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.RequestConfiguration
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Einwilligung (Google UMP) und Initialisierung des AdMob-SDK.
 * Anzeigen werden erst angefragt, wenn [canRequestAds] true ist (Einwilligung liegt vor bzw. ist nicht nötig).
 */
class AdsController {
    var canRequestAds by mutableStateOf(false)
        private set
    var privacyOptionsRequired by mutableStateOf(false)
        private set

    private var initialized = false

    @Volatile
    private var sdkReady = false

    /**
     * Nach der Einführung aufrufen: Einwilligungsstatus aktualisieren, ggf. Formular zeigen, dann SDK starten.
     * In den ersten [AdsEligibility.AD_FREE_DAYS] Tagen passiert nichts (keine Werbung, kein Einwilligungsformular).
     */
    fun start(activity: Activity, firstRunAtMillis: Long, nowMillis: Long = System.currentTimeMillis()) {
        if (!AdsEligibility.adsAllowed(firstRunAtMillis, nowMillis)) return
        val info = UserMessagingPlatform.getConsentInformation(activity)
        info.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { update(activity, info) }
            },
            { update(activity, info) }, // Fehler: ggf. früher erteilte Einwilligung weiter nutzen
        )
        // Früher erteilte Einwilligung erlaubt Anzeigen sofort, ohne auf das Netzwerk zu warten.
        update(activity, info)
    }

    /** Datenschutzoptionen (Einwilligung ändern) erneut anzeigen. */
    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            update(activity, UserMessagingPlatform.getConsentInformation(activity))
        }
    }

    private fun update(activity: Activity, info: ConsentInformation) {
        privacyOptionsRequired =
            info.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        val allowed = info.canRequestAds()
        if (!allowed) {
            canRequestAds = false
        } else if (initialized) {
            canRequestAds = sdkReady
        } else {
            initialized = true
            val appId = activity.getString(R.string.admob_app_id)
            // Testgeräte (siehe ads.xml) bekommen immer nur Test-Anzeigen, auch im Release-Build: schützt vor Eigenklicks.
            val testDevices = activity.getString(R.string.admob_test_device_ids).split(',').map { it.trim() }.filter { it.isNotEmpty() }
            val request = RequestConfiguration.Builder()
                .setMaxAdContentRating(RequestConfiguration.MaxAdContentRating.MAX_AD_CONTENT_RATING_T) // Zielgruppe 16+
                .setTestDeviceIds(testDevices)
                .build()
            val app = activity.applicationContext
            CoroutineScope(Dispatchers.IO).launch {
                // Banner erst nach abgeschlossener Initialisierung anfragen, sonst IllegalStateException.
                MobileAds.initialize(app, InitializationConfig.Builder(appId).setRequestConfiguration(request).build()) {
                    sdkReady = true
                    canRequestAds = true
                }
            }
        }
    }
}
