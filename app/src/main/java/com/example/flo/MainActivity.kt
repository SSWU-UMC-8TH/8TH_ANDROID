package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
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

        initFragment()
        initBottomNavigation()
        initMiniPlayer()
        initPlayStopButton()
        initActivityResultLauncher()
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
            val intent = Intent(this, SongActivity::class.java).apply {
                putExtra(KEY_TITLE, song.title)
                putExtra(KEY_SINGER, song.singer)
                putExtra(KEY_SECOND, song.second)
                putExtra(KEY_PLAYTIME, song.playTime)
                putExtra(KEY_PLAY, song.isPlaying)
                putExtra(KEY_MUSIC, song.music)
            }
            resultLauncher.launch(intent)
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

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = sharedPreferences.getString("songData", null)
        song = songJson?.let { gson.fromJson(it, Song::class.java) } ?: Song("Hypeboy", "뉴진스", 0, 180, false, "music_hypeboy")
        updateMiniPlayer()
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
}