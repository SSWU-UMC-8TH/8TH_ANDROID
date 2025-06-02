package com.example.flo.data.entities

import androidx.room.*

@Entity(tableName = "AlbumTable")
data class Album(
    @PrimaryKey(autoGenerate = false) var albumIdx: Int = 0,
    var title: String = "",
    var singer: String = "",
    var coverImg: Int? = null,
    var ment: String = "hi",
    //var songs: ArrayList<Song>? = null,
    //val video
)