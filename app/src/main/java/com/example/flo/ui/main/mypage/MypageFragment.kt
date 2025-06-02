package com.example.flo.ui.main.mypage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.data.remote.AuthResponse
import com.example.flo.data.remote.AuthService
import com.example.flo.data.remote.TestResponse
import com.example.flo.ui.signin.LoginActivity
import com.example.flo.ui.main.MainActivity
import com.example.flo.utils.db.SongDatabase
import com.example.flo.databinding.FragmentMypageBinding
import com.example.flo.utils.api.TestView
import com.google.android.material.tabs.TabLayoutMediator


class MypageFragment : Fragment(), TestView {

    // FragmentMypageBinding을 위한 변수
    private var _binding: FragmentMypageBinding? = null
    private var binding
        get() = _binding!!
        set(value){
            _binding = value
        }

    private lateinit var songDB: SongDatabase

    private val information = arrayListOf("내 리스트", "좋아요", "저장앨범", "많이 들은", "팔로잉")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        songDB = SongDatabase.Companion.getInstance(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()  // 음악 RecyclerView 초기화
        //initClickListeners()
    }

    override fun onStart() {
        super.onStart()
        initView()
        binding.testTv.setOnClickListener {
            test()
        }
    }

    // 음악 ViewPager 초기화
    private fun initViewPager() {
        val mypageAdapter = MypageVPAdapter(this)
        binding.mypageVP.adapter = mypageAdapter

        TabLayoutMediator(binding.mypageTabLayout, binding.mypageVP) { tab, position ->
            tab.text = information[position]
        }.attach()
    }

    private fun initClickListeners() {
        binding.textLogin.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }

    private fun getJwt(): String{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)!!
        return spf.getString("jwt","")!!
    }

    private fun initView() {
        val jwt: String? = getJwt()
        if (jwt == "") {
            binding.textLogin.text = "로그인"
            binding.textLogin.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        } else {
            binding.textLogin.text = "로그아웃"
            binding.textLogin.setOnClickListener {
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun logout(){
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()
        editor.remove("jwt")
        editor.apply()

    }

    private fun test(){
        val authService = AuthService()
        authService.setTestView(this)

        authService.test(getJwt())
    }

    override fun onTestSuccess(response: TestResponse) {
        Toast.makeText(activity, response.result, Toast.LENGTH_SHORT).show()
    }

    override fun onTestFailure(response: TestResponse) {
        Toast.makeText(activity, response.message, Toast.LENGTH_SHORT).show()
    }

//    private fun initRecyclerview(){
//        val mypageRVAdapter = MypageRVAdapter()
//
//        mypageRVAdapter.setMyItemClickListener(object : MypageRVAdapter.MyItemClickListener{
//            override fun onRemoveSong(songId: Int) {
//                songDB.songDao().updateIsLikeById(false, songId)
//            }
//        })
//
//        binding.mypageRV.apply {
//            adapter = mypageRVAdapter
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) // 수직 레이아웃으로 설정
//        }
//
//        mypageRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)
//    }
}