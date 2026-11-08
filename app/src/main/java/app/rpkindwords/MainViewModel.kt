package app.rpkindwords

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.rpkindwords.data.BackupCodec
import app.rpkindwords.data.BackupData
import app.rpkindwords.data.Languages
import app.rpkindwords.data.MessageEntity
import app.rpkindwords.data.StyleProfile
import app.rpkindwords.data.TranslationPrefs
import app.rpkindwords.reminder.NotificationHelper
import app.rpkindwords.speech.Speaker
import app.rpkindwords.translate.TranslationService
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
    private val translation = app.translation
    private val settings = app.settings

    /** Spendenbitte: nur bei jedem 4. echten App-Start (Zähler lokal, überlebt Drehen/Sprachwechsel ohne erneute Zählung). */
    private val _donatePrompt = MutableStateFlow(false)
    val donatePrompt: StateFlow<Boolean> = _donatePrompt
    init { viewModelScope.launch { _donatePrompt.value = settings.registerStartAndCheckDonatePrompt() } }
    fun dismissDonatePrompt() { _donatePrompt.value = false }

    /** null = noch nicht geladen (weder Einführung noch Inhalt zeigen, kein Aufblitzen). */
    val onboardingSeen: StateFlow<Boolean?> = settings.onboardingSeen.map<Boolean, Boolean?> { it }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    fun markOnboardingSeen() { viewModelScope.launch { settings.setOnboardingSeen() } }

    val languageOverride: StateFlow<String?> =
        settings.contentLanguageOverride.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val contentLang = settings.contentLanguageOverride
        .map { it ?: Languages.resolve(Locale.getDefault().language) }
        .distinctUntilChanged()

    val reminder: StateFlow<ReminderSettings> =
        settings.reminder.stateIn(viewModelScope, SharingStarted.Eagerly, ReminderSettings())

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category

    private val currentId = MutableStateFlow<String?>(null)
    /** Lernen auf dem Gerät: wurde der angezeigte Satz geteilt/kopiert/als Favorit gemerkt? Sonst gilt Weiterklicken als "übersprungen". */
    private var positiveForCurrent = false

    private val speaker = Speaker(application)
    private val _speechProblems = kotlinx.coroutines.flow.MutableSharedFlow<Speaker.Problem>(extraBufferCapacity = 4)
    val speechProblems: kotlinx.coroutines.flow.SharedFlow<Speaker.Problem> = _speechProblems
    val speechRate: StateFlow<Int> = settings.speechRatePct.stateIn(viewModelScope, SharingStarted.Eagerly, 80)
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
            combine(contentLang, _category) { l, c -> l to c }.collect { (l, c) ->
                val first = messages.random(l, c, null)?.id
                currentId.value = first
                positiveForCurrent = false
                first?.let { messages.recordShown(it) }
                _loaded.value = true
            }
        }
    }

    fun newSuggestion() {
        viewModelScope.launch {
            val lang = contentLang.first()
            val next = messages.random(lang, _category.value, currentId.value)
                ?: messages.random(lang, _category.value, null)
            val previous = currentId.value
            if (previous != null && !positiveForCurrent) messages.recordSkipped(previous)
            currentId.value = next?.id
            positiveForCurrent = false
            next?.let { messages.recordShown(it.id) }
        }
    }

    /** Teilen oder Kopieren eines Satzes zählt als positive Rückmeldung (nur lokal gespeichert). */
    fun recordPositive(id: String) {
        if (id == currentId.value) positiveForCurrent = true
        viewModelScope.launch { messages.recordPositive(id) }
    }

    init { speaker.onProblem = { _speechProblems.tryEmit(it) } }

    /** Der übersetzte Text, der gerade vorgelesen wird (zum Mitlesen). */
    private val _translatedText = kotlinx.coroutines.flow.MutableSharedFlow<String>(extraBufferCapacity = 4)
    val translatedText: kotlinx.coroutines.flow.SharedFlow<String> = _translatedText

    val translationPrefs: StateFlow<TranslationPrefs> =
        settings.translationPrefs.stateIn(viewModelScope, SharingStarted.Eagerly, TranslationPrefs())

    /** Liest einen Satz vor (lokale Sprachausgabe, gewählte Stimme und Tempo; bei aktiver Übersetzung zuerst übersetzt). */
    fun speak(m: MessageEntity) {
        viewModelScope.launch {
            recordPositive(m.id)
            speakText(m.text, m.language)
        }
    }

    fun speakSample(text: String, lang: String) {
        viewModelScope.launch { speakText(text, lang) }
    }

    private suspend fun speakText(text: String, from: String) {
        val prefs = translationPrefs.value
        val target = prefs.target
        if (!prefs.active || target == null || target == from) {
            speaker.speak(text, from, settings.voiceFor(from).first(), speechRate.value / 100f)
            return
        }
        when (val r = translation.translate(text, from, target)) {
            is TranslationService.Result.Ok -> {
                _translatedText.tryEmit(r.text)
                speaker.speak(r.text, target, settings.voiceFor(target).first(), speechRate.value / 100f)
            }
            is TranslationService.Result.Fail -> speaker.report(
                if (r.reason == TranslationService.Reason.MODEL_MISSING) Speaker.Problem.TRANSLATION_MODEL_MISSING else Speaker.Problem.TRANSLATION_FAILED,
            )
        }
    }

    fun grantTranslationConsent() { viewModelScope.launch { settings.grantTranslationConsent() } }

    /** Widerruf: stoppt die Übersetzung sofort; auf Wunsch werden die geladenen Modelle gelöscht. */
    fun revokeTranslationConsent(deleteModels: Boolean) {
        viewModelScope.launch {
            if (deleteModels) translation.deleteAllModels() else translation.close()
            settings.revokeTranslationConsent()
        }
    }

    fun setTranslationTarget(lang: String?) { viewModelScope.launch { settings.setTranslationTarget(lang) } }
    fun setTranslationAllowMobile(allow: Boolean) { viewModelScope.launch { settings.setTranslationAllowMobile(allow) } }
    fun checkModels(from: String, to: String, result: (Boolean) -> Unit) { viewModelScope.launch { result(translation.modelsReady(from, to)) } }
    fun downloadModels(from: String, to: String, result: (Boolean) -> Unit) {
        viewModelScope.launch { result(translation.download(from, to, translationPrefs.value.allowMobile) == null) }
    }
    fun deleteModels(done: () -> Unit) { viewModelScope.launch { translation.deleteAllModels(); done() } }

    fun stopSpeaking() = speaker.stop()
    fun loadVoices(lang: String, result: (List<String>) -> Unit) = speaker.voices(lang, result)
    fun voiceFlow(lang: String) = settings.voiceFor(lang)
    fun setVoice(lang: String, name: String?) { viewModelScope.launch { settings.setVoice(lang, name) } }
    fun setSpeechRate(pct: Int) { viewModelScope.launch { settings.setSpeechRate(pct) } }

    override fun onCleared() {
        speaker.shutdown()
        super.onCleared()
    }

    fun selectCategory(c: String?) { _category.value = c }
    suspend fun setContentLanguage(lang: String?) { settings.setContentLanguage(lang) }
    fun toggleFavorite(m: MessageEntity) {
        if (!m.favorite) recordPositive(m.id)
        viewModelScope.launch { messages.setFavorite(m.id, !m.favorite) }
    }

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

    /** Backup als JSON-Text (eigene Sätze, Favoriten, Sprache, Erinnerungen). */
    suspend fun exportBackup(): String {
        val (custom, favs) = messages.backupContent()
        return BackupCodec.encode(BackupData(custom, favs, settings.contentLanguageOverride.first(), settings.reminder.first()))
    }

    /** Spielt ein Backup ein; null = ungültige Datei, sonst (neue Sätze, gesetzte Favoriten). */
    suspend fun importBackup(json: String): Pair<Int, Int>? {
        val data = BackupCodec.decode(json) ?: return null
        val result = messages.restoreContent(data.custom, data.favorites)
        settings.setContentLanguage(data.contentLang)
        // Erinnerung nur aktiv lassen, wenn Benachrichtigungen erlaubt sind (sonst bliebe der Schalter wirkungslos).
        val rem = data.reminder.let { if (it.enabled && !NotificationHelper.canPost(app)) it.copy(enabled = false) else it }
        settings.updateReminder { rem }
        ReminderScheduler.reschedule(app, settings.reminder.first())
        return result
    }

    fun updateReminder(transform: (ReminderSettings) -> ReminderSettings) {
        viewModelScope.launch {
            settings.updateReminder(transform)
            ReminderScheduler.reschedule(app, settings.reminder.first())
        }
    }
}
