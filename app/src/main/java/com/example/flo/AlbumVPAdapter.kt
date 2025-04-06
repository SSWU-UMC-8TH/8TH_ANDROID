package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AlbumVPAdapter(fragment: Fragment, val detail: String) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SongFragment()
            1 -> DetailFragment.newInstance(detail)
            else -> VideoFragment()
        }
    }

    override fun getItemCount(): Int = 3
}