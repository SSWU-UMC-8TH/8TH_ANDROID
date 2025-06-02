package com.example.flo.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album(
    @PrimaryKey(autoGenerate = false) var id: Int = 0,
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null
){
    @Ignore
    var songs: ArrayList<Song> = arrayListOf()
}

