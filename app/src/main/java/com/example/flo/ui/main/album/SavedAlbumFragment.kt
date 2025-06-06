package com.example.flo.ui.main.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.FragmentLockerSavedalbumBinding

class SavedAlbumFragment : Fragment() {
    lateinit var binding: FragmentLockerSavedalbumBinding
    lateinit var albumDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedalbumBinding.inflate(inflater, container, false)

        albumDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview() {
        val userId = getUserId()

        // 비로그인 상태면 안내 메시지 띄우고 return
        if (userId == -1) {
            binding.lockerSavedAlbumEmptyTv.visibility = View.VISIBLE
            binding.lockerSavedSongRecyclerView.visibility = View.GONE
            binding.lockerSavedAlbumEmptyTv.text = "로그인 후 이용 가능합니다."
            return
        }

        // 로그인 상태면 정상적으로 RecyclerView 구성
        binding.lockerSavedAlbumEmptyTv.visibility = View.GONE
        binding.lockerSavedSongRecyclerView.visibility = View.VISIBLE

        binding.lockerSavedSongRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val albumRVAdapter = AlbumLockerRVAdapter()

        albumRVAdapter.setMyItemClickListener(object : AlbumLockerRVAdapter.MyItemClickListener {
            override fun onRemoveSong(songId: Int) {
                // 삭제 처리 필요시 여기에 구현
            }
        })

        binding.lockerSavedSongRecyclerView.adapter = albumRVAdapter

        val likedAlbums = albumDB.albumDao().getLikedAlbums(userId)
        albumRVAdapter.addAlbums(likedAlbums as ArrayList)
    }


    /*private fun getJwt() : Int {
        val spf = activity?.getSharedPreferences("auth2" , AppCompatActivity.MODE_PRIVATE)
        val jwt = spf!!.getInt("jwt", 0)
        Log.d("MAIN_ACT/GET_JWT", "jwt_token: $jwt")

        return jwt
    }*/

    private fun getUserId(): Int {
        val spf = activity?.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        return spf?.getInt("userId", -1) ?: -1
    }
}