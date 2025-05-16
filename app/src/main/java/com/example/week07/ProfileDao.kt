package com.example.week07

import androidx.room.*

@Dao
interface ProfileDao {
    @Insert
    fun insert(profile: Profile)

    @Update
    fun update(profile: Profile)

    @Delete
    fun delete(profile: Profile)

    @Query("SELECT * FROM Profile")
    fun getAll(): List<Profile>

    @Delete
    fun deleteAll(vararg profile: Profile)
}