package app.rpkindwords.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.rpkindwords.R

private data class Step(val title: Int, val text: Int)

private val steps = listOf(
    Step(R.string.onboarding_1_title, R.string.onboarding_1_text),
    Step(R.string.onboarding_2_title, R.string.onboarding_2_text),
    Step(R.string.onboarding_3_title, R.string.onboarding_3_text),
)

/** Kurze Einführung beim ersten Start (nach dem Splash), jederzeit überspringbar. */
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    val step = steps[index]
    val last = index == steps.lastIndex
    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.systemBarsPadding().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onFinished) { Text(stringResource(R.string.onboarding_skip)) }
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(stringResource(step.title), style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Text(stringResource(step.text), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 24.dp)) {
                steps.indices.forEach { i ->
                    Box(
                        Modifier.size(8.dp).background(
                            if (i == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            CircleShape,
                        ),
                    )
                }
            }
            Button(onClick = { if (last) onFinished() else index++ }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(if (last) R.string.onboarding_done else R.string.onboarding_next))
            }
        }
    }
}
