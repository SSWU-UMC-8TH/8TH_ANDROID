package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson
import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()
    private lateinit var songDB: SongDatabase

    private val timer = Timer()

    private val bannerSlideHandler = Handler(Looper.getMainLooper())
    private var currentPositionBanner = 0
    private val pannelSlideHandler = Handler(Looper.getMainLooper())
    private var currentPositionPannel = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        /*binding.homeAlbumImgIv1.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm , AlbumFragment())
                .commitAllowingStateLoss()
        }*/

        inputDummyAlbums()

        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())
        Log.d("albumlist", albumDatas.toString())

        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        val albums = songDB.albumDao().getAlbums()
        val allSongs = songDB.songDao().getSongs()

        albumDatas.clear()
        albumDatas.addAll(albums.map { album ->
            album.songs = ArrayList(allSongs.filter { it.albumIdx == album.id })
            album
        })
        albumRVAdapter.notifyDataSetChanged()

        albumRVAdapter.setMyItemClickListener(object: AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }
            override fun onPlayAlbum(album: Album) {
                album.songs.firstOrNull()?.let { firstSong ->
                    (activity as? MainActivity)?.updateMiniPlayerWithSong(firstSong)
                }
            }
        })

        val bannerAdapter = BannerVPAdapter(this)
        val pannelAdapter = PannelVPAdapter(this)
        // 배너 프래그먼트 추가
        val bannerImages = listOf(R.drawable.img_home_viewpager_exp, R.drawable.img_home_viewpager_exp2 )
        bannerImages.forEach { bannerAdapter.addFragment(BannerFragment(it)) }

        // 패널 프래그먼트 추가
        val pannelImages = listOf(
            R.drawable.img_first_album_default, R.drawable.img_album_exp3, R.drawable.img_album_exp4,
            R.drawable.img_album_exp5, R.drawable.img_album_exp6 )
        pannelImages.forEach { pannelAdapter.addFragment(PannelFragment(it)) }

        //bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))

        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        startBannerAutoSlide(bannerAdapter)

        binding.homePannelBackgroundVp.adapter = pannelAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.homePannelIndicator.setViewPager(binding.homePannelBackgroundVp)
        startPannelAutoSlide(pannelAdapter)


        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }



    private fun startBannerAutoSlide(adapter: BannerVPAdapter) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                bannerSlideHandler.post {
                    if (currentPositionBanner == adapter.itemCount) {
                        currentPositionBanner = 0
                    }
                    binding.homeBannerVp.setCurrentItem(currentPositionBanner, true)
                    currentPositionBanner++
                }
            }
        }, 3000, 3000)
    }

    private fun startPannelAutoSlide(adapter: PannelVPAdapter) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                pannelSlideHandler.post {
                    if (currentPositionPannel == adapter.itemCount) {
                        currentPositionPannel = 0
                    }
                    binding.homePannelBackgroundVp.setCurrentItem(currentPositionPannel, true)
                    currentPositionPannel++
                }
            }
        }, 3000, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }

    private fun inputDummyAlbums(){
        val songDB = SongDatabase.getInstance(requireActivity())!!
        val songs = songDB.albumDao().getAlbums()

        if (songs.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                1,
                "Lilac",
                "아이유 (IU)",
                R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                2,
                "Butter",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp
            )
        )

        songDB.albumDao().insert(
            Album(
                3,
                "Next Level",
                "에스파 (AESPA)",
                R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                4,
                "Boy with Luv",
                "방탄소년단(BTS)",
                R.drawable.img_album_exp4,
            )
        )


        songDB.albumDao().insert(
            Album(
                5,
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                R.drawable.img_album_exp5
            )
        )

        val songDBData = songDB.albumDao().getAlbums()
        Log.d("DB data", songDBData.toString())
    }
}
