package com.example.flo.ui.main.locker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerBinding
import com.example.flo.ui.login.LoginActivity
import com.example.flo.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.user.UserApiClient
import com.bumptech.glide.Glide
import com.example.flo.R


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
        bottomSheetFragment.setBottomSheetListener(object :
            BottomSheetFragment.BottomSheetListener {
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

    override fun onResume() {
        super.onResume()
        Log.d("LockerFragment", "onResume 호출됨")
        initViews()
    }

    private fun initViews() {
        Log.d("LockerFragment", "initViews() 호출됨")
        val spf = activity?.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        val nickname = spf?.getString("nickname", null)
        val profileUrl = spf?.getString("profile", null)
        val email = spf?.getString("email", null)
        val jwt = spf?.getString("jwt", null)
        val isLoggedIn = !nickname.isNullOrEmpty() && (!jwt.isNullOrEmpty() || profileUrl != null)

        Log.d("LockerFragment", "SharedPreferences - nickname: $nickname, profile: $profileUrl, jwt: $jwt")

        if (isLoggedIn) {
            // 로그인 상태
            binding.lockerLoginTv.text = "로그아웃 ($nickname)"
            binding.lockerNicknameTv.text = nickname
            binding.lockerEmailTv.text = email

            if (!profileUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(profileUrl)
                    .circleCrop()
                    .into(binding.lockerProfileIv)
            } else {
                binding.lockerProfileIv.setImageResource(R.drawable.default_profile)
            }

            binding.lockerLoginTv.setOnClickListener {
                kakaoLogout()
            }
        } else {
            // 비로그인 상태
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
            binding.lockerProfileIv.setImageResource(R.drawable.default_profile)
        }
    }


    private fun getJwt(): Boolean {
        val spf = activity?.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        val jwt = spf?.getString("jwt", null)
        return !jwt.isNullOrEmpty()
    }

    private fun logout() {
        val spf = activity?.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        spf?.edit()?.remove("jwt")?.apply()
    }

    private fun kakaoLogout() {
        val spf = activity?.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)

        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("KAKAO_LOGOUT", "로그아웃 실패", error)
            } else {
                spf?.edit()?.clear()?.apply()
                Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

}
