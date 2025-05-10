package com.example.flo

import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import me.relex.circleindicator.CircleIndicator3
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.ItemAlbumBinding
import com.google.gson.Gson

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()
    private var panelList = ArrayList<RecommendPanel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        var currentPage = 0
        //페이지 변경하기
        fun setPage(){
            if(currentPage == 2)
                currentPage = 0
            binding.bannerPager.setCurrentItem(currentPage, true)
            currentPage+=1
        }

        val handler=Handler(Looper.getMainLooper()){
            setPage()
            true
        }
        class PagerRunnable:Runnable{
            override fun run() {
                while(true){
                    try {
                        Thread.sleep(1000)
                        handler.sendEmptyMessage(0)
                    } catch (e : InterruptedException){
                        Log.d("interupt", "interupt발생")
                    }
                }
            }
        }

        Thread(PagerRunnable()).start()

        albumDatas.apply{
            add(Album("Butter", "BTS", R.drawable.img_album_exp))
            add(Album("Lilac", "아이유(IU)", R.drawable.img_album_exp2))
            add(Album("Next Level", "에스파(AESPA)", R.drawable.img_album_exp3))
            add(Album("Boy with Luv", "BTS", R.drawable.img_album_exp4))
            add(Album("BBoom BBoom", "모모랜드", R.drawable.img_album_exp5))
            add(Album("Weekend", "태연", R.drawable.img_album_exp6))
        }

        val albumRVAdapter = AlbumRVAdapter<ItemAlbumBinding>(
            albumList = albumDatas,
            bindingInflater = { inflater, parent, _ -> ItemAlbumBinding.inflate(inflater, parent, false) },
            onBind = { binding, album, _ ->
                binding.itemAlbumTitleTv.text = album.title
                binding.itemAlbumSigerTv.text = album.singer
                binding.itemAlbumCoverImgIv.setImageResource(album.coverImg!!)
            }
            )
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }
        })

        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))

        val bannerPager : ViewPager2 = binding.bannerPager
        bannerPager.adapter = bannerAdapter
        bannerPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val child = binding.bannerPager.getChildAt(0)
        (child as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER

        val indicator : CircleIndicator3 = binding.indicator
        indicator.setViewPager(bannerPager)

        panelList.apply {
            add(RecommendPanel(R.drawable.img_panel_jazz_hiphop,
                Album("Modal Soul", "Nujabes",R.drawable.img_modal_soul),
                Album("Lifes Like", "Jazzyfact",R.drawable.img_lifes_like),
                "jazz"))
            add(RecommendPanel(R.drawable.img_panel_lofi,
                Album("WW3", "YE", R.drawable.img_ww3),
                Album("I am Music", "Playboy Carti", R.drawable.img_i_am_music),
                "hip hop"))
        }

        val recommendAdapter = RecommendVPAdapter(this)
        recommendAdapter.addFragment(RecommendFragment(panelList[0]))
        recommendAdapter.addFragment(RecommendFragment(panelList[1]))

        val recommendPager : ViewPager2 = binding.recommendScroll
        recommendPager.adapter = recommendAdapter
        recommendPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {
        val albumFragment = AlbumFragment().apply {
            arguments = Bundle().apply {
                val gson = Gson()
                val albumJson = gson.toJson(album)
                putString("album", albumJson)
            }
        }
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, albumFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}