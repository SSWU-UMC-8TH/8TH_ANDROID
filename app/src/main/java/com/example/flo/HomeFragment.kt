package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import java.util.Timer
import java.util.TimerTask

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
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

        binding.homeAlbumImgIv1.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm , AlbumFragment())
                .commitAllowingStateLoss()
        }

        val bannerAdapter = BannerVPAdapter(this)
        val pannelAdapter = PannelVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        pannelAdapter.addFragment(PannelFragment(R.drawable.img_first_album_default))
        pannelAdapter.addFragment(PannelFragment(R.drawable.img_album_exp3))
        pannelAdapter.addFragment(PannelFragment(R.drawable.img_album_exp4))
        pannelAdapter.addFragment(PannelFragment(R.drawable.img_album_exp5))
        pannelAdapter.addFragment(PannelFragment(R.drawable.img_album_exp6))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        startBannerAutoSlide(bannerAdapter)

        binding.homePannelBackgroundVp.adapter = pannelAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.homePannelIndicator.setViewPager(binding.homePannelBackgroundVp)
        startPannelAutoSlide(pannelAdapter)


        return binding.root
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
