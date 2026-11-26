package app.rpkindwords.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteContentTest {
    private fun rowsJson(from: Int, count: Int) =
        (from until from + count).joinToString(prefix = "[", postfix = "]") {
            """{"id":"de-love-$it","category":"love","text":"Satz $it"}"""
        }

    /** Fake-Server: Version und 2300 Sätze in Seiten à 1000; [versionAfterLoad] simuliert eine Änderung während des Ladens. */
    private fun api(version: Int = 12, total: Int = 2300, versionAfterLoad: Int = version, fail: (String) -> Boolean = { false }): RemoteApi {
        var versionCalls = 0
        return RemoteApi { path ->
            when {
                fail(path) -> null
                path.startsWith("content_version") ->
                    """[{"version":${if (versionCalls++ == 0) version else versionAfterLoad}}]"""
                else -> {
                    val offset = Regex("""offset=(\d+)""").find(path)!!.groupValues[1].toInt()
                    rowsJson(offset, minOf(RemoteContent.PAGE, total - offset).coerceAtLeast(0))
                }
            }
        }
    }

    @Test fun loadsAllPagesWhenNewer() {
        val f = RemoteContent.fetch(api(), "de", 11)!!
        assertEquals(12, f.version)
        assertEquals(2300, f.items.size)
        assertEquals("de", f.language)
    }

    @Test fun nothingWhenNotNewer() {
        assertNull(RemoteContent.fetch(api(version = 11), "de", 11))
        assertNull(RemoteContent.fetch(api(version = 11), "de", 12))
    }

    @Test fun exactPageBoundaryTerminates() {
        assertEquals(2000, RemoteContent.fetch(api(total = 2000), "de", 11)!!.items.size)
    }

    @Test fun networkErrorMeansNoChange() {
        assertNull(RemoteContent.fetch(api(fail = { it.startsWith("messages") && "offset=1000" in it }), "de", 11))
        assertNull(RemoteContent.fetch(api(fail = { true }), "de", 11))
    }

    @Test fun versionChangedWhileLoadingIsRejected() {
        assertNull(RemoteContent.fetch(api(version = 12, versionAfterLoad = 13), "de", 11))
    }

    @Test fun parserSkipsInvalidAndSurvivesGarbage() {
        val r = RemoteContent.parseItems("""[{"id":"a","category":"love","text":" ok "},{"id":"","category":"love","text":"x"},{"id":"b","category":"","text":"x"}]""")!!
        assertEquals(listOf("a"), r.items.map { it.id })
        assertEquals("ok", r.items[0].text)
        assertEquals(3, r.rawCount)
        assertNull(RemoteContent.parseItems("not json"))
        assertNull(RemoteContent.parseVersion("[]"))
        assertNull(RemoteContent.parseVersion(null))
        assertEquals(7, RemoteContent.parseVersion("""[{"version":7}]"""))
    }

    @Test fun plausibilityRejectsShrunkenOrEmptyStock() {
        assertTrue(RemoteContent.plausible(1250, 1300))
        assertTrue(RemoteContent.plausible(1250, 900))
        assertFalse(RemoteContent.plausible(1250, 100))
        assertFalse(RemoteContent.plausible(1250, 0))
        assertTrue(RemoteContent.plausible(0, 10))
    }

    @Test fun remoteFileFlowsThroughContentSyncKeepingFavorites() {
        val fav = MessageEntity("de-love-5", "de", "love", "alt", Source.BUNDLED, true, false, 1L)
        val gone = MessageEntity("de-love-999", "de", "love", "weg", Source.BUNDLED, false, false, 1L)
        val file = ContentFile("de", 12, listOf(ContentItem("de-love-5", "love", "neu"), ContentItem("de-love-6", "love", "frisch")))
        val plan = ContentSync.plan(listOf(fav, gone), emptySet(), file, 2L)
        assertEquals(listOf("de-love-999"), plan.deleteIds)
        assertEquals("neu", plan.updates.single().text)
        assertTrue(plan.updates.single().favorite)
        assertEquals(listOf("de-love-6"), plan.inserts.map { it.id })
    }
}
