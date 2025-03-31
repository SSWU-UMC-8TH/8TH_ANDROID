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

class SongActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySongBinding
    private var song=true

    // ActivityResultLauncher 선언
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var songTitle=intent.getStringExtra(KEY_TITLE) ?: binding.titleText.text.toString()
        var songSinger=intent.getStringExtra(KEY_SINGER)?: binding.singerText.text.toString()
        binding.titleText.text=songTitle
        binding.singerText.text=songSinger

        // ActivityResultLauncher 초기화
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                songTitle = data?.getStringExtra(KEY_TITLE)?:songTitle
                songSinger = data?.getStringExtra(KEY_SINGER)?:songSinger

                // 받아온 데이터 처리
                binding.titleText.text = songTitle
                binding.singerText.text = songSinger
            }
        }


        binding.backIcon.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(KEY_TITLE, binding.titleText.text.toString())
                putExtra(KEY_SINGER, binding.singerText.text.toString())
            }
            setResult(RESULT_OK, resultIntent) // 결과 전달
            finish() // SongActivity 종료
        }

        binding.nextSong.setOnClickListener {
            if(song)
            {
                binding.titleText.text="지민"
                binding.singerText.text="지민"
            }
            else{
                binding.titleText.text="성민"
                binding.singerText.text="성민"
            }
            song=!song
        }
    }
}