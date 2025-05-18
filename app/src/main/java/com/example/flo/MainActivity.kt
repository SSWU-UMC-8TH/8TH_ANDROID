package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG_HOME = "home_fragment"
        private const val TAG_AROUND = "around_fragment"
        private const val TAG_SEARCH = "search_fragment"
        private const val TAG_MYPAGE = "mypage_fragment"

        private const val KEY_TITLE="title"
        private const val KEY_SINGER="singer"
        private const val KEY_PLAY="play"
        private const val KEY_SECOND="second"
        private const val KEY_PLAYTIME="playTime"
        private const val KEY_MUSIC="music"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var song = Song("제목", "가수", 0, 60, false, "music_hypeboy")
    private var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySong()
        initFragment()
        initBottomNavigation()
        initMiniPlayer()
        initPlayStopButton()
        initActivityResultLauncher()
    }

    override fun onStart() {
        super.onStart()
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
//        val songJson = sharedPreferences.getString("songData", null)
//        song = songJson?.let { gson.fromJson(it, Song::class.java) } ?: Song("Hypeboy", "뉴진스", 0, 180, false, "music_hypeboy")
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        val songDB = SongDatabase.getInstance(this)!!
        song = if(songId == 0) {
            songDB.songDao().getSong(1)
        }else {
            songDB.songDao().getSong(songId)
        }

        Log.d("song id", song.id.toString())

        updateMiniPlayer()
    }

    private fun initFragment() {
        setFragment(TAG_HOME, HomeFragment())
    }

    private fun initBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.aroundFragment -> setFragment(TAG_AROUND, AroundFragment())
                R.id.searchFragment -> setFragment(TAG_SEARCH, SearchFragment())
                R.id.mypageFragment -> setFragment(TAG_MYPAGE, MypageFragment())
            }
            true
        }
    }

    private fun initMiniPlayer() {
        binding.miniPlayer.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initPlayStopButton() {
        binding.playStop.setOnClickListener {
            song.isPlaying = !song.isPlaying
            updatePlayStopIcon()
        }
    }

    private fun initActivityResultLauncher() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    song = Song(
                        title = data.getStringExtra(KEY_TITLE) ?: song.title,
                        singer = data.getStringExtra(KEY_SINGER) ?: song.singer,
                        isPlaying = data.getBooleanExtra(KEY_PLAY, song.isPlaying),
                        second = data.getIntExtra(KEY_SECOND, song.second),
                        playTime = data.getIntExtra(KEY_PLAYTIME, song.playTime),
                        music = data.getStringExtra(KEY_MUSIC) ?: song.music
                    )
                    updateMiniPlayer()
                    updatePlayStopIcon()
                    showToast("SongActivity에서 받은 제목: ${song.title}, 가수: ${song.singer}")
                }
            }
        }
    }

    private fun updateMiniPlayer() {
        binding.bottomnavTitleTv.text = song.title
        binding.bottomnavSingerTv.text = song.singer
        binding.mainStartTimeTv.text = formatTime(song.second)
        binding.mainEndTimeTv.text = formatTime(song.playTime)
        binding.mainProgressSb.progress = (song.second * 100000) / song.playTime
    }

    private fun updatePlayStopIcon() {
        val icon = if (song.isPlaying) R.drawable.ic_stop else R.drawable.ic_play
        binding.playStop.setImageResource(icon)
    }

    private fun formatTime(seconds: Int): String = String.format("%02d:%02d", seconds / 60, seconds % 60)

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val transaction = manager.beginTransaction()

        listOf(TAG_HOME, TAG_AROUND, TAG_SEARCH, TAG_MYPAGE).forEach { tagName ->
            manager.findFragmentByTag(tagName)?.let { transaction.hide(it) }
        }

        val selectedFragment = manager.findFragmentByTag(tag)
        if (selectedFragment == null) {
            transaction.add(R.id.mainFrame, fragment, tag)
        } else {
            transaction.show(selectedFragment)
        }
        transaction.commit()
    }

    private fun inputDummySong(){
        val songDB = SongDatabase.getInstance(this)!!

        val songs = songDB.songDao().getSongs()

        if(songs.isNotEmpty()) return

        songDB.songDao().insert(Song("Weekend", "태연", 0, 20, false, "music_weekend", R.drawable.img_album_exp6))
        songDB.songDao().insert(Song("Lilac", "아이유(IU)", 0, 20, false, "music_lilac", R.drawable.img_album_exp2))
        songDB.songDao().insert(Song("Next Level", "에스파(AESPA)", 0, 20, false, "music_next", R.drawable.img_album_exp3))
        songDB.songDao().insert(Song("Boy with Luv", "방탄소년단", 0, 20, false, "music_boy", R.drawable.img_album_exp4))
        songDB.songDao().insert(Song("BBoom BBoom", "모모랜드", 0, 20, false, "music_bboom", R.drawable.img_album_exp5))
        songDB.songDao().insert(Song("Butter", "방탄소년단", 0, 20, false, "music_butter", R.drawable.img_album_exp))

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }
}