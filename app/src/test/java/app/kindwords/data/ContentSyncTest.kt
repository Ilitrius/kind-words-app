package app.kindwords.data

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

class ContentSyncTest {
    private fun bundled(id: String, text: String, fav: Boolean = false, cat: String = "love") =
        MessageEntity(id, "de", cat, text, Source.BUNDLED, fav, false, 1L)

    private fun testFile() =
        ContentParser.parse(javaClass.getResource("/messages_test_de.json")!!.readText())!!

    @Test fun parserSkipsInvalidAndDuplicates() {
        val f = testFile()
        assertEquals("de", f.language)
        assertEquals(2, f.version)
        assertEquals(listOf("de-love-001", "de-love-002", "de-love-003"), f.items.map { it.id })
    }

    @Test fun parserReturnsNullOnGarbage() {
        assertNull(ContentParser.parse("not json"))
        assertNull(ContentParser.parse("{}"))
    }

    @Test fun freshInstallInsertsAll() {
        val plan = ContentSync.plan(emptyList(), emptySet(), testFile(), 10L)
        assertEquals(3, plan.inserts.size)
        assertTrue(plan.updates.isEmpty() && plan.deleteIds.isEmpty())
        assertTrue(plan.inserts.all { it.source == Source.BUNDLED && !it.favorite })
    }

    @Test fun updateKeepsFavoriteAndHidden() {
        val old = bundled("de-love-001", "Alter Text", fav = true).copy(hidden = true)
        val plan = ContentSync.plan(listOf(old), emptySet(), testFile(), 10L)
        val upd = plan.updates.single()
        assertEquals("Neuer Text eins", upd.text)
        assertTrue(upd.favorite && upd.hidden)
        assertEquals(2, plan.inserts.size)
    }

    @Test fun unchangedRowsAreNotRewritten() {
        val plan = ContentSync.plan(listOf(bundled("de-love-001", "Neuer Text eins")), emptySet(), testFile(), 10L)
        assertTrue(plan.updates.isEmpty())
    }

    @Test fun removedBundledDeletedExceptFavorites() {
        val existing = listOf(bundled("gone-1", "x"), bundled("gone-2", "y", fav = true))
        val plan = ContentSync.plan(existing, emptySet(), testFile(), 10L)
        assertEquals(listOf("gone-1"), plan.deleteIds)
    }

    @Test fun userIdCollisionIsNeverTouched() {
        val plan = ContentSync.plan(emptyList(), setOf("de-love-001"), testFile(), 10L)
        assertEquals(setOf("de-love-002", "de-love-003"), plan.inserts.map { it.id }.toSet())
    }

    @Test fun versionGate() {
        val f = testFile()
        assertTrue(ContentSync.needsSync(0, f))
        assertTrue(ContentSync.needsSync(1, f))
        assertTrue(!ContentSync.needsSync(2, f))
    }

    @Test fun languageResolution() {
        assertEquals("de", Languages.resolve("de"))
        assertEquals("en", Languages.resolve("fr"))
        assertEquals("en", Languages.resolve(null))
    }

    /** Prüft die echten Assets, sobald sie existieren (sonst übersprungen). */
    @Test fun realAssetsAreValid() {
        for (lang in Languages.supported) {
            val f = File("src/main/assets/messages/messages_$lang.json")
            assumeTrue(f.exists())
            val parsed = ContentParser.parse(f.readText())
            assertNotNull("messages_$lang.json parsebar", parsed)
            assertEquals(lang, parsed!!.language)
            assertTrue(parsed.items.isNotEmpty())
            assertTrue(parsed.items.all { it.category in MessageRepository.categories })
            assertTrue(parsed.items.all { it.id.startsWith("$lang-") })
            val plan = ContentSync.plan(emptyList(), emptySet(), parsed, 0L)
            assertEquals(parsed.items.size, plan.inserts.size)
        }
    }
}
