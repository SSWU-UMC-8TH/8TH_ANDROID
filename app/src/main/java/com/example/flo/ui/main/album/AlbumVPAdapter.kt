package com.example.flo.ui.main.album

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flo.ui.main.album.detail.DetailFragment
import com.example.flo.ui.main.album.songs.SongFragment
import com.example.flo.ui.main.album.video.VideoFragment

class AlbumVPAdapter(fragment: Fragment, val detail: String) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SongFragment()
            1 -> DetailFragment.Companion.newInstance(detail)
            else -> VideoFragment()
        }
    }

    override fun getItemCount(): Int = 3
}