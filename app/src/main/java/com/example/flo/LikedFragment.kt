package com.example.flo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLikedBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class LikedFragment : Fragment() {

    lateinit var binding: FragmentLikedBinding

    private lateinit var songDB: SongDatabase

    lateinit var mypageRVAdapter: MypageRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentLikedBinding.inflate(inflater,container,false)

        songDB = SongDatabase.getInstance(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerview()
        initClickEvent()
    }

    // 클릭 이벤트
    private fun initClickEvent(){
        binding.btnSelectAll.setOnClickListener {
            binding.btnSelectAllIV.setColorFilter(resources.getColor(R.color.select_color))
            binding.btnSelectAllTV.setTextColor(resources.getColor(R.color.select_color))

            showBottomSheetDialog()
        }

    }

    // 음악 RecyclerView 초기화
    private fun initRecyclerview(){
        mypageRVAdapter = MypageRVAdapter()

        mypageRVAdapter.setMyItemClickListener(object : MypageRVAdapter.MyItemClickListener{
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
            }
        })

        binding.mypageRV.apply {
            adapter = mypageRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) // 수직 레이아웃으로 설정
        }

        mypageRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)
    }

    private fun showBottomSheetDialog(){

        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        val btn_delete = bottomSheetView.findViewById<ImageView>(R.id.btn_download)

        btn_delete.setOnClickListener {
            deleteAllLiked()
            binding.btnSelectAllIV.clearColorFilter()
            binding.btnSelectAllTV.setTextColor(resources.getColor(R.color.black))
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun deleteAllLiked(){

        while(mypageRVAdapter.songs.isNotEmpty()){
            mypageRVAdapter.removeSong(0)
        }
    }
}