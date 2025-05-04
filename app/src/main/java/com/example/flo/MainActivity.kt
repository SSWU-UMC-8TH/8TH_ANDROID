package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var song:Song = Song()
    private var gson: Gson = Gson()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var updateSeekBarThread: Thread? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FLO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        initBottomNavigation()

        binding.mainPlayerCl.setOnClickListener {
            val intent = Intent(this,SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer",song.singer)
            intent.putExtra("second",song.second)
            intent.putExtra("playTime",song.playTime)
            intent.putExtra("isPlaying",song.isPlaying)
            intent.putExtra("music", song.music)
            intent.putExtra("coverImg", song.coverImg)
            startActivity(intent)
        }

        binding.mainMiniplayerBtn.setOnClickListener {
            if (mediaPlayer != null && !isPlaying) {
                mediaPlayer?.start()
                isPlaying = true
                togglePlayPauseButtons(true)
            }
        }

        binding.mainPauseBtn.setOnClickListener {
            if (mediaPlayer != null && isPlaying) {
                mediaPlayer?.pause()
                isPlaying = false
                togglePlayPauseButtons(false)
            }
        }

    }

    private fun togglePlayPauseButtons(isPlaying: Boolean) {
        if (isPlaying) {
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
        } else {
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
        }
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun setMiniPlayer(song: Song){
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainMiniplayerProgressSb.progress = (song.second*100000)/song.playTime
    }

    override fun onStart(){
        super.onStart()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songJson = sharedPreferences.getString("songData", null)

        song = if(songJson == null){
            Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")

        }   else {
            gson.fromJson(songJson, Song::class.java)
        }
        setMiniPlayer(song)
    }

    fun updateMiniPlayerWithSong(song: Song) {
        this.song = song  // 내부 상태 업데이트
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainMiniplayerProgressSb.progress = (song.second*100000)/song.playTime
        playMusic(song.music)
        togglePlayPauseButtons(true)
        isPlaying = true
    }

    private fun playMusic(fileName: String) {
        updateSeekBarThread?.interrupt()
        updateSeekBarThread = null
        // 이전에 재생 중이던 플레이어 정리
        mediaPlayer?.release()
        mediaPlayer = null

        // 새로운 MediaPlayer 생성
        val resId = resources.getIdentifier(fileName, "raw", packageName)
        mediaPlayer = MediaPlayer.create(this, resId)

        mediaPlayer?.start()
        startSeekBarUpdate()
    }

    private fun startSeekBarUpdate() {
        updateSeekBarThread?.interrupt()
        updateSeekBarThread = object : Thread() {
            override fun run() {
                try {
                    while (mediaPlayer != null && isPlaying) {
                        sleep(50)  // 더 자연스럽고, CPU 낭비 적음

                        val currentMs = mediaPlayer?.currentPosition ?: 0
                        val playTimeMs = song.playTime * 1000  // 60초 → 60000ms

                        if (currentMs <= playTimeMs) {
                            val progress = (currentMs.toFloat() / playTimeMs * 100000).toInt()
                            runOnUiThread {
                                binding.mainMiniplayerProgressSb.progress = progress
                            }
                        }
                    }
                } catch (e: InterruptedException) {
                    // 쓰레드가 안전하게 종료됨
                }
            }
        }
        updateSeekBarThread?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
        updateSeekBarThread?.interrupt()
    }
}