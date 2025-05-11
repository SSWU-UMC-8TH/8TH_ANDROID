package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var songs: List<Song>
    private var nowPos = 0

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

        inputDummySongs()
        initBottomNavigation()

        val songDB = SongDatabase.getInstance(this)!!
        songs = songDB.songDao().getSongs()

        // 다음곡 / 이전곡 버튼
        binding.mainNextBtn.setOnClickListener {
            moveSong(+1)
        }

        binding.mainPreviousBtn.setOnClickListener {
            moveSong(-1)
        }

        // 미니플레이어 클릭 시 전체 플레이어로 이동
        binding.mainPlayerCl.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        // 재생 버튼
        binding.mainMiniplayerBtn.setOnClickListener {
            if (mediaPlayer != null && !isPlaying) {
                mediaPlayer?.start()
                isPlaying = true
                togglePlayPauseButtons(true)
                startSeekBarUpdate()
            }
        }

        // 일시정지 버튼
        binding.mainPauseBtn.setOnClickListener {
            if (mediaPlayer != null && isPlaying) {
                mediaPlayer?.pause()
                isPlaying = false
                togglePlayPauseButtons(false)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        val songDB = SongDatabase.getInstance(this)!!
        songs = songDB.songDao().getSongs()

        nowPos = getPlayingSongPosition(songId)
        setMiniPlayer(songs[nowPos])
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        updateSeekBarThread?.interrupt()
        isPlaying = false
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in songs.indices) {
            if (songs[i].id == songId) return i
        }
        return 0
    }

    private fun moveSong(direct: Int) {
        val newPos = nowPos + direct
        if (newPos < 0 || newPos >= songs.size) {
            Toast.makeText(this, "재생할 곡이 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos = newPos
        val song = songs[nowPos]
        setMiniPlayer(song)

        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", song.id)
        editor.apply()
    }

    private fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainMiniplayerProgressSb.progress = (song.second * 100000) / song.playTime
        prepareMusic(song.music)
        togglePlayPauseButtons(false)
        isPlaying = false
    }

    private fun prepareMusic(fileName: String) {
        updateSeekBarThread?.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null

        val resId = resources.getIdentifier(fileName, "raw", packageName)
        mediaPlayer = MediaPlayer.create(this, resId)
        // 재생은 하지 않음!
    }

    private fun startSeekBarUpdate() {
        updateSeekBarThread = object : Thread() {
            override fun run() {
                try {
                    while (mediaPlayer != null && isPlaying) {
                        sleep(50)
                        val currentMs = mediaPlayer?.currentPosition ?: 0
                        val playTimeMs = songs[nowPos].playTime * 1000
                        if (currentMs <= playTimeMs) {
                            val progress = (currentMs.toFloat() / playTimeMs * 100000).toInt()
                            runOnUiThread {
                                binding.mainMiniplayerProgressSb.progress = progress
                            }
                        }
                    }
                } catch (_: InterruptedException) {
                }
            }
        }
        updateSeekBarThread?.start()
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

    private fun inputDummySongs() {
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()
        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song("Lilac", "아이유 (IU)", 0, 200, false, "music_lilac", R.drawable.img_album_exp2, false, 1)
        )
        songDB.songDao().insert(
            Song("Flu", "아이유 (IU)", 0, 200, false, "music_flu", R.drawable.img_album_exp2, false, 1)
        )
        songDB.songDao().insert(
            Song("Butter", "방탄소년단 (BTS)", 0, 190, false, "music_butter", R.drawable.img_album_exp, false, 2)
        )
        songDB.songDao().insert(
            Song("Next Level", "에스파 (AESPA)", 0, 210, false, "music_next", R.drawable.img_album_exp3, false, 3)
        )
        songDB.songDao().insert(
            Song("Boy with Luv", "방탄소년단 (BTS)", 0, 230, false, "music_boy", R.drawable.img_album_exp4, false, 4)
        )
        songDB.songDao().insert(
            Song("BBoom BBoom", "모모랜드 (MOMOLAND)", 0, 240, false, "music_bboom", R.drawable.img_album_exp5, false, 5)
        )
        val songDBData = songDB.songDao().getSongs()
        Log.d("DB data", songDBData.toString())
    }

    private fun initBottomNavigation() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }
    }

    fun updateMiniPlayerWithSong(song: Song) {
        nowPos = getPlayingSongPosition(song.id)
        setMiniPlayer(song)

        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", song.id)
        editor.apply()
    }
}
