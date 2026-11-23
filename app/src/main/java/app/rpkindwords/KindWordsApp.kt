package app.rpkindwords

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.room.Room
import app.rpkindwords.data.AppDatabase
import app.rpkindwords.data.AssetContentSource
import app.rpkindwords.data.MessageRepository
import app.rpkindwords.data.SettingsRepository
import app.rpkindwords.reminder.ReminderScheduler
import app.rpkindwords.reminder.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class KindWordsApp : Application() {
    lateinit var settings: SettingsRepository
        private set
    lateinit var messages: MessageRepository
    lateinit var translation: app.rpkindwords.translate.TranslationService
        private set
    lateinit var ads: app.rpkindwords.ads.AdsConsent
        private set
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "kindwords.db").build()
        settings = SettingsRepository(this)
        messages = MessageRepository(db.messageDao(), settings, AssetContentSource(this))
        translation = app.rpkindwords.translate.TranslationService(this, settings)
        ads = app.rpkindwords.ads.AdsConsent(this)
        NotificationHelper.createChannel(this)
        appScope.launch {
            messages.ensureSeeded()
            // Widgets neu zeichnen (neuer Tag, geänderte Sprache/Inhalte); ohne Widgets kostet das fast nichts.
            runCatching { app.rpkindwords.widget.KindWordsWidget().updateAll(this@KindWordsApp) }
            // Plan sicherstellen, aber einen vorhandenen (ggf. gerade laufenden) Auftrag NICHT ersetzen:
            // Startet WorkManager den Prozess für einen fälligen Worker, würde REPLACE ihn sofort abbrechen.
            ReminderScheduler.reschedule(this@KindWordsApp, settings.reminder.first(), replace = false)
        }
    }
}
