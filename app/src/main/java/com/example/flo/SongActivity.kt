package com.example.flo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flo.databinding.ActivitySongBinding

private const val KEY_TITLE="title"
private const val KEY_SINGER="singer"
private const val KEY_PLAY="play"

class SongActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySongBinding
    private lateinit var song : Song
    private lateinit var timer : Timer
    private var isPlaying = false
    private var isShuffle = true
    private var isRepeat = true
    private var songNext = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        setPlayer(song)

        // 뒤로가기 버튼 클릭 이벤트 처리
        binding.backIcon.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(KEY_TITLE, binding.titleText.text.toString())
                putExtra(KEY_SINGER, binding.singerText.text.toString())
                putExtra(KEY_PLAY, !isPlaying)
            }
            setResult(RESULT_OK, resultIntent) // 결과 전달
            finish() // SongActivity 종료
        }

        // 플레이 버튼 클릭 이벤트 처리
        var playStop = binding.playStop
        playStop.setOnClickListener {
            checkPlayingState()
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
            if (isRepeat) {
                binding.icRepeat.setColorFilter(ContextCompat.getColor(this, R.color.gray))
            } else {
                binding.icRepeat.setColorFilter(ContextCompat.getColor(this, R.color.black))
            }
            isRepeat = !isRepeat
        }

        // 다음 노래 버튼 클릭 이벤트 처리
        binding.nextSong.setOnClickListener {
            if (songNext) {
                binding.titleText.text = "지민"
                binding.singerText.text = "지민"
                songNext = false
            } else {
                binding.titleText.text = "성민"
                binding.singerText.text = "성민"
                songNext = true
            }
        }

        // 이전 노래 버튼 클릭 이벤트 처리
        binding.preSong.setOnClickListener {
            if (songNext) {
                binding.titleText.text = "지민"
                binding.singerText.text = "지민"
            } else {
                binding.titleText.text = "성민"
                binding.singerText.text = "성민"
            }
            songNext = !songNext
        }
    }

    // MainActivity에서 전달된 데이터를 직접 가져오기
    private fun initSong(){
        val receivedIntent = intent
        if(receivedIntent.hasExtra(KEY_TITLE) && receivedIntent.hasExtra(KEY_SINGER)){
            song=Song(
                receivedIntent.getStringExtra(KEY_TITLE)?: "제목",
                receivedIntent.getStringExtra(KEY_SINGER)?: "가수",
                receivedIntent.getIntExtra("second", 0),
                receivedIntent.getIntExtra("playTime", 0),
                receivedIntent.getBooleanExtra("isPlaying", false)
            )
        }
        startTimer()
    }

    // UI 업데이트
    private fun setPlayer(song : Song){
        binding.titleText.text = song.title
        binding.singerText.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)
        checkPlayingState()
    }

    // 노래 재생 상태 변경
    private fun checkPlayingState() {

        if (isPlaying) {
            binding.playStop.setImageResource(R.drawable.ic_play)
        } else {
            binding.playStop.setImageResource(R.drawable.ic_stop)
        }
        isPlaying = !isPlaying
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying
    }

    private fun startTimer(){
        timer = Timer(song.playTime,song.isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var second : Int = 0
        private var mills : Float = 0f

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
