package com.example.flo

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding


class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songMusicTitleTv.text = intent.getStringExtra("title")
            binding.songSingerNameTv.text = intent.getStringExtra("singer")
        }

        binding.songDownIb.setOnClickListener {
            finish()
        }
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songRepeatIv.setOnClickListener {
            setRepeatStatus(false)
        }

        binding.songRepeatActiveIv.setOnClickListener {
            setRepeatStatus(true)
        }


        binding.songRandomIv.setOnClickListener {
            setRandomStatus(false)
        }

        binding.songRandomActiveIv.setOnClickListener {
            setRandomStatus(true)
        }

    }
    fun setPlayerStatus (isPlaying : Boolean){
        if(isPlaying){
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
        } else {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
        }
    }

    fun setRepeatStatus (isRandom : Boolean){
        if(isRandom){
            binding.songRepeatIv.visibility = View.VISIBLE
            binding.songRepeatActiveIv.visibility = View.GONE
        } else {
            binding.songRepeatIv.visibility = View.GONE
            binding.songRepeatActiveIv.visibility = View.VISIBLE
        }
    }

    fun setRandomStatus (isRandom : Boolean){
        if(isRandom){
            binding.songRandomIv.visibility = View.VISIBLE
            binding.songRandomActiveIv.visibility = View.GONE
        } else {
            binding.songRandomIv.visibility = View.GONE
            binding.songRandomActiveIv.visibility = View.VISIBLE
        }
    }

}