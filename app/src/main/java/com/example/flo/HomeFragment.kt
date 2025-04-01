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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHomeBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

        binding.album1.setOnClickListener {
            sendToAlbumFragment(R.drawable.album1, "성민", "성민")
        }
        binding.album2.setOnClickListener {
            sendToAlbumFragment(R.drawable.album2, "유성민", "유성민")
        }
        binding.album3.setOnClickListener {
            sendToAlbumFragment(R.drawable.album3, "성민민", "성민민")
        }

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
        recommendAdapter.addFragment(RecommendFragment(RecommendPanel.list[0]!!))
        recommendAdapter.addFragment(RecommendFragment(RecommendPanel.list[1]!!))

        val recommendPager : ViewPager2 = binding.recommendScroll
        recommendPager.adapter = recommendAdapter
        recommendPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun sendToAlbumFragment(imageResId: Int, title: String, singer: String) {
        val albumFragment = AlbumFragment.newInstance(imageResId, title, singer)
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, albumFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}