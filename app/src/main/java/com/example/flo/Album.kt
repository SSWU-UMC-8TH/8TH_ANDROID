package com.example.flo

data class Album(
    var albumFragment : AlbumFragment
    //var songFragment: SongFragment,
    //var detailFragment: DetailFragment,
    //var videoFragment: VideoFragment
){
    companion object{
        var list = mutableMapOf<String, Album>(
            "modal_soul" to Album(AlbumFragment.newInstance(R.drawable.img_modal_soul, "Modal Soul", "Nujabes")),
            "lifes_like" to Album(AlbumFragment.newInstance(R.drawable.img_lifes_like, "Lifes Like", "Jazzyfact")),
            "ww3" to Album(AlbumFragment.newInstance(R.drawable.img_ww3, "WW3", "YE")),
            "i_am_music" to Album(AlbumFragment.newInstance(R.drawable.img_i_am_music, "I am Music", "Playboy Carti"))
        )
    }
}
