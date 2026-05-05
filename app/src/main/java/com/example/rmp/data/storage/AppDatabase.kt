package com.example.rmp.data.storage

import android.content.Context
import androidx.room.*
import com.example.rmp.data.model.*

@Database(
    entities = [
        User::class,
        UserTrackLike::class,
        UserPlaylist::class,
        PlaylistTrack::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun userTrackLikeDao(): UserTrackLikeDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "music_app_db")
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}