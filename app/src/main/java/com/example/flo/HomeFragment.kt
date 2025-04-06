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

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHomeBinding



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

        val albumList = Album.list

        binding.album1.setOnClickListener {
            sendToAlbumFragment(albumList["modal_soul"]!!)
        }
        binding.album1Img.setImageResource(albumList["modal_soul"]!!.coverImg)
        binding.album1Title.text = albumList["modal_soul"]!!.title
        binding.album1Singer.text = albumList["modal_soul"]!!.singer

        binding.album2.setOnClickListener {
            sendToAlbumFragment(albumList["lifes_like"]!!)
        }
        binding.album2Img.setImageResource(albumList["lifes_like"]!!.coverImg)
        binding.album2Title.text = albumList["lifes_like"]!!.title
        binding.album2Singer.text = albumList["lifes_like"]!!.singer

        binding.album3.setOnClickListener {
            sendToAlbumFragment(albumList["ww3"]!!)
        }
        binding.album3Img.setImageResource(albumList["ww3"]!!.coverImg)
        binding.album3Title.text = albumList["ww3"]!!.title
        binding.album3Singer.text = albumList["ww3"]!!.singer

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

        val recommendAdapter = RecommendVPAdapter(this)
        val panelList = RecommendPanel.list
        recommendAdapter.addFragment(RecommendFragment(panelList[0]!!))
        recommendAdapter.addFragment(RecommendFragment(panelList[1]!!))

        val recommendPager : ViewPager2 = binding.recommendScroll
        recommendPager.adapter = recommendAdapter
        recommendPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun sendToAlbumFragment(album: Album) {
        val albumFragment = AlbumFragment.newInstance(album)
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, albumFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}