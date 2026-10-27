package app.rpkindwords

import android.app.Application
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
        private set
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "kindwords.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
        settings = SettingsRepository(this)
        messages = MessageRepository(db.messageDao(), settings, AssetContentSource(this))
        NotificationHelper.createChannel(this)
        appScope.launch {
            messages.ensureSeeded()
            // Plan sicherstellen, aber einen vorhandenen (ggf. gerade laufenden) Auftrag NICHT ersetzen:
            // Startet WorkManager den Prozess für einen fälligen Worker, würde REPLACE ihn sofort abbrechen.
            ReminderScheduler.reschedule(this@KindWordsApp, settings.reminder.first(), replace = false)
        }
    }
}
