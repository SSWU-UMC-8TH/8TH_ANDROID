package com.example.flo.utils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.flo.R
import com.example.flo.data.entities.Song

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    var isPlaying = false

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicBinder()
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.music_hypeboy)
    }

    fun playMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    fun changeSong(context:Context, song: Song) {
        mediaPlayer.reset()  // 기존 곡 초기화

        val musicResId = context.resources.getIdentifier(song.music, "raw", context.packageName)
        // 새로 리소스를 지정해서 초기화
        mediaPlayer = MediaPlayer.create(context, musicResId)
        pauseMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
