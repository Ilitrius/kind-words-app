package app.rpkindwords.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.rpkindwords.R
import app.rpkindwords.data.MessageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Kategorie-Auswahl als Chip-Reihe mit gleitendem Balken (wie die untere Leiste).
 * Tippen wählt eine Kategorie, normales Wischen scrollt die Reihe. Gedrückt halten und Ziehen:
 * der Balken folgt dem Finger, die Kategorie wechselt beim Überfahren, am Rand scrollt die Reihe mit.
 */
@Composable
fun CategoryBar(selected: String?, onSelect: (String?) -> Unit, modifier: Modifier = Modifier) {
    val ids = remember { listOf<String?>(null) + MessageRepository.categories }
    val selectedIndex = ids.indexOf(selected).coerceAtLeast(0)
    val state = rememberLazyListState()
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val position = remember { Animatable(selectedIndex.toFloat()) }
    var dragging by remember { mutableStateOf(false) }
    var dragX by remember { mutableFloatStateOf(0f) }
    var width by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val indicator = MaterialTheme.colorScheme.secondaryContainer
    val currentOnSelect by rememberUpdatedState(onSelect)
    val currentIndex by rememberUpdatedState(selectedIndex)

    LaunchedEffect(selectedIndex, dragging) {
        if (!dragging) position.animateTo(selectedIndex.toFloat())
    }

    // Alle Maße hier in "logischer" Richtung (Start = links bei LTR, rechts bei RTL).
    fun updateFromDrag() {
        val items = state.layoutInfo.visibleItemsInfo
        if (items.isEmpty()) return
        val x = if (rtl) width - dragX else dragX
        val first = items.first()
        val last = items.last()
        val p = when {
            x <= first.offset + first.size / 2f -> first.index.toFloat()
            x >= last.offset + last.size / 2f -> last.index.toFloat()
            else -> {
                val i = items.indexOfLast { x >= it.offset + it.size / 2f }
                val a = items[i]
                val b = items[i + 1]
                val ca = a.offset + a.size / 2f
                val cb = b.offset + b.size / 2f
                a.index + (x - ca) / (cb - ca)
            }
        }
        scope.launch { position.snapTo(p) }
        val target = p.roundToInt().coerceIn(0, ids.lastIndex)
        if (target != currentIndex) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            currentOnSelect(ids[target])
        }
    }

    // Am Rand weiterscrollen, solange der Finger dort bleibt.
    LaunchedEffect(dragging) {
        val edge = with(density) { 48.dp.toPx() }
        while (dragging) {
            val x = if (rtl) width - dragX else dragX
            val delta = when {
                x < edge -> -18f
                x > width - edge -> 18f
                else -> 0f
            }
            if (delta != 0f) {
                state.scrollBy(delta)
                updateFromDrag()
            }
            delay(16)
        }
    }

    LazyRow(
        state = state,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .onSizeChanged { width = it.width }
            .drawBehind {
                val items = state.layoutInfo.visibleItemsInfo
                val p = position.value
                val i = floor(p).toInt().coerceIn(0, ids.lastIndex)
                val f = (p - i).coerceIn(0f, 1f)
                val a = items.firstOrNull { it.index == i }
                val b = items.firstOrNull { it.index == minOf(i + 1, ids.lastIndex) }
                if (a != null && (b != null || f == 0f)) {
                    val other = b ?: a
                    val start = a.offset + (other.offset - a.offset) * f
                    val w = a.size + (other.size - a.size) * f
                    val left = if (rtl) size.width - start - w else start
                    // Chip ist 32 dp hoch und im 48-dp-Tippbereich zentriert; der Balken deckt nur den sichtbaren Chip.
                    val h = FilterChipDefaults.Height.toPx()
                    drawRoundRect(indicator, Offset(left, (size.height - h) / 2f), Size(w, h), CornerRadius(8.dp.toPx()))
                }
            }
            .pointerInput(rtl) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    // Erst nach langem Drücken ohne Bewegung beginnt das Gleiten; sonst bleibt es bei Tippen/Scrollen.
                    val aborted = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                        var stop = false
                        while (!stop) {
                            val change = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull { it.id == down.id }
                            stop = change == null || !change.pressed ||
                                (change.position - down.position).getDistance() > viewConfiguration.touchSlop
                        }
                        true
                    }
                    if (aborted != null) return@awaitEachGesture
                    dragging = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    dragX = down.position.x
                    updateFromDrag()
                    try {
                        var pressed = true
                        while (pressed) {
                            val change = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull { it.id == down.id } ?: break
                            dragX = change.position.x
                            updateFromDrag()
                            change.consume()
                            pressed = change.pressed
                        }
                    } finally {
                        dragging = false
                    }
                }
            },
    ) {
        items(ids.size) { i ->
            val id = ids[i]
            FilterChip(
                selected = i == selectedIndex,
                onClick = { onSelect(id) },
                label = { Text(if (id == null) stringResource(R.string.category_all) else categoryLabel(id)) },
                // Der gleitende Balken liefert die Füllung der gewählten Kategorie.
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.Transparent),
            )
        }
    }
}
