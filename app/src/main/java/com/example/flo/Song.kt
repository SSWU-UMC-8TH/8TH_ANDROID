package com.example.flo

import androidx.room.*

@Entity(tableName = "SongTable")
data class Song (
    val title : String = "",
    val singer : String = "",
    var second : Int = 0,
    var playTime: Int = 50,
    var isPlaying : Boolean = false,
    var music : String = "",
    var coverImg: Int? = null,
    var albumIdx : Int = 0,
    var isLike : Boolean = false,
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}