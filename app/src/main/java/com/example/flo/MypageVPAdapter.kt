package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MypageVPAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> BlankFragment()
            1 -> LikedFragment()
            2 -> FirebaseFragment()
            3 -> BlankFragment()
            else -> BlankFragment()
        }
    }

    override fun getItemCount(): Int = 5

}