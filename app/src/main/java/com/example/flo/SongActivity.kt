package com.example.flo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding

private const val KEY_TITLE="title"
private const val KEY_SINGER="singer"
private const val KEY_PLAY="play"

class SongActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySongBinding
    private var isPlaying=true
    private var songNext=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MainActivity에서 전달된 데이터를 직접 가져오기
        val receivedIntent = intent
        val intentTitle = receivedIntent.getStringExtra(KEY_TITLE) ?: "제목"
        val intentSinger = receivedIntent.getStringExtra(KEY_SINGER) ?: "가수"
        isPlaying = receivedIntent.getBooleanExtra(KEY_PLAY, false)

        // UI 업데이트
        binding.titleText.text = intentTitle
        binding.singerText.text = intentSinger
        checkPlayingState()

        var playStop=binding.playStop
        playStop.setOnClickListener {
            if(isPlaying)
                playStop.setImageResource(R.drawable.ic_play)
            else
                playStop.setImageResource(R.drawable.ic_stop)
            isPlaying=!isPlaying
        }

        binding.backIcon.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(KEY_TITLE, binding.titleText.text.toString())
                putExtra(KEY_SINGER, binding.singerText.text.toString())
                putExtra(KEY_PLAY, !isPlaying)
            }
            setResult(RESULT_OK, resultIntent) // 결과 전달
            finish() // SongActivity 종료
        }

        binding.nextSong.setOnClickListener {
            if(songNext)
            {
                binding.titleText.text="지민"
                binding.singerText.text ="지민"
                songNext=false
            }
            else{
                binding.titleText.text="성민"
                binding.singerText.text ="성민"
                songNext=true
            }
        }
    }

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
}