package app.rpkindwords.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.union
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import app.rpkindwords.MainViewModel
import app.rpkindwords.R
import app.rpkindwords.update.UpdateStatus

/** Nicht blockierender Update-Hinweis: Download läuft im Hintergrund, die App bleibt nutzbar. */
@Composable
private fun UpdateBanner(status: UpdateStatus, onUpdate: () -> Unit, onRestart: () -> Unit, onDismiss: () -> Unit) {
    val downloaded = status == UpdateStatus.DOWNLOADED
    Surface(color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(if (downloaded) R.string.update_ready else R.string.update_available),
                Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = if (downloaded) onRestart else onUpdate) {
                Text(stringResource(if (downloaded) R.string.update_restart else R.string.update_action))
            }
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.update_later)) }
        }
    }
}

private enum class Tab(val label: Int, val icon: ImageVector) {
    Home(R.string.nav_home, Icons.Filled.Home),
    Favorites(R.string.nav_favorites, Icons.Filled.Favorite),
    Mine(R.string.nav_mine, Icons.Filled.Create),
    Settings(R.string.nav_settings, Icons.Filled.Settings),
}

@Composable
fun MainScreen(
    vm: MainViewModel,
    updateStatus: UpdateStatus = UpdateStatus.NONE,
    onUpdate: () -> Unit = {},
    onRestartForUpdate: () -> Unit = {},
) {
    val tabState = rememberSaveable { mutableIntStateOf(0) }
    var tab by tabState
    var bannerDismissed by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    BackHandler(enabled = tab != 0) { tab = 0 }
    val donatePrompt by vm.donatePrompt.collectAsStateWithLifecycle()
    val onboardingSeen by vm.onboardingSeen.collectAsStateWithLifecycle()
    val speechContext = androidx.compose.ui.platform.LocalContext.current
    val speechMessages = mapOf(
        app.rpkindwords.speech.Speaker.Problem.MISSING_VOICE_DATA to stringResource(R.string.speech_missing_voice),
        app.rpkindwords.speech.Speaker.Problem.UNAVAILABLE to stringResource(R.string.speech_unavailable),
        app.rpkindwords.speech.Speaker.Problem.FAILED to stringResource(R.string.speech_failed),
        app.rpkindwords.speech.Speaker.Problem.NOTHING_TO_READ to stringResource(R.string.speech_nothing),
        app.rpkindwords.speech.Speaker.Problem.TRANSLATION_MODEL_MISSING to stringResource(R.string.translation_model_missing),
        app.rpkindwords.speech.Speaker.Problem.TRANSLATION_FAILED to stringResource(R.string.translation_failed),
    )
    androidx.compose.runtime.LaunchedEffect(Unit) {
        vm.translatedText.collect { text -> android.widget.Toast.makeText(speechContext, text, android.widget.Toast.LENGTH_LONG).show() }
    }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        vm.speechProblems.collect { problem ->
            android.widget.Toast.makeText(speechContext, speechMessages[problem].orEmpty(), android.widget.Toast.LENGTH_LONG).show()
            if (problem == app.rpkindwords.speech.Speaker.Problem.MISSING_VOICE_DATA) {
                app.rpkindwords.speech.VoiceSetup.open(speechContext)
            }
        }
    }
    // Einführung und Spendenbitte nie am selben Start: wurde die Einführung gezeigt, entfällt die Bitte bis zum nächsten Start.
    var onboardingShownThisStart by rememberSaveable { mutableStateOf(false) }
    if (onboardingSeen == false) onboardingShownThisStart = true
    when (onboardingSeen) {
        null -> { Box(Modifier.fillMaxSize()); return }
        false -> { OnboardingScreen(onFinished = vm::markOnboardingSeen); return }
        else -> Unit
    }
    val donateDialogOpen = donatePrompt && !onboardingShownThisStart && stringResource(R.string.donate_url).startsWith("https://")
    if (donateDialogOpen) DonateDialog(onDismiss = vm::dismissDonatePrompt)

    // Werbung: Einwilligung erst nach der Einführung abfragen; Banner nur auf Home und Favoriten, nie unter dem Spendendialog.
    val ads = (androidx.compose.ui.platform.LocalContext.current.applicationContext as app.rpkindwords.KindWordsApp).ads
    val adsActivity = androidx.activity.compose.LocalActivity.current
    androidx.compose.runtime.LaunchedEffect(Unit) { adsActivity?.let { ads.gather(it) } }
    val canRequestAds by ads.adsReady.collectAsStateWithLifecycle()
    val showAd = canRequestAds && !donateDialogOpen && (tab == Tab.Home.ordinal || tab == Tab.Favorites.ordinal)
    Scaffold(
        // Ausschnitte (Kamera-Notch) im Querformat nicht überdecken
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.displayCutout),
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            // Große Systemschrift: Labels der Leiste begrenzen, damit sie nicht mitten im Wort umbrechen.
            val density = LocalDensity.current
            Column {
            if (showAd) app.rpkindwords.ads.AdBanner(app.rpkindwords.BuildConfig.ADMOB_BANNER_ID)
            CompositionLocalProvider(LocalDensity provides Density(density.density, density.fontScale.coerceAtMost(1.3f))) {
            TabBar(tabState)
            }
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (updateStatus != UpdateStatus.NONE && !bannerDismissed) {
                UpdateBanner(updateStatus, onUpdate, onRestartForUpdate, onDismiss = { bannerDismissed = true })
            }
            Box(Modifier.weight(1f)) {
                when (Tab.entries[tab]) {
                    Tab.Home -> HomeScreen(vm, snackbar)
                    Tab.Favorites -> FavoritesScreen(vm)
                    Tab.Mine -> MineScreen(vm)
                    Tab.Settings -> SettingsScreen(vm)
                }
            }
        }
    }
}

/** Untere Leiste: ein Balken zeigt den Reiter und folgt beim Ziehen (gedrückt halten) dem Finger; beim Loslassen rastet er ein. */
@Composable
private fun TabBar(tabState: MutableIntState) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val count = Tab.entries.size
    val scope = rememberCoroutineScope()
    val position = remember { Animatable(tabState.intValue.toFloat()) }
    var dragging by remember { mutableStateOf(false) }
    LaunchedEffect(tabState.intValue, dragging) {
        if (!dragging) position.animateTo(tabState.intValue.toFloat())
    }
    val indicator = MaterialTheme.colorScheme.secondaryContainer
    Box(Modifier.background(NavigationBarDefaults.containerColor)) {
        NavigationBar(
            Modifier
                .drawBehind {
                    val itemWidth = size.width / count
                    val centerX = (position.value + 0.5f) * itemWidth
                    val x = if (rtl) size.width - centerX else centerX
                    val w = 64.dp.toPx()
                    val h = 32.dp.toPx()
                    drawRoundRect(indicator, Offset(x - w / 2, 12.dp.toPx()), Size(w, h), CornerRadius(h / 2))
                }
                .pointerInput(rtl) {
                    // Einfache Taps bleiben bei den Items; erst eine Bewegung startet das Ziehen.
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        do {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull() ?: break
                            if (change.positionChanged() && abs(change.position.x - change.previousPosition.x) > 0f) {
                                dragging = true
                                val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                                val p = ((if (rtl) 1f - fraction else fraction) * count - 0.5f).coerceIn(0f, count - 1f)
                                scope.launch { position.snapTo(p) }
                                val target = p.roundToInt()
                                if (target != tabState.intValue) tabState.intValue = target
                                change.consume()
                            }
                        } while (event.changes.any { it.pressed })
                        dragging = false
                    }
                },
            containerColor = Color.Transparent,
        ) {
            Tab.entries.forEachIndexed { i, t ->
                NavigationBarItem(
                    selected = tabState.intValue == i,
                    onClick = { tabState.intValue = i },
                    icon = { Icon(t.icon, contentDescription = null) },
                    label = { Text(stringResource(t.label), maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
                )
            }
        }
    }
}
