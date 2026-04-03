package com.example.screenshame.data.db

import androidx.room.*

// table for storing the screentime limits set by the user
@Entity (tableName = "app_limits")
data class AppLimit (
    @PrimaryKey val packageName : String,
    val appName : String,
    val dailyLimitMinutes : Int
)

// table for storing daily usage snapshots
@Entity (tableName = "usage_snapshots")
data class UsageSnapshot (
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val appName : String,
    val date : String, // dd - mm - yyyy
    val usageMinutes: Int
)

// dao for app limits
@Dao
interface AppLimitDao {
    @Query ( "SELECT * FROM app_limits" )
    suspend fun getAll() : List<AppLimit>

    @Query ( "SELECT * FROM  app_limits WHERE packageName = :packageName" )
    suspend fun getLimit( packageName: String ) : AppLimit?

    @Insert ( onConflict = OnConflictStrategy.REPLACE )
    suspend fun upsert( limit : AppLimit )

    @Delete
    suspend fun delete( limit: AppLimit )
}

// dao for usage snapshots
@Dao
interface UsageSnapshotDao {
    @Query ( "SELECT * FROM usage_snapshots WHERE date = :date" )
    suspend fun getSnapshotsForDate ( date: String ) : List<UsageSnapshot>

    @Query ( "SELECT * FROM usage_snapshots WHERE packageName = :packageName ORDER BY date DESC LIMIT 7" )
    suspend fun getlast7days( packageName: String ) : List<UsageSnapshot>

    @Insert ( onConflict = OnConflictStrategy.REPLACE )
    suspend fun upsert ( snapshot: UsageSnapshot )
}

@Database ( entities = [AppLimit::class, UsageSnapshot::class], version = 1 )
abstract class AppDatabase : RoomDatabase() {
    abstract fun appLimitDao() : AppLimitDao
    abstract fun usageSnapshotDao() : UsageSnapshotDao

    companion object {
        @Volatile private var instance : AppDatabase? = null

        fun getInstance ( context : android.content.Context ) : AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "screenshame_db"
                ).build().also { instance = it }
            }
        }
    }
}