package com.example.flo

import android.content.Context
import androidx.room.*

@Database(entities = [Song::class], version = 2)
abstract class SongDatabase: RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object{
        private var instance: SongDatabase? = null

        @Synchronized
        fun getInstance(context: Context): SongDatabase{
            if (instance==null) {
                synchronized(SongDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SongDatabase::class.java,
                        "song-database"
                    ).allowMainThreadQueries().build()
                }
            }
            return instance!!
        }
    }
}