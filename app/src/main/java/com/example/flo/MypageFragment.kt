package com.example.flo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentMypageBinding
import com.google.android.material.tabs.TabLayoutMediator


class MypageFragment : Fragment() {

    // FragmentMypageBinding을 위한 변수
    private var _binding: FragmentMypageBinding? = null
    private var binding
        get() = _binding!!
        set(value){
            _binding = value
        }

    private lateinit var songDB: SongDatabase

    private val information = arrayListOf("내 리스트", "좋아요", "저장한 곡", "많이 들은", "팔로잉")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()  // 음악 RecyclerView 초기화
    }

    override fun onStart() {
        super.onStart()
    }

    // 음악 ViewPager 초기화
    private fun initViewPager() {
        val mypageAdapter = MypageVPAdapter(this)
        binding.mypageVP.adapter = mypageAdapter

        TabLayoutMediator(binding.mypageTabLayout, binding.mypageVP) { tab, position ->
            tab.text = information[position]
        }.attach()
    }

//    private fun initRecyclerview(){
//        val mypageRVAdapter = MypageRVAdapter()
//
//        mypageRVAdapter.setMyItemClickListener(object : MypageRVAdapter.MyItemClickListener{
//            override fun onRemoveSong(songId: Int) {
//                songDB.songDao().updateIsLikeById(false, songId)
//            }
//        })
//
//        binding.mypageRV.apply {
//            adapter = mypageRVAdapter
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) // 수직 레이아웃으로 설정
//        }
//
//        mypageRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)
//    }
}