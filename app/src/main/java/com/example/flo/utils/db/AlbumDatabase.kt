package com.example.flo.utils.db

import android.content.Context
import androidx.room.*
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Like
import com.example.flo.data.entities.Song
import com.example.flo.data.entities.User
import com.example.flo.utils.db.dao.AlbumDao

@Database(entities = [Album::class, Song::class, User::class, Like::class], version = 5)
abstract class AlbumDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao

    companion object{
        private var instance: AlbumDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AlbumDatabase{
            if(instance==null){
                synchronized(AlbumDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AlbumDatabase::class.java,
                        "album-database"
                    ).allowMainThreadQueries().build()
                }
            }
            return instance!!
        }
    }
}