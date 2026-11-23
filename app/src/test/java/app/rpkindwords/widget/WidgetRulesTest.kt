package app.rpkindwords.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetRulesTest {
    private val fresh = WidgetData(category = "love", messageId = "de-love-001", text = "Hallo", lang = "de", epochDay = 100)

    @Test fun sameDayAndLanguageKeepsSentence() = assertFalse(WidgetRules.needsNewSentence(fresh, 100, "de"))
    @Test fun newDayRenews() = assertTrue(WidgetRules.needsNewSentence(fresh, 101, "de"))
    @Test fun languageChangeRenews() = assertTrue(WidgetRules.needsNewSentence(fresh, 100, "en"))
    @Test fun emptyRenews() = assertTrue(WidgetRules.needsNewSentence(WidgetData(), 100, "de"))
    @Test fun unknownCategoryMeansAll() { assertNull(WidgetRules.sanitizeCategory("bogus")); assertNull(WidgetRules.sanitizeCategory(null)) }
    @Test fun knownCategoryKept() = assertEquals("humor", WidgetRules.sanitizeCategory("humor"))
}
