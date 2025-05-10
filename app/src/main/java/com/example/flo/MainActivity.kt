package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.flo.databinding.ActivityMainBinding
import android.widget.Toast
import com.google.gson.Gson

private const val TAG_HOME = "home_fragment"
private const val TAG_AROUND = "around_fragment"
private const val TAG_SEARCH = "search_fragment"
private const val TAG_MYPAGE = "mypage_fragment"

private const val KEY_TITLE="title"
private const val KEY_SINGER="singer"
private const val KEY_PLAY="play"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var songTitle: String = "제목"
    private var songSinger: String = "가수"
    private var isPlaying: Boolean = false
    private var song : Song = Song()
    private var gson : Gson = Gson()

    // ActivityResultLauncher 선언
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.aroundFragment -> setFragment(TAG_AROUND, AroundFragment())
                R.id.searchFragment -> setFragment(TAG_SEARCH, SearchFragment())
                R.id.mypageFragment -> setFragment(TAG_MYPAGE, MypageFragment())
            }
            true
        }

        // ActivityResultLauncher 초기화
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                songTitle = data?.getStringExtra(KEY_TITLE)?: songTitle
                songSinger = data?.getStringExtra(KEY_SINGER)?: songSinger
                isPlaying = data?.getBooleanExtra(KEY_PLAY, isPlaying)?: isPlaying

                // 받아온 데이터 처리
                binding.miniPlayer.findViewById<TextView>(R.id.bottomnav_title_tv).text = songTitle
                binding.miniPlayer.findViewById<TextView>(R.id.bottomnav_singer_tv).text = songSinger

                checkPlayingState()
                Toast.makeText(this, "SongActivity에서 받은 제목: $songTitle, 가수: $songSinger ", Toast.LENGTH_SHORT).show()
            }
        }

        var playStop=binding.playStop
        playStop.setOnClickListener {
            isPlaying=!isPlaying
            if(isPlaying)
                playStop.setImageResource(R.drawable.ic_stop)
            else
                playStop.setImageResource(R.drawable.ic_play)
        }

        // miniPlayer 클릭 시 SongActivity 호출
        binding.miniPlayer.setOnClickListener {
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra(KEY_TITLE, song.title)
            intent.putExtra(KEY_SINGER, song.singer)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)
            resultLauncher.launch(intent) // SongActivity 시작
        }
    }

    private fun setMiniPlayer(song: Song) {
        binding.bottomnavTitleTv.text = song.title
        binding.bottomnavSingerTv.text = song.singer
        binding.mainStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.mainEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.mainProgressSb.progress = (song.second * 100000)/song.playTime
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences=getSharedPreferences("song", MODE_PRIVATE)
        val songJson = sharedPreferences.getString("songData", null)

        song=if(songJson==null){
            Song("Hypeboy", "뉴진스", 0, 60, false, "music_hypeboy")
        }
        else{
            gson.fromJson(songJson, Song::class.java)
        }

        setMiniPlayer(song)
    }

    /*
    // onNewIntent에서 intent 데이터 갱신
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent // 새 Intent로 갱신
        handleIntentData() // 새로운 데이터를 처리하는 함수 호출
    }*/

    private fun checkPlayingState(){
        if(isPlaying)
        {
            binding.playStop.setImageResource(R.drawable.ic_play)
            isPlaying=false
        }
        else{
            binding.playStop.setImageResource(R.drawable.ic_stop)
            isPlaying=true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {

        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()
        var selectedFragment : Fragment? = manager.findFragmentByTag(tag)

        val home = manager.findFragmentByTag(TAG_HOME)
        val around = manager.findFragmentByTag(TAG_AROUND)
        val search = manager.findFragmentByTag(TAG_SEARCH)
        val mypage = manager.findFragmentByTag(TAG_MYPAGE)

        home?.let { fragTransaction.hide(it) }
        around?.let { fragTransaction.hide(it) }
        search?.let { fragTransaction.hide(it) }
        mypage?.let { fragTransaction.hide(it) }

        if (selectedFragment == null) {
            fragTransaction.add(R.id.mainFrame, fragment, tag)
        } else {
            fragTransaction.show(selectedFragment)
        }

        fragTransaction.commit()
    }

}