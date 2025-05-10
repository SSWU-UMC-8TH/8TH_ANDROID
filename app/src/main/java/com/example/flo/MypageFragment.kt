package com.example.flo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentMypageBinding
import com.example.flo.databinding.ItemMypageBinding


class MypageFragment : Fragment() {

    // FragmentMypageBinding을 위한 변수
    private var _binding: FragmentMypageBinding? = null
    private var binding
        get() = _binding!!
        set(value){
            _binding = value
        }

    // 음악 데이터를 담을 리스트
    private val songDatas = ArrayList<Album>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSongData()  // 음악 데이터 초기화
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSongList()  // 음악 RecyclerView 초기화
    }

    // 음악 데이터 초기화
    private fun initSongData() {
        songDatas.apply {
            add(Album("Butter", "BTS", R.drawable.img_album_exp))
            add(Album("Lilac", "아이유(IU)", R.drawable.img_album_exp2))
            add(Album("Next Level", "에스파(AESPA)", R.drawable.img_album_exp3))
            add(Album("Boy with Luv", "BTS", R.drawable.img_album_exp4))
            add(Album("BBoom BBoom", "모모랜드", R.drawable.img_album_exp5))
            add(Album("Weekend", "태연", R.drawable.img_album_exp6))
            add(Album("WW3", "Kanye West", R.drawable.img_ww3))
            add(Album("I am Music", "Playboy Carti", R.drawable.img_i_am_music))
            add(Album("Modal Soul", "Nujabes", R.drawable.img_modal_soul))
            add(Album("Lifes Like", "Jazzyfact", R.drawable.img_lifes_like))
        }
    }

    // 음악 RecyclerView 초기화
    private fun initSongList(){
        val albumRVAdapter = AlbumRVAdapter<ItemMypageBinding>(
            albumList = songDatas,
            bindingInflater = { inflater, parent, _ ->
                ItemMypageBinding.inflate(inflater, parent, false) // 아이템 레이아웃 인플레이트
            },
            onBind = { binding, album, _ ->
                // 앨범 데이터를 아이템 뷰에 바인딩
                binding.itemSongTitleTv.text = album.title
                binding.itemSongSingerTv .text = album.singer
                binding.itemSongImgIv.setImageResource(album.coverImg!!)
            }
        )

        binding.mypageRV.apply {
            adapter = albumRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) // 수직 레이아웃으로 설정
        }

    }
}