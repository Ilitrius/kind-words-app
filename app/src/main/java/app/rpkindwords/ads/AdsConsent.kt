package app.rpkindwords.ads

import android.app.Activity
import android.content.Context
import app.rpkindwords.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Einwilligung (Google UMP) und Start des AdMob-SDK.
 * Das SDK wird erst gestartet, wenn UMP `canRequestAds()` meldet; vorher gibt es keinen Netzwerkkontakt der Werbung.
 */
class AdsConsent(context: Context) {
    private val appContext = context.applicationContext
    private val info: ConsentInformation = UserMessagingPlatform.getConsentInformation(appContext)
    private val initStarted = AtomicBoolean(false)
    private val gathering = AtomicBoolean(false)

    private val _adsReady = MutableStateFlow(false)
    /** true, sobald Werbung angefragt werden darf und das SDK gestartet ist. */
    val adsReady: StateFlow<Boolean> = _adsReady

    private val _privacyOptionsRequired = MutableStateFlow(false)
    /** true, wenn in den Einstellungen der Eintrag "Datenschutzoptionen für Werbung" erscheinen muss. */
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired

    /** Bei jedem Start einmal: Einwilligungsstatus aktualisieren, Formular bei Bedarf zeigen. */
    fun gather(activity: Activity) {
        if (!gathering.compareAndSet(false, true)) return
        val params = ConsentRequestParameters.Builder().apply {
            if (BuildConfig.DEBUG && BuildConfig.ADMOB_CONSENT_DEBUG_EEA) {
                setConsentDebugSettings(
                    ConsentDebugSettings.Builder(activity)
                        .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                        .build(),
                )
            }
        }.build()
        info.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    gathering.set(false)
                    refresh()
                }
            },
            {
                gathering.set(false)
                refresh()
            },
        )
        // Hat der Nutzer früher schon zugestimmt, darf es sofort losgehen (auch offline).
        refresh()
    }

    /** Öffnet das Formular der Datenschutzoptionen (Einwilligung ändern/widerrufen). */
    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { refresh() }
    }

    private fun refresh() {
        _privacyOptionsRequired.value =
            info.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        if (!info.canRequestAds()) {
            _adsReady.value = false
            return
        }
        if (initStarted.compareAndSet(false, true)) {
            CoroutineScope(Dispatchers.IO).launch {
                MobileAds.initialize(appContext) { _adsReady.value = info.canRequestAds() }
            }
        } else {
            _adsReady.value = true
        }
    }
}
