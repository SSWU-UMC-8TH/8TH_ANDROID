package com.example.flo

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import me.relex.circleindicator.CircleIndicator3
import android.os.Handler
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.ItemAlbumBinding
import com.google.gson.Gson

class HomeFragment : Fragment() {

    // Auto scroll을 위한 딜레이 (1초)
    private val SCROLL_DELAY = 1000L

    // FragmentHomeBinding을 위한 변수
    private var _binding: FragmentHomeBinding? = null
    private var binding
        get() = _binding!!
        set(value){
            _binding = value
        }

    // 앨범과 패널 데이터를 담을 리스트
    private val albumDatas = ArrayList<Album>()
    private val panelList = ArrayList<RecommendPanel>()
    private var currentPage = 0
    private var pagerHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var autoScrollRunnable: Runnable

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAlbumData() // 앨범 데이터 초기화
        initPanelData() // 패널 데이터 초기화
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false) // Fragment 레이아웃 초기화
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBanner() // 배너 초기화
        initTodayAlbumRV() // 오늘의 앨범 RecyclerView 초기화
        initRecommendViewPager() // 패널 ViewPager 초기화
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pagerHandler.removeCallbacks(autoScrollRunnable) // 배너 자동 스크롤 제거
        _binding = null // 바인딩 객체 해제
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // 앨범 데이터 초기화
    private fun initAlbumData() {
        albumDatas.apply {
            add(Album(1, "Butter", "BTS", R.drawable.img_album_exp))
            add(Album(2, "Lilac", "아이유(IU)", R.drawable.img_album_exp2))
            add(Album(3, "Next Level", "에스파(AESPA)", R.drawable.img_album_exp3))
            add(Album(4, "Boy with Luv", "BTS", R.drawable.img_album_exp4))
            add(Album(5, "BBoom BBoom", "모모랜드", R.drawable.img_album_exp5))
            add(Album(6, "Weekend", "태연", R.drawable.img_album_exp6))
            add(Album(7, "Modal Soul", "Nujabes", R.drawable.img_modal_soul))
            add(Album(8, "Lifes Like", "Jazzyfact", R.drawable.img_lifes_like))
            add(Album(9, "WW3", "YE", R.drawable.img_ww3))
            add(Album(10, "I am Music", "Playboy Carti", R.drawable.img_i_am_music))
        }
    }

    // 패널 데이터 초기화
    private fun initPanelData() {
        panelList.apply {
            add(
                RecommendPanel(
                    R.drawable.img_panel_jazz_hiphop,
                    Album(7, "Modal Soul", "Nujabes", R.drawable.img_modal_soul),
                    Album(8, "Lifes Like", "Jazzyfact", R.drawable.img_lifes_like),
                    "jazz"
                )
            )
            add(
                RecommendPanel(
                    R.drawable.img_panel_lofi,
                    Album(9, "WW3", "YE", R.drawable.img_ww3),
                    Album(10, "I am Music", "Playboy Carti", R.drawable.img_i_am_music),
                    "hip hop"
                )
            )
        }
    }

    // 오늘의 앨범 RecyclerView 초기화
    private fun initTodayAlbumRV() {
        val albumRVAdapter = AlbumRVAdapter(albumList = albumDatas)

        // 아이템 클릭 리스너 설정
        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album) // 앨범 클릭 시 AlbumFragment로 이동
            }


            override fun onPlayAlbum(position: Int) {
                (activity as? MainActivity)?.let{
                    it.findViewById<TextView>(R.id.main_miniplayer_title_tv).text = albumDatas[position].title
                    it.findViewById<TextView>(R.id.main_miniplayer_singer_tv).text = albumDatas[position].singer

                    if(mediaPlayer==null){
                        val music = resources.getIdentifier("music_hypeboy", "raw", it.packageName)
                        mediaPlayer = MediaPlayer.create(it, music)
                    }

                    if(mediaPlayer?.isPlaying()==false){
                        mediaPlayer?.start()
                    }
                }
            }
        })

        binding.homeTodayMusicAlbumRv.apply {
            adapter = albumRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) // 수평 레이아웃으로 설정
        }
    }

    // 배너 초기화 및 설정
    private fun initBanner() {
        val bannerAdapter = BannerVPAdapter(this)
        // 배너에 두 개의 이미지 추가
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))

        val bannerPager: ViewPager2 = binding.bannerPager
        bannerPager.adapter = bannerAdapter
        bannerPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 수평 스크롤 설정

        // 배너 Pager의 오버스크롤을 방지
        val child = bannerPager.getChildAt(0)
        (child as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER

        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(bannerPager) // 배너 인디케이터 연결

        startAutoBannerScroll() // 자동 배너 스크롤 시작
    }

    // 자동 배너 스크롤을 시작하는 함수
    private fun startAutoBannerScroll() {
        autoScrollRunnable = object : Runnable {
            override fun run() {
                _binding.let{
                    if (currentPage == 2) currentPage = 0 // 마지막 페이지 후 첫 번째 페이지로 돌아감
                    binding.bannerPager.setCurrentItem(currentPage, true) // 배너 페이지 전환
                    currentPage++ // 현재 페이지 인덱스 증가
                    pagerHandler.postDelayed(this, SCROLL_DELAY) // 일정 시간 간격으로 반복
                }
            }
        }
        pagerHandler.postDelayed(autoScrollRunnable, SCROLL_DELAY) // 첫 번째 실행
    }

    // 패널 ViewPager 초기화
    private fun initRecommendViewPager() {
        val recommendAdapter = RecommendVPAdapter(this)
        panelList.forEach { panel ->
            recommendAdapter.addFragment(RecommendFragment(panel)) // 패널을 각 ViewPager 아이템으로 추가
        }

        binding.recommendScroll.apply {
            adapter = recommendAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL // 수평 스크롤 설정
        }
    }

    // 앨범 아이템 클릭 시 해당 앨범의 세부 정보를 보여주는 AlbumFragment로 이동
    private fun changeAlbumFragment(album: Album) {
        val albumFragment = AlbumFragment().apply {
            arguments = Bundle().apply {
                val gson = Gson()
                val albumJson = gson.toJson(album) // 앨범 객체를 JSON 문자열로 변환
                putString("home2album", albumJson) // 번들에 JSON 데이터 넣기
            }
        }

        // MainActivity에서 Fragment를 교체하여 이동
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, albumFragment) // AlbumFragment로 교체
            .addToBackStack(null) // 백스택에 추가하여 뒤로가기 가능
            .commitAllowingStateLoss() // 커밋 후 상태 손실 허용
    }
}
