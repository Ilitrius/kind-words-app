package app.rpkindwords.data

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteBiasTest {
    @Test fun boundaries() {
        assertTrue(FavoriteBias.preferFavorite(0.0))
        assertTrue(FavoriteBias.preferFavorite(FavoriteBias.SHARE - 0.0001))
        assertFalse(FavoriteBias.preferFavorite(FavoriteBias.SHARE))
        assertFalse(FavoriteBias.preferFavorite(0.99))
    }

    @Test fun shareIsRoughlyFortyPercent() {
        val rnd = Random(42)
        val hits = (1..10_000).count { FavoriteBias.preferFavorite(rnd.nextDouble()) }
        assertEquals(FavoriteBias.SHARE, hits / 10_000.0, 0.02)
    }
}
