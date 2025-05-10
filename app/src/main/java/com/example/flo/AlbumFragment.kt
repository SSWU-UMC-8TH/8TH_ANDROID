package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {

    private lateinit var album: Album
    private var _binding: FragmentAlbumBinding? = null
    private var binding
        get() = _binding!!
        set(value){
            _binding = value
        }

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumJson = arguments?.getString("home2album")
        album = Gson().fromJson(albumJson, Album::class.java)

        setInit(album)
        setupViewPager()

        // 뒤로가기 버튼 클릭 리스너 설정
        binding.albumBackIv.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 앨범 정보를 초기화하는 함수
    private fun setInit(album: Album) {
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title
        binding.albumSingerNameTv.text = album.singer
    }

    // ViewPager와 TabLayout 설정하는 함수
    private fun setupViewPager() {
        val albumAdapter = AlbumVPAdapter(this, album.ment.toString())
        binding.albumContentVp.adapter = albumAdapter

        // TabLayout과 ViewPager 연결
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()
    }
}