package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flo.databinding.FragmentLockerMusicfileBinding

class LockerVPAdapter (fragment : Fragment) : FragmentStateAdapter(fragment) {

    val savedSongFragment = SavedSongFragment()
    private val otherFragment = Fragment()  // 음악파일 탭 대체용 (필요시 교체)

    override fun getItemCount(): Int  = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> savedSongFragment
            else -> MusicFileFragment()
        }
    }
}