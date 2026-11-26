package app.rpkindwords.ui

import androidx.annotation.DrawableRes
import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.rpkindwords.R

/** Maskottchen Kiko (rein dekorativ, daher ohne Beschreibung für Screenreader). */
@Composable
fun Mascot(@DrawableRes pose: Int, modifier: Modifier = Modifier, size: Dp = 160.dp) {
    Image(painterResource(pose), contentDescription = null, modifier = modifier.size(size))
}

/**
 * Kiko mit dem Herz für die Titelzeile: streckt es nach vorn (Figur wächst zum Betrachter) und zieht es wieder zu sich.
 * Bei abgeschalteten Animationen (Systemeinstellung) bleibt das Bild ruhig.
 */
@Composable
fun MascotOffering(modifier: Modifier = Modifier, size: Dp = 56.dp) {
    val context = LocalContext.current
    val animate = remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
    }
    val scale = if (animate) {
        rememberInfiniteTransition(label = "kiko").animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                keyframes {
                    durationMillis = 2600
                    1f at 0
                    1.24f at 800 using FastOutSlowInEasing
                    1.24f at 1200
                    1f at 2000 using FastOutSlowInEasing
                    1f at 2600
                },
            ),
            label = "kikoScale",
        ).value
    } else {
        1f
    }
    Image(
        painterResource(R.drawable.kiko_pose2_herz),
        contentDescription = null,
        modifier = modifier.size(size).graphicsLayer {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0.5f, 1f)
        },
    )
}
