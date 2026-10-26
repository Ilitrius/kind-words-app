package app.rpkindwords.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
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
)

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
            "AND (:category IS NULL OR category = :category) AND id != :excludeId " +
            "ORDER BY RANDOM() LIMIT 1",
    )
    suspend fun random(language: String, category: String?, excludeId: String): MessageEntity?

    @Query(
        "SELECT * FROM messages WHERE language = :language AND hidden = 0 AND favorite = 1 " +
            "AND (:category IS NULL OR category = :category) AND id != :excludeId " +
            "ORDER BY RANDOM() LIMIT 1",
    )
    suspend fun randomFavorite(language: String, category: String?, excludeId: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE id = :id")
    fun observe(id: String): Flow<MessageEntity?>

    @Query("SELECT * FROM messages WHERE favorite = 1 ORDER BY updatedAt DESC, id")
    fun observeFavorites(): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE source = 'USER' ORDER BY updatedAt DESC, id")
    fun observeUserMessages(): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET favorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean)

    @Query("SELECT COUNT(*) FROM messages WHERE language = :language AND hidden = 0")
    suspend fun countVisible(language: String): Int
}

@Database(entities = [MessageEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
