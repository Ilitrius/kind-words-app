package app.rpkindwords.data

import app.rpkindwords.reminder.ReminderMode
import app.rpkindwords.reminder.ReminderSettings
import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BackupCodecTest {
    private val data = BackupData(
        custom = listOf(BackupCustom("de", "Du bist toll", true), BackupCustom("en", "You rock", false)),
        favorites = listOf("de-love-001"),
        contentLang = "de",
        reminder = ReminderSettings(true, ReminderMode.FIXED, 2, 480, 1200, listOf(540, 1080), setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)),
    )

    @Test fun roundTrip() {
        assertEquals(data, BackupCodec.decode(BackupCodec.encode(data)))
    }

    @Test fun specialCharactersSurvive() {
        val text = "Zeile\n mit \"Zitat\" <b>&</b> 😊"
        val d = data.copy(custom = listOf(BackupCustom("de", text, false)))
        assertEquals(text, BackupCodec.decode(BackupCodec.encode(d))!!.custom.single().text)
    }

    @Test fun systemLanguageRoundTrip() {
        val d = data.copy(contentLang = null)
        assertNull(BackupCodec.decode(BackupCodec.encode(d))!!.contentLang)
    }

    @Test fun rejectsWrongFormatVersionAndGarbage() {
        assertNull(BackupCodec.decode("not json"))
        assertNull(BackupCodec.decode("""{"format":"other","version":1}"""))
        assertNull(BackupCodec.decode("""{"format":"kindwords-backup","version":2}"""))
        assertNull(BackupCodec.decode("x".repeat(BackupCodec.MAX_BYTES + 1)))
    }

    @Test fun sanitizesEntries() {
        val json = """{"format":"kindwords-backup","version":1,
          "customMessages":[{"language":"fr","text":"nope"},{"language":"de","text":"   "},
            {"language":"en","text":"${"a".repeat(900)}"}],
          "favorites":["", "ok-1"],
          "settings":{"contentLang":"xx","reminder":{"perDay":99,"windowStartMin":-5,"fixedTimesMin":[5000,600],"days":[0,9]}}}"""
        val d = BackupCodec.decode(json)!!
        assertEquals(1, d.custom.size)
        assertEquals(BackupCodec.MAX_TEXT, d.custom[0].text.length)
        assertEquals(listOf("ok-1"), d.favorites)
        assertNull(d.contentLang)
        assertEquals(3, d.reminder.perDay)
        assertEquals(0, d.reminder.windowStartMin)
        assertEquals(listOf(600), d.reminder.fixedTimesMin)
        assertEquals(ReminderSettings().days, d.reminder.days)
        assertNotNull(d.reminder)
    }
}
