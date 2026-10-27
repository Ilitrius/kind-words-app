package app.rpkindwords

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.rpkindwords.data.Languages
import app.rpkindwords.data.MessageEntity
import app.rpkindwords.data.StyleProfile
import app.rpkindwords.reminder.ReminderScheduler
import app.rpkindwords.reminder.ReminderSettings
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as KindWordsApp
    private val messages = app.messages
    private val settings = app.settings

    val languageOverride: StateFlow<String?> =
        settings.contentLanguageOverride.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val contentLang = settings.contentLanguageOverride
        .map { it ?: Languages.resolve(Locale.getDefault().language) }
        .distinctUntilChanged()

    val reminder: StateFlow<ReminderSettings> =
        settings.reminder.stateIn(viewModelScope, SharingStarted.Eagerly, ReminderSettings())

    val audience: StateFlow<String?> =
        settings.audience.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category

    private val currentId = MutableStateFlow<String?>(null)
    /** Stil der eigenen Sätze: steuert Auswahl (Repository) und Satzende des angezeigten Vorschlags. */
    private val styleProfile: StateFlow<StyleProfile> =
        messages.userMessages().map { list -> StyleProfile.from(list.map { it.text }) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StyleProfile.EMPTY)

    val current: StateFlow<MessageEntity?> = combine(
        currentId.flatMapLatest { id -> if (id == null) flowOf(null) else messages.observe(id) },
        styleProfile,
    ) { message, profile -> message?.let { messages.styled(it, profile) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val favorites: StateFlow<List<MessageEntity>> =
        messages.favorites().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val userMessages: StateFlow<List<MessageEntity>> =
        messages.userMessages().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _loaded = MutableStateFlow(false)
    val loaded: StateFlow<Boolean> = _loaded

    init {
        viewModelScope.launch {
            messages.ensureSeeded()
            combine(contentLang, _category, settings.audience) { l, c, _ -> l to c }.collect { (l, c) ->
                currentId.value = messages.random(l, c, null)?.id
                _loaded.value = true
            }
        }
    }

    fun newSuggestion() {
        viewModelScope.launch {
            val lang = contentLang.first()
            val next = messages.random(lang, _category.value, currentId.value)
                ?: messages.random(lang, _category.value, null)
            currentId.value = next?.id
        }
    }

    /** Tippen auf die schon gewählte Seite hebt die Auswahl auf (dann nur neutrale Sätze). */
    fun selectAudience(value: String) {
        viewModelScope.launch { settings.setAudience(if (audience.value == value) null else value) }
    }

    fun selectCategory(c: String?) { _category.value = c }
    suspend fun setContentLanguage(lang: String?) { settings.setContentLanguage(lang) }
    fun toggleFavorite(m: MessageEntity) { viewModelScope.launch { messages.setFavorite(m.id, !m.favorite) } }

    fun addUserMessage(text: String) {
        viewModelScope.launch { messages.addUser(contentLang.first(), text) }
    }
    fun editUserMessage(id: String, text: String) { viewModelScope.launch { messages.editUser(id, text) } }
    fun deleteUserMessage(id: String) {
        viewModelScope.launch {
            messages.deleteUser(id)
            if (currentId.value == id) newSuggestion()
        }
    }

    fun updateReminder(transform: (ReminderSettings) -> ReminderSettings) {
        viewModelScope.launch {
            settings.updateReminder(transform)
            ReminderScheduler.reschedule(app, settings.reminder.first())
        }
    }
}
