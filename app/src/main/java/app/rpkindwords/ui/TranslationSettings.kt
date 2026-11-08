package app.rpkindwords.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.rpkindwords.MainViewModel
import app.rpkindwords.R
import app.rpkindwords.translate.TranslationService
import java.util.Locale

/**
 * Übersetzen vor dem Vorlesen (Google ML Kit, auf dem Gerät). Standardmäßig aus; ein Einschalten verlangt eine
 * ausdrückliche Einwilligung (Opt-in), Widerruf jederzeit hier. Modelle laden nur auf Knopfdruck.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslationSettings(vm: MainViewModel, sourceLang: String) {
    val context = LocalContext.current
    val appLocale = LocalConfiguration.current.locales[0]
    val prefs by vm.translationPrefs.collectAsStateWithLifecycle()
    var askConsent by remember { mutableStateOf(false) }
    var askRevoke by remember { mutableStateOf(false) }
    var ready by remember(prefs.target, prefs.consent) { mutableStateOf<Boolean?>(null) }
    var busy by remember { mutableStateOf(false) }
    val downloadDone = stringResource(R.string.translation_download_done)
    val downloadFailed = stringResource(R.string.translation_download_failed)
    val deleted = stringResource(R.string.translation_models_deleted)

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.translation_switch), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        Switch(checked = prefs.consent, onCheckedChange = { if (it) askConsent = true else askRevoke = true })
    }
    Text(stringResource(R.string.translation_hint), style = MaterialTheme.typography.bodyMedium)

    if (prefs.consent) {
        Text(stringResource(R.string.translation_target_label), style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TranslationService.TARGET_LANGUAGES.filter { it != sourceLang }.forEach { code ->
                FilterChip(
                    selected = prefs.target == code,
                    onClick = { vm.setTranslationTarget(if (prefs.target == code) null else code) },
                    label = { Text(languageName(code, appLocale)) },
                )
            }
        }
        val target = prefs.target
        if (target != null) {
            LaunchedEffect(target, busy) { vm.checkModels(sourceLang, target) { ready = it } }
            Text(
                stringResource(if (ready == true) R.string.translation_models_ready else R.string.translation_models_missing),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (ready == false) {
                Button(
                    enabled = !busy,
                    onClick = {
                        busy = true
                        vm.downloadModels(sourceLang, target) { ok ->
                            busy = false
                            Toast.makeText(context, if (ok) downloadDone else downloadFailed, Toast.LENGTH_LONG).show()
                        }
                    },
                ) { Text(stringResource(if (busy) R.string.translation_downloading else R.string.translation_download)) }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.translation_mobile), Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Switch(checked = prefs.allowMobile, onCheckedChange = { vm.setTranslationAllowMobile(it) })
            }
        }
        OutlinedButton(onClick = {
            vm.deleteModels {
                ready = false
                Toast.makeText(context, deleted, Toast.LENGTH_SHORT).show()
            }
        }) { Text(stringResource(R.string.translation_delete_models)) }
    }

    if (askConsent) {
        val privacyUrl = stringResource(R.string.privacy_url)
        AlertDialog(
            onDismissRequest = { askConsent = false },
            title = { Text(stringResource(R.string.translation_consent_title)) },
            text = {
                androidx.compose.foundation.layout.Column {
                    Text(stringResource(R.string.translation_consent_text))
                    TextButton(onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, privacyUrl.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        } catch (_: Exception) {
                        }
                    }) { Text(stringResource(R.string.privacy_link)) }
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.grantTranslationConsent(); askConsent = false }) { Text(stringResource(R.string.translation_consent_accept)) }
            },
            dismissButton = { TextButton(onClick = { askConsent = false }) { Text(stringResource(R.string.translation_consent_decline)) } },
        )
    }
    if (askRevoke) {
        AlertDialog(
            onDismissRequest = { askRevoke = false },
            title = { Text(stringResource(R.string.translation_revoke_title)) },
            text = { Text(stringResource(R.string.translation_revoke_text)) },
            confirmButton = {
                TextButton(onClick = { vm.revokeTranslationConsent(deleteModels = true); askRevoke = false }) { Text(stringResource(R.string.translation_revoke_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { vm.revokeTranslationConsent(deleteModels = false); askRevoke = false }) { Text(stringResource(R.string.translation_revoke_keep)) }
            },
        )
    }
}

private fun languageName(code: String, locale: Locale): String =
    Locale.forLanguageTag(code).getDisplayLanguage(locale).replaceFirstChar { it.uppercase(locale) }
