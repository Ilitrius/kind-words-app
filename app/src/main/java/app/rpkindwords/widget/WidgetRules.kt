package app.rpkindwords.widget

import app.rpkindwords.data.MessageRepository

/** Gespeicherter Zustand eines Widgets (je Widget-ID). */
data class WidgetData(
    val category: String? = null,
    val messageId: String = "",
    val text: String = "",
    val lang: String = "",
    val epochDay: Long = -1L,
)

/** Reine Regeln für das Widget (ohne Android), getestet in WidgetRulesTest. */
object WidgetRules {
    /** Nur bekannte Kategorien; alles andere bedeutet "alle". */
    fun sanitizeCategory(category: String?): String? = category?.takeIf { it in MessageRepository.categories }

    /** Neuer Satz nötig: noch keiner, neuer Kalendertag oder andere Inhaltssprache. */
    fun needsNewSentence(data: WidgetData, todayEpochDay: Long, lang: String): Boolean =
        data.text.isBlank() || data.epochDay != todayEpochDay || data.lang != lang
}
