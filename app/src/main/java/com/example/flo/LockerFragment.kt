package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {
    private lateinit var binding: FragmentLockerBinding
    private val information = arrayListOf("저장한곡", "음악파일", "저장앨범")
    private lateinit var lockerAdapter: LockerVPAdapter
    val bottomSheetFragment = BottomSheetFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter

        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        binding.lockerLoginTv.setOnClickListener{
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        // 전체선택 버튼 클릭 시 SavedSongFragment에 전달
        binding.lockerSelectAllTv.setOnClickListener {
            lockerAdapter.savedSongFragment.selectAllSongs(true)
        }

        binding.lockerSelectAllImgIv.setOnClickListener {
            lockerAdapter.savedSongFragment.selectAllSongs(true)
        }

        setupBottomSheetListener()

        return binding.root
    }

    private fun setupBottomSheetListener() {
        bottomSheetFragment.setBottomSheetListener(object : BottomSheetFragment.BottomSheetListener {
            override fun onDeleteSelected() {
                lockerAdapter.savedSongFragment.deleteSelectedSongs()
            }
        })

        binding.lockerSelectAllTv.setOnLongClickListener {
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
            true
        }

        binding.lockerSelectAllImgIv.setOnLongClickListener {
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
            true
        }
    }

    override fun onStart() {
        super.onStart()

        initViews()
    }

    private fun initViews() {
        val jwt: Int = getJwt()

        if (jwt == 0){
            binding.lockerLoginTv.text = "로그인"

            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
        else{
            binding.lockerLoginTv.text = "로그아웃"

            binding.lockerLoginTv.setOnClickListener {
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth" , AppCompatActivity.MODE_PRIVATE)

        return spf!!.getInt("jwt", 0)
    }

    private fun logout() {
        val spf = activity?.getSharedPreferences("auth" , AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        editor.remove("jwt")
        editor.apply()
    }
}
