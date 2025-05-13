package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

private const val KEY_TITLE="title"
private const val KEY_SINGER="singer"
private const val KEY_PLAY="play"
private const val KEY_SECOND="second"
private const val KEY_PLAYTIME="playTime"
private const val KEY_MUSIC="music"

private const val START = true
private const val STOP = false

class SongActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySongBinding
    private lateinit var song : Song
    private lateinit var timer : Timer
    private var isPlaying = false
    private var isShuffle = true
    private var isRepeat = true
    private var songNext = true

    private var second : Int = 0
    private var mills : Float = 0f
    private var mediaPlayer : MediaPlayer? = null
    private var gson : Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        setPlayer(song)

        // 뒤로가기 버튼 클릭 이벤트 처리
        binding.backIcon.setOnClickListener {
            changePlayingState(STOP)
            val resultIntent = Intent().apply {
                putExtra(KEY_TITLE, binding.bottomnavTitleTv.text.toString())
                putExtra(KEY_SINGER, binding.bottomnavSingerTv.text.toString())
                putExtra(KEY_PLAY, !isPlaying)
                putExtra(KEY_SECOND, song.second)
                putExtra(KEY_PLAYTIME, song.playTime)
                putExtra(KEY_MUSIC, song.music)
            }
            setResult(RESULT_OK, resultIntent) // 결과 전달
            finish() // SongActivity 종료
        }

        // 플레이 버튼 클릭 이벤트 처리
        var playStop = binding.playStop
        playStop.setOnClickListener {
            changePlayingState()
        }

        // 셔플 버튼 클릭 이벤트 처리
        binding.icShuffle.setOnClickListener {
            if (isShuffle) {
                binding.icShuffle.setColorFilter(ContextCompat.getColor(this, R.color.gray))
            } else {
                binding.icShuffle.setColorFilter(ContextCompat.getColor(this, R.color.black))
            }
            isShuffle = !isShuffle
        }

        // 반복 버튼 클릭 이벤트 처리
        binding.icRepeat.setOnClickListener {
            second=0
            mills=0f
            binding.songStartTimeTv.text = String.format("00:00")
            binding.songProgressSb.progress = 0
        }

        // 다음 노래 버튼 클릭 이벤트 처리
        binding.nextSong.setOnClickListener {
            if (songNext) {
                binding.bottomnavTitleTv.text = "지민"
                binding.bottomnavSingerTv.text = "지민"
                songNext = false
            } else {
                binding.bottomnavTitleTv.text = "성민"
                binding.bottomnavSingerTv.text = "성민"
                songNext = true
            }
        }

        // 이전 노래 버튼 클릭 이벤트 처리
        binding.preSong.setOnClickListener {
            if (songNext) {
                binding.bottomnavTitleTv.text = "지민"
                binding.bottomnavSingerTv.text = "지민"
            } else {
                binding.bottomnavTitleTv.text = "성민"
                binding.bottomnavSingerTv.text = "성민"
            }
            songNext = !songNext
        }
    }

    override fun onPause() {
        super.onPause()
        changePlayingState(STOP)
        song.second=((binding.songProgressSb.progress * song.playTime)/100)/1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // MainActivity에서 전달된 데이터를 직접 가져오기
    private fun initSong(){
        val receivedIntent = intent
        if(receivedIntent.hasExtra(KEY_TITLE) && receivedIntent.hasExtra(KEY_SINGER)){
            song=Song(
                receivedIntent.getStringExtra(KEY_TITLE)?: "제목",
                receivedIntent.getStringExtra(KEY_SINGER)?: "가수",
                receivedIntent.getIntExtra(KEY_SECOND, 0),
                receivedIntent.getIntExtra(KEY_PLAYTIME, 40),
                receivedIntent.getBooleanExtra(KEY_PLAY, false),
                receivedIntent.getStringExtra(KEY_MUSIC)?: "music"
            )
        }
        startTimer()
    }

    // UI 및 재생 상태 업데이트
    private fun setPlayer(song : Song){
        binding.bottomnavTitleTv.text = song.title
        binding.bottomnavSingerTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        changePlayingState(song.isPlaying)
    }

    //노래 재생 or 정지
    private fun checkPlayingState() {

        if (isPlaying) {
            binding.playStop.setImageResource(R.drawable.ic_stop)
            if(mediaPlayer?.isPlaying==false) {
                mediaPlayer?.start()
            }
        } else {
            binding.playStop.setImageResource(R.drawable.ic_play)
            if(mediaPlayer?.isPlaying==true){
                mediaPlayer?.pause()
            }
        }
    }

    // 노래 재생 상태 변경
    private fun changePlayingState(playingState : Boolean = !isPlaying){

        isPlaying = playingState
        song.isPlaying = playingState
        timer.isPlaying = playingState

        checkPlayingState()
    }

    private fun startTimer(){
        timer = Timer(song.playTime,song.isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {

        override fun run(){
            super.run()
            while (true){

                if(second>=playTime){
                    break
                }
                if(isPlaying){
                    sleep(50)
                    mills+=50

                    runOnUiThread {
                        binding.songProgressSb.progress = ((mills/playTime)*100).toInt()
                    }

                    if(mills%1000==0f){
                        runOnUiThread {
                            binding.songStartTimeTv.text = String.format("%02d:%02d", second/60, second%60)
                        }
                        second++
                    }
                }
            }
        }
    }
}
