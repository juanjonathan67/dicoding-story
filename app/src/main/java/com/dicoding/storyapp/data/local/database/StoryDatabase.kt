package com.dicoding.storyapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.storyapp.data.local.data.RemoteKeys
import com.dicoding.storyapp.data.remote.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var instance: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}