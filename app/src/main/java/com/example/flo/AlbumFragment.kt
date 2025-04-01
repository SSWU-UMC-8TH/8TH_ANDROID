package com.example.flo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlbumFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlbumFragment : Fragment() {
    var imageResId: Int? = null
    var title: String? = null
    var singer: String? = null

    private lateinit var binding: FragmentAlbumBinding
    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            imageResId = it.getInt("imageResId") // 전달받은 이미지 ID 저장
            title = it.getString("title") // 전달받은 제목 저장
            singer = it.getString("singer") // 전달받은 가수 이름 저장
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAlbumBinding.inflate(inflater, container, false)

        imageResId?.let {
            binding.albumAlbumIv.setImageResource(it) // 이미지 설정
            binding.albumMusicTitleTv.text = title // 제목 설정
            binding.albumSingerNameTv.text = singer // 가수 이름 설정
        }
        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.mainFrame, HomeFragment()).commitAllowingStateLoss()
        }

//        binding.songLalacLayout.setOnClickListener {
//            Toast.makeText(activity, "LILAC", Toast.LENGTH_SHORT).show()
//        }
        val albumAdapter= AlbumVPAdapter(this)
        binding.albumContentVp.adapter=albumAdapter
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp){
            tab, position ->
            tab.text = information[position]

        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    companion object {
        fun newInstance(imageResId: Int, title: String, singer: String) = AlbumFragment().apply {
            arguments = Bundle().apply {
                putInt("imageResId", imageResId)
                putString("title", title)
                putString("singer", singer)
            }
        }
    }
}