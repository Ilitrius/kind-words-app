package app.rpkindwords.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.rpkindwords.MainViewModel
import app.rpkindwords.R
import app.rpkindwords.reminder.NotificationHelper
import app.rpkindwords.reminder.ReminderMode
import app.rpkindwords.reminder.ReminderPlanner
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

private const val MAX_FIXED_TIMES = 6

private sealed interface PickTarget {
    data object WindowStart : PickTarget
    data object WindowEnd : PickTarget
    data class Fixed(val index: Int) : PickTarget
    data object NewFixed : PickTarget
}

private fun fmt(min: Int): String =
    LocalTime.of(min / 60, min % 60).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

private fun openNotificationSettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun SettingsScreen(vm: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val s by vm.reminder.collectAsStateWithLifecycle()
    val langOverride by vm.languageOverride.collectAsStateWithLifecycle()
    var permissionDenied by remember { mutableStateOf(false) }
    var tick by remember { mutableIntStateOf(0) }
    var picking by remember { mutableStateOf<PickTarget?>(null) }
    LifecycleResumeEffect(Unit) { tick++; onPauseOrDispose { } }
    val canPost = remember(tick, s.enabled, permissionDenied) { NotificationHelper.canPost(context) }

    val scope = rememberCoroutineScope()
    val changeLanguage: (String?) -> Unit = { lang ->
        scope.launch {
            vm.setContentLanguage(lang)
            (context as? Activity)?.recreate() // lädt alle Texte in der neuen Sprache
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionDenied = !granted
        if (granted) vm.updateReminder { it.copy(enabled = true) }
    }

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.nav_settings), style = MaterialTheme.typography.headlineMedium)

        SectionTitle(R.string.settings_reminders)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.reminders_enable), Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = s.enabled, onCheckedChange = { on ->
                if (on && Build.VERSION.SDK_INT >= 33 && !NotificationHelper.canPost(context)) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    permissionDenied = false
                    vm.updateReminder { it.copy(enabled = on) }
                }
            })
        }
        Text(stringResource(R.string.reminders_permission_info), style = MaterialTheme.typography.bodyMedium)
        if ((permissionDenied || s.enabled) && !canPost) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.reminders_permission_denied), color = MaterialTheme.colorScheme.onErrorContainer)
                    OutlinedButton(onClick = { openNotificationSettings(context) }) { Text(stringResource(R.string.open_notification_settings)) }
                }
            }
        }

        if (s.enabled) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = s.mode == ReminderMode.RANDOM, onClick = { vm.updateReminder { it.copy(mode = ReminderMode.RANDOM) } }, label = { Text(stringResource(R.string.mode_random)) })
                FilterChip(selected = s.mode == ReminderMode.FIXED, onClick = { vm.updateReminder { it.copy(mode = ReminderMode.FIXED) } }, label = { Text(stringResource(R.string.mode_fixed)) })
            }
            if (s.mode == ReminderMode.RANDOM) {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (n in ReminderPlanner.MIN_PER_DAY..ReminderPlanner.MAX_PER_DAY) {
                        FilterChip(
                            selected = s.perDay == n,
                            onClick = { vm.updateReminder { it.copy(perDay = n) } },
                            label = { Text(pluralStringResource(R.plurals.times_per_day, n, n)) },
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.window_from))
                    FilledTonalButton(onClick = { picking = PickTarget.WindowStart }) { Text(fmt(s.windowStartMin)) }
                    Text(stringResource(R.string.window_to))
                    FilledTonalButton(onClick = { picking = PickTarget.WindowEnd }) { Text(fmt(s.windowEndMin)) }
                }
            } else {
                s.fixedTimesMin.forEachIndexed { i, m ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { picking = PickTarget.Fixed(i) }) { Text(fmt(m)) }
                        IconButton(onClick = { vm.updateReminder { it.copy(fixedTimesMin = it.fixedTimesMin.filterIndexed { j, _ -> j != i }) } }) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.time_remove, fmt(m)))
                        }
                    }
                }
                if (s.fixedTimesMin.size < MAX_FIXED_TIMES) {
                    OutlinedButton(onClick = { picking = PickTarget.NewFixed }) { Text(stringResource(R.string.time_add)) }
                }
            }
            Text(stringResource(R.string.reminders_days), style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (d in DayOfWeek.entries) {
                    FilterChip(
                        selected = d in s.days,
                        onClick = { vm.updateReminder { it.copy(days = if (d in it.days) it.days - d else it.days + d) } },
                        label = { Text(d.getDisplayName(TextStyle.SHORT, Locale.getDefault())) },
                    )
                }
            }
        }

        HorizontalDivider()
        SectionTitle(R.string.settings_language)
        Text(stringResource(R.string.settings_language_info), style = MaterialTheme.typography.bodyMedium)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = langOverride == null, onClick = { changeLanguage(null) }, label = { Text(stringResource(R.string.lang_system)) })
            FilterChip(selected = langOverride == "de", onClick = { changeLanguage("de") }, label = { Text(stringResource(R.string.lang_de)) })
            FilterChip(selected = langOverride == "en", onClick = { changeLanguage("en") }, label = { Text(stringResource(R.string.lang_en)) })
        }

        val donateUrl = stringResource(R.string.donate_url)
        if (donateUrl.startsWith("https://")) {
            HorizontalDivider()
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.donate_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.donate_text), style = MaterialTheme.typography.bodyMedium)
                    val error = stringResource(R.string.donate_error)
                    FilledTonalButton(onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, donateUrl.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        } catch (_: Exception) {
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    }) { Text(stringResource(R.string.donate_button)) }
                }
            }
        }

        HorizontalDivider()
        val packageInfo = remember(context) { context.packageManager.getPackageInfo(context.packageName, 0) }
        val versionText = stringResource(
            R.string.settings_version,
            packageInfo.versionName.orEmpty(),
            PackageInfoCompat.getLongVersionCode(packageInfo),
        )
        Text(versionText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    picking?.let { target ->
        val initial = when (target) {
            PickTarget.WindowStart -> s.windowStartMin
            PickTarget.WindowEnd -> s.windowEndMin
            is PickTarget.Fixed -> s.fixedTimesMin.getOrElse(target.index) { 18 * 60 }
            PickTarget.NewFixed -> 18 * 60
        }
        TimeDialog(initial, onDismiss = { picking = null }) { m ->
            picking = null
            vm.updateReminder { cur ->
                when (target) {
                    PickTarget.WindowStart -> cur.copy(windowStartMin = m, windowEndMin = maxOf(cur.windowEndMin, m + 30).coerceAtMost(1439))
                    PickTarget.WindowEnd -> cur.copy(windowEndMin = maxOf(m, cur.windowStartMin + 30).coerceAtMost(1439))
                    is PickTarget.Fixed -> cur.copy(fixedTimesMin = cur.fixedTimesMin.toMutableList().also { if (target.index in it.indices) it[target.index] = m }.distinct().sorted())
                    PickTarget.NewFixed -> cur.copy(fixedTimesMin = (cur.fixedTimesMin + m).distinct().sorted())
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(res: Int) {
    Text(stringResource(res), style = MaterialTheme.typography.titleLarge)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeDialog(initialMin: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    val context = LocalContext.current
    val state = rememberTimePickerState(initialMin / 60, initialMin % 60, DateFormat.is24HourFormat(context))
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { TimePicker(state) },
        confirmButton = { TextButton(onClick = { onConfirm(state.hour * 60 + state.minute) }) { Text(stringResource(R.string.action_ok)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}
