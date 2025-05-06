package com.example.flo

import android.R
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Song (
    val title : String = "",
    val singer : String = "",
    var second : Int = 0,
    var playTime: Int = 0,
    var isPlaying : Boolean = false,
    var music : String = ""
): Parcelable