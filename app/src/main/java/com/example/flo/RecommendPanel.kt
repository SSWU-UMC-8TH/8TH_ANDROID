package com.example.flo

data class RecommendPanel(
    var panelImg : Int,
    var album1: Album,
    var album2: Album,
    var ment: String
){
    companion object{
        var list = mutableMapOf<Int, RecommendPanel>(
            0 to RecommendPanel(R.drawable.img_panel_lofi, Album.list["i_am_music"]!!, Album.list["ww3"]!!,"hip-hop"),
            1 to RecommendPanel(R.drawable.img_panel_jazz_hiphop, Album.list["modal_soul"]!!, Album.list["lifes_like"]!!,"jazz")
        )
    }
}
