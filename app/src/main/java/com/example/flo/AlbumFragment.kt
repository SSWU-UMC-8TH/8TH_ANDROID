package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {
    private lateinit var album : Album
    private var gson: Gson = Gson()
    //var songFragment : SongFragment? = null
    //var detailFragment : DetailFragment? = null
    //var videoFragment : VideoFragment? = null

    private lateinit var binding: FragmentAlbumBinding
    private val information = arrayListOf("수록곡", "상세정보", "영상")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAlbumBinding.inflate(inflater, container, false)

        val albumJson = arguments?.getString("album")
        album = Gson().fromJson(albumJson, Album::class.java)
        setInit(album)

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.mainFrame, HomeFragment()).commitAllowingStateLoss()
        }

     /*   binding.songLalacLayout.setOnClickListener {
            Toast.makeText(activity, "LILAC", Toast.LENGTH_SHORT).show()
        }*/
        val albumAdapter= AlbumVPAdapter(this, album.ment.toString())
        binding.albumContentVp.adapter=albumAdapter
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp){
            tab, position ->
            tab.text = information[position]

        }.attach()

        return binding.root
    }

    private fun setInit(album: Album) {
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title
        binding.albumSingerNameTv.text = album.singer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    companion object {
        @JvmStatic
        fun newInstance(album: Album) = AlbumFragment().apply {
            arguments = Bundle().apply {
                putParcelable("album", album)
            }
        }
    }
}