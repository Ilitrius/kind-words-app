package app.rpkindwords.data

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

/** Liefert einen Context, dessen Ressourcen in der gewählten App-Sprache laden (null = Systemsprache). */
object LocaleContext {
    fun wrap(base: Context, lang: String?): Context {
        if (lang == null) return base
        val locale = Locale.forLanguageTag(lang)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLocales(LocaleList(locale))
        return base.createConfigurationContext(config)
    }
}
