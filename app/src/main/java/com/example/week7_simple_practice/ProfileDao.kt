package com.example.week7_simple_practice

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

//Data Access Object
@Dao
interface ProfileDao {
    @Insert
    fun insert(profile: Profile)

    @Update
    fun Update(profile: Profile)

    @Delete
    fun Delete(profile: Profile)

    @Query("SELECT * FROM Profile")
    fun getAll() : List<Profile>

}