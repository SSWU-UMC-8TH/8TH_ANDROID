package com.example.flo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val coverImg: Int,
    val title: String,
    val singer: String,
    //val songList,
    val ment: String
    //val video
):Parcelable{
    companion object{
        val list = mutableMapOf<String, Album>(
            "modal_soul" to Album(R.drawable.img_modal_soul, "Modal Soul", "Nujabes", "ment1"),
            "lifes_like" to Album(R.drawable.img_lifes_like, "Lifes Like", "Jazzyfact", "ment2"),
            "ww3" to Album(R.drawable.img_ww3, "WW3", "YE", "ment3"),
            "i_am_music" to Album(R.drawable.img_i_am_music, "I am Music", "Playboy Carti", "ment4")
        )
    }
}
