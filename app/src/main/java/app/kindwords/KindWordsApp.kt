package app.kindwords

import android.app.Application
import androidx.room.Room
import app.kindwords.data.AppDatabase
import app.kindwords.data.AssetContentSource
import app.kindwords.data.MessageRepository
import app.kindwords.data.SettingsRepository
import app.kindwords.reminder.ReminderScheduler
import app.kindwords.reminder.NotificationHelper
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
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "kindwords.db").build()
        settings = SettingsRepository(this)
        messages = MessageRepository(db.messageDao(), settings, AssetContentSource(this))
        NotificationHelper.createChannel(this)
        appScope.launch {
            messages.ensureSeeded()
            // Plan nach App-Start/Update/Reboot (nächster Start) sicherstellen
            ReminderScheduler.reschedule(this@KindWordsApp, settings.reminder.first())
        }
    }
}
