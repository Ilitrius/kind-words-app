package app.rpkindwords.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import app.rpkindwords.KindWordsApp
import app.rpkindwords.MainActivity
import app.rpkindwords.R
import app.rpkindwords.data.Languages
import app.rpkindwords.data.LocaleContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.Locale
import kotlinx.coroutines.flow.first

object ShareIntents {
    fun send(text: String): Intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
}

object NotificationHelper {
    const val CHANNEL_ID = "reminders"
    private const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        val ch = NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_reminders_name), NotificationManager.IMPORTANCE_DEFAULT)
        ch.description = context.getString(R.string.channel_reminders_desc)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
    }

    fun canPost(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return false
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun show(base: Context, text: String, lang: String? = null) {
        val context = LocaleContext.wrap(base, lang)
        if (!canPost(context)) return
        createChannel(context)
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val open = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), flags)
        val chooser = Intent.createChooser(ShareIntents.send(text), null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val share = PendingIntent.getActivity(context, 1, chooser, flags)
        val n = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .addAction(0, context.getString(R.string.action_share), share)
            .build()
        @Suppress("MissingPermission") // canPost() geprüft
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, n)
    }
}

/**
 * Erinnerungen laufen über AlarmManager.setAndAllowWhileIdle (inexakt, ohne Sonderberechtigung).
 * Anders als WorkManager/JobScheduler wird der Alarm auch im App-Standby (Bucket "Rare") und in Doze zugestellt.
 */
object ReminderScheduler {
    private const val REQUEST_CODE = 42

    private fun pendingIntent(context: Context, flags: Int): PendingIntent? =
        PendingIntent.getBroadcast(context, REQUEST_CODE, Intent(context, ReminderReceiver::class.java), flags or PendingIntent.FLAG_IMMUTABLE)

    /**
     * Plant genau einen Alarm für den nächsten Zeitpunkt.
     * [replace] = true ersetzt einen vorhandenen Alarm (Einstellungen/Zeit geändert, nach Auslösung),
     * false behält ihn (App-Start) und plant nur, falls keiner existiert.
     */
    fun reschedule(
        context: Context,
        settings: ReminderSettings,
        now: ZonedDateTime = ZonedDateTime.now(),
        replace: Boolean = true,
    ) {
        val alarms = context.getSystemService(AlarmManager::class.java)
        val next = ReminderPlanner.nextTrigger(settings, now)
        if (next == null) {
            pendingIntent(context, PendingIntent.FLAG_NO_CREATE)?.let { alarms.cancel(it); it.cancel() }
            return
        }
        if (!replace && pendingIntent(context, PendingIntent.FLAG_NO_CREATE) != null) return
        val pi = pendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT)!!
        alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.toInstant().toEpochMilli(), pi)
    }
}

/** Löst die Erinnerung aus und plant den nächsten Alarm; reagiert außerdem auf Boot, Update und Zeitänderungen. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as KindWordsApp
        val fire = intent.action == null // eigener Alarm (explizite Intent ohne Action)
        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val settings = app.settings.reminder.first()
                if (fire && settings.enabled) {
                    // Ein Fehler beim Anzeigen darf die Kette nicht abreißen lassen: der nächste Alarm wird immer geplant.
                    runCatching {
                        app.messages.ensureSeeded()
                        val lang = app.settings.contentLanguageOverride.first() ?: Languages.resolve(Locale.getDefault().language)
                        app.messages.random(lang, null, null)?.let {
                            val text = app.messages.styled(it, app.messages.styleProfile()).text
                            NotificationHelper.show(app, text, lang)
                        }
                    }
                }
                // Boot/Update: Alarme sind weg -> neu planen. Zeit-/Zonenwechsel und Auslösung: ersetzen.
                val keep = intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
                ReminderScheduler.reschedule(app, settings, replace = !keep)
            } finally {
                pending.finish()
            }
        }
    }
}
