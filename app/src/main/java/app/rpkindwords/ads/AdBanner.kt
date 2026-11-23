package app.rpkindwords.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import app.rpkindwords.R
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError

/** Verankertes adaptives Banner; Aufrufer zeigt es nur, wenn die Einwilligung Anzeigen erlaubt. */
@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) return
    val context = LocalContext.current
    val unitId = stringResource(R.string.admob_banner_unit_id)
    val width = LocalConfiguration.current.screenWidthDp
    val adView = remember { AdView(context) }
    LaunchedEffect(width) {
        val size = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
        adView.loadAd(
            BannerAdRequest.Builder(unitId, size).build(),
            object : AdLoadCallback<BannerAd> {
                override fun onAdLoaded(ad: BannerAd) {}
                override fun onAdFailedToLoad(adError: LoadAdError) {}
            },
        )
    }
    DisposableEffect(adView) { onDispose { adView.destroy() } }
    AndroidView(modifier = modifier.fillMaxWidth().wrapContentSize(), factory = { adView })
}
