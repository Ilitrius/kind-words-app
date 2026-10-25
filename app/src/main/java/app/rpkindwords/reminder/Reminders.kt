package app.rpkindwords.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import app.rpkindwords.KindWordsApp
import app.rpkindwords.MainActivity
import app.rpkindwords.R
import app.rpkindwords.data.Languages
import java.time.Duration
import java.time.ZonedDateTime
import java.util.Locale
import java.util.concurrent.TimeUnit
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

    fun show(context: Context, text: String) {
        if (!canPost(context)) return
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

object ReminderScheduler {
    private const val WORK_NAME = "kindwords_reminder"

    /** Plant genau einen inexakten Work-Request für den nächsten Zeitpunkt (ersetzt den alten). */
    fun reschedule(context: Context, settings: ReminderSettings, now: ZonedDateTime = ZonedDateTime.now()) {
        val wm = WorkManager.getInstance(context)
        val next = ReminderPlanner.nextTrigger(settings, now)
        if (next == null) {
            wm.cancelUniqueWork(WORK_NAME)
            return
        }
        val delay = Duration.between(now, next).toMillis().coerceAtLeast(0)
        val req = OneTimeWorkRequestBuilder<ReminderWorker>().setInitialDelay(delay, TimeUnit.MILLISECONDS).build()
        wm.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, req)
    }
}

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as KindWordsApp
        val settings = app.settings.reminder.first()
        if (settings.enabled) {
            app.messages.ensureSeeded()
            val lang = app.settings.contentLanguageOverride.first() ?: Languages.resolve(Locale.getDefault().language)
            app.messages.random(lang, null, null)?.let { NotificationHelper.show(applicationContext, it.text) }
        }
        ReminderScheduler.reschedule(applicationContext, settings)
        return Result.success()
    }
}
