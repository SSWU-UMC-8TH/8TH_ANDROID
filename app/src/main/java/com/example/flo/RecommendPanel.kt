package com.example.flo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendPanel(
    var panelImg : Int,
    var album1: Album,
    var album2: Album,
    var ment: String
):Parcelable{
    companion object{
        val albumList = Album.list
        val list = mapOf<Int, RecommendPanel>(
            0 to RecommendPanel(R.drawable.img_panel_lofi, albumList["i_am_music"]!!, albumList["ww3"]!!,"hip-hop"),
            1 to RecommendPanel(R.drawable.img_panel_jazz_hiphop, albumList["modal_soul"]!!, albumList["lifes_like"]!!,"jazz")
        )
    }
}
