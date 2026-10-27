package app.rpkindwords.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

object Source {
    const val BUNDLED = "BUNDLED"
    const val USER = "USER"
}

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val language: String,
    val category: String,
    val text: String,
    val source: String,
    val favorite: Boolean = false,
    val hidden: Boolean = false,
    val updatedAt: Long = 0L,
    /** Für wen der Satz formuliert ist: [Audience.FEMALE], [Audience.MALE] oder [Audience.ANY] (beide). */
    @ColumnInfo(defaultValue = "any") val audience: String = Audience.ANY,
)

object Audience {
    const val FEMALE = "f"
    const val MALE = "m"
    const val ANY = "any"

    /** Nur als Auswahl in den Einstellungen: zeigt ausschließlich die für alle neutralen Sätze ([ANY]). */
    const val DIVERSE = "d"

    /** Gültige Werte für Satz-Zielgruppe in den Inhaltsdateien. */
    fun isValid(value: String?) = value == FEMALE || value == MALE

    /** Gültige Werte der Nutzerauswahl. */
    fun isChoice(value: String?) = isValid(value) || value == DIVERSE

    /** Wert für die DAO-Filter: bei "divers" oder ohne Auswahl nur geschlechtsneutrale Sätze. */
    fun filter(selected: String?) = if (isValid(selected)) selected!! else ANY
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE language = :language AND source = 'BUNDLED'")
    suspend fun bundledForLanguage(language: String): List<MessageEntity>

    @Query("SELECT id FROM messages WHERE source = 'USER'")
    suspend fun userIds(): List<String>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun get(id: String): MessageEntity?

    @Query("DELETE FROM messages WHERE id = :id AND source = 'USER'")
    suspend fun deleteUser(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MessageEntity>)

    @Update
    suspend fun updateAll(items: List<MessageEntity>)

    @Query("DELETE FROM messages WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query(
        "SELECT * FROM messages WHERE language = :language AND hidden = 0 " +
            "AND audience IN ('any', :audience) " +
            "AND (:category IS NULL OR category = :category) AND id != :excludeId " +
            "ORDER BY RANDOM() LIMIT 1",
    )
    suspend fun random(language: String, category: String?, excludeId: String, audience: String): MessageEntity?

    @Query(
        "SELECT * FROM messages WHERE language = :language AND hidden = 0 AND favorite = 1 " +
            "AND audience IN ('any', :audience) " +
            "AND (:category IS NULL OR category = :category) AND id != :excludeId " +
            "ORDER BY RANDOM() LIMIT 1",
    )
    suspend fun randomFavorite(language: String, category: String?, excludeId: String, audience: String): MessageEntity?

    @Query(
        "SELECT * FROM messages WHERE language = :language AND hidden = 0 " +
            "AND audience IN ('any', :audience) " +
            "AND (:category IS NULL OR category = :category) AND id != :excludeId",
    )
    suspend fun candidates(language: String, category: String?, excludeId: String, audience: String): List<MessageEntity>

    @Query("SELECT text FROM messages WHERE source = 'USER'")
    suspend fun userTexts(): List<String>

    @Query("SELECT * FROM messages WHERE id = :id")
    fun observe(id: String): Flow<MessageEntity?>

    @Query("SELECT * FROM messages WHERE favorite = 1 ORDER BY updatedAt DESC, id")
    fun observeFavorites(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE source = 'USER' ORDER BY updatedAt DESC, id")
    fun observeUserMessages(): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET favorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean)

    @Query("SELECT COUNT(*) FROM messages WHERE language = :language AND hidden = 0 AND audience IN ('any', :audience)")
    suspend fun countVisible(language: String, audience: String): Int
}

@Database(entities = [MessageEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        /** v2: Spalte `audience` (bestehende Sätze sind für beide). */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE messages ADD COLUMN audience TEXT NOT NULL DEFAULT 'any'")
            }
        }
    }
}
