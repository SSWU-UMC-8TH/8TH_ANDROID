package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        // 데이터 리스트 생성 더미 데이터
        albumDatas.apply {
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp,
                arrayListOf(
                    Song("Butter", "방탄소년단 (BTS)", 0, 60, false, "music_butter", R.drawable.img_album_exp))))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2,
                arrayListOf(Song("Lilac", "아이유 (IU)", 0, 60, false, "music_lilac", R.drawable.img_album_exp2))))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3,
                arrayListOf(Song("Next Level", "에스파 (AESPA)", 0, 60, false, "music_next", R.drawable.img_album_exp3))))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4,
                arrayListOf(Song("Boy with Luv", "방탄소년단 (BTS)", 0, 60, false, "music_boy", R.drawable.img_album_exp4))))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5,
                arrayListOf(Song("BBoom BBoom", "모모랜드 (MOMOLAND)", 0, 60, false, "music_bboom", R.drawable.img_album_exp5))))
            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6,
                arrayListOf(Song("Weekend", "태연 (Tae Yeon)", 0, 60, false, "music_flu", R.drawable.img_album_exp6))))
        }

        // 더미데이터랑 Adapter 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        // 리사이클러뷰에 어댑터를 연결
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        albumRVAdapter.setMyItemClickListener(object: AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }
            override fun onPlayAlbum(album: Album) {
                album.songs?.firstOrNull()?.let { firstSong ->
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
}
