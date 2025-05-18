package com.example.flo

import androidx.room.*

@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    @Query("SELECT * FROM AlbumTable")
    fun getAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable WHERE albumIdx = :id")
    fun getAlbum(id: Int): Album

    @Query("DELETE FROM AlbumTable")
    fun deleteAll()
}