package com.example.flo.utils.db

import android.content.Context
import androidx.room.*
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Like
import com.example.flo.data.entities.Song
import com.example.flo.data.entities.User
import com.example.flo.utils.db.dao.AlbumDao
import com.example.flo.utils.db.dao.SongDao
import com.example.flo.utils.db.dao.UserDao

@Database(entities = [Song::class, User::class, Like::class, Album::class], version = 5)
abstract class SongDatabase: RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun userDao(): UserDao
    abstract fun albumDao(): AlbumDao

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