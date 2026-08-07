package com.bdaysquirrel.app.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "birthdays")
data class BirthdayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val day: Int,
    val month: Int,
    val year: Int? = null,
    val note: String = "",
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

@Dao
interface BirthdayDao {
    @Query("SELECT * FROM birthdays ORDER BY month, day, name COLLATE NOCASE")
    fun observeAll(): Flow<List<BirthdayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(birthday: BirthdayEntity)

    @Delete
    suspend fun delete(birthday: BirthdayEntity)
}

@Database(
    entities = [BirthdayEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2)],
)
abstract class BdayDatabase : RoomDatabase() {
    abstract fun birthdayDao(): BirthdayDao

    companion object {
        fun create(context: Context): BdayDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                BdayDatabase::class.java,
                "bdaysquirrel.db",
            ).build()
    }
}

class BirthdayRepository(
    private val dao: BirthdayDao,
) {
    val birthdays: Flow<List<BirthdayEntity>> = dao.observeAll()

    suspend fun add(
        name: String,
        day: Int,
        month: Int,
        year: Int?,
        note: String,
        photoUri: String?,
    ) {
        dao.insert(
            BirthdayEntity(
                name = name.trim(),
                day = day,
                month = month,
                year = year,
                note = note.trim(),
                photoUri = photoUri,
            ),
        )
    }

    suspend fun update(
        birthday: BirthdayEntity,
        name: String,
        day: Int,
        month: Int,
        year: Int?,
        note: String,
        photoUri: String?,
    ) {
        dao.insert(
            birthday.copy(
                name = name.trim(),
                day = day,
                month = month,
                year = year,
                note = note.trim(),
                photoUri = photoUri,
            ),
        )
    }

    suspend fun delete(birthday: BirthdayEntity) {
        dao.delete(birthday)
    }
}
