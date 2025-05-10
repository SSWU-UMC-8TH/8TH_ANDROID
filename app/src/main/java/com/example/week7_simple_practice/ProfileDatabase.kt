package com.example.week7_simple_practice

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Profile::class], version = 1)
abstract class ProfileDatabase: RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        private var instance: ProfileDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ProfileDatabase? {
            if (instance == null) {
                synchronized(ProfileDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ProfileDatabase::class.java,
                        "user-database"     //다른 데베랑 이름 겹치면 꼬임
                    ).build()
                }
            }
            return instance
        }
    }
}