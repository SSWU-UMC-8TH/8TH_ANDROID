package com.example.flo.ui.song

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.R
import com.example.flo.data.entities.Song
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson


class SongActivity : AppCompatActivity() {
    lateinit var binding: ActivitySongBinding
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()

        // 알림
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

    }


    override fun onPause() {
        super.onPause()

        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime)/100)/1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터

        editor.putInt("songId",songs[nowPos].id)

        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initClickListener(){
        binding.songDownIb.setOnClickListener {
            finish()
        }

        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }

        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }

        binding.songRandomIv.setOnClickListener {
            setRandom(songs[nowPos].isRandom)
        }

        binding.songRepeatIv.setOnClickListener {
            setRepeat(songs[nowPos].isRepeat)
            randomPlay()
        }
    }

    private fun randomPlay() {
        binding.songProgressSb.progress = 0                     // SeekBar 맨 앞으로
        binding.songStartTimeTv.text = String.format("%02d:%02d", 0, 0)  // 시간 00:00으로 초기화
        songs[nowPos].second = 0                                         // song 객체 시간 초기화
        songs[nowPos].isPlaying = true                                   // 곡 상태를 재생으로 설정

        mediaPlayer?.seekTo(0)         // 음악 위치 초기화
        mediaPlayer?.start()

        setPlayerStatus(songs[nowPos].isPlaying)                         // 재생 상태 UI 반영 및 MediaPlayer 시작

        if (!timer.isInterrupted) {
            timer.interrupt()
        }

        startTimer()
    }

    private fun initSong(){
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID",songs[nowPos].id.toString())

        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike,songs[nowPos].id)

        if (!isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            CustomSnackbar.make(binding.root, "좋아요를 추가했어요").show()
        } else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            CustomSnackbar.make(binding.root, "좋아요를 취소했어요").show()
        }
    }

    private fun moveSong(direct: Int){
        if (nowPos + direct < 0){
            CustomSnackbar.make(binding.root, "처음 곡입니다.").show()
            return
        }

        if (nowPos + direct >= songs.size){
            CustomSnackbar.make(binding.root, "마지막 곡입니다").show()
            return
        }

        nowPos += direct

        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }

    private fun setRandom(isRandom: Boolean){
        songs[nowPos].isRandom = !isRandom

        if (!isRandom){
            binding.songRandomIv.setImageResource(R.drawable.nugu_btn_random_active)
        } else{
            binding.songRandomIv.setImageResource(R.drawable.nugu_btn_random_inactive)
        }
    }

    private fun setRepeat(isRepeat: Boolean){
        songs[nowPos].isRepeat = !isRepeat

        if (!isRepeat){
            binding.songRepeatIv.setImageResource(R.drawable.nugu_btn_repeat_active)
        } else{
            binding.songRepeatIv.setImageResource(R.drawable.nugu_btn_repeat_inactive)
        }
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song: Song){
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        if (song.isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
        setPlayerStatus(song.isPlaying)

    }

    private fun setPlayerStatus (isPlaying : Boolean){
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
        }
    }

    private fun startTimer(){
        timer = Timer(songs[nowPos].playTime,songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true):Thread(){

        private var second : Int = 0
        private var mills: Float = 0f

        override fun run(){
            super.run()
            try{
                while (true){
                    if (second >= playTime){
                        break
                    }

                    if (isPlaying){
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime)*100).toInt()
                        }

                        if (mills % 1000 == 0f){
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d",second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            }
            catch (e: InterruptedException){
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}