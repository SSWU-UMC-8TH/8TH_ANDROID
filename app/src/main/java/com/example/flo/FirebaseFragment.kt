package com.example.flo

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLikedBinding

class FirebaseFragment : Fragment() {

    lateinit var binding: FragmentLikedBinding

    lateinit var albumDB: SongDatabase

    lateinit var adapter: FirebaseRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikedBinding.inflate(inflater, container, false)

        albumDB = SongDatabase.getInstance(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerview()
    }

    private fun getJwt():Int{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getInt("jwt",0)
    }

    private fun initRecyclerview() {
        val jwt: Int = getJwt()
        val albums = albumDB.albumDao().getLikedAlbums(jwt) as ArrayList<Album>

        adapter = FirebaseRVAdapter(albums)

        adapter.setMyItemClickListener(object : FirebaseRVAdapter.MyItemClickListener{
            override fun onDislikedAlbum(albumId: Int) {
                albumDB.albumDao().disLikedAlbum(jwt, albumId)
                Toast.makeText(activity, "앨범이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })


        binding.mypageRV.apply {
            adapter = this@FirebaseFragment.adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) // 수직 레이아웃으로 설정
        }

    }
}