package com.example.flo.ui.song

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flo.utils.MusicService
import com.example.flo.R
import com.example.flo.utils.db.dao.SongDaoFire
import com.example.flo.utils.db.SongDatabase
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivitySongBinding

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
    private lateinit var timer : Timer
    private var isPlaying = false
    private var isShuffle = true

    private var second : Int = 0
    private var mills : Float = 0f
    //private var mediaPlayer : MediaPlayer? = null

    private val songs=arrayListOf<Song>()
    private lateinit var songDB: SongDatabase
    private var nowPos = 0

    private var musicService: MusicService? = null
    private var isBound = false

    // Firebase 관련 변수
    val daoFire = SongDaoFire()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val musicBinder = binder as MusicService.MusicBinder
            musicService = musicBinder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
        initPlayList()
        initSong()
        initClickListener()

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
        setPlayerStatus(STOP)
        toMainActivity()
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
    }

    // 음악 데이터베이스 초기화
    private fun initPlayList(){
        songDB = SongDatabase.Companion.getInstance(this)
        songs.addAll(songDB.songDao().getSongs())
    }

    // 클릭 이벤트 할당 함수
    private fun initClickListener(){
        // 뒤로가기 버튼 클릭 이벤트 처리
        binding.songDownIb.setOnClickListener {
            finish() // SongActivity 종료
        }

        // 플레이 버튼 클릭 이벤트 처리
        binding.songMiniplayerIv.setOnClickListener {
            setPlayerStatus()
        }

        // 셔플 버튼 클릭 이벤트 처리
        binding.songRandomIv.setOnClickListener {
            if (isShuffle) {
                binding.songRandomIv.setColorFilter(ContextCompat.getColor(this, R.color.gray))
            } else {
                binding.songRandomIv.setColorFilter(ContextCompat.getColor(this, R.color.black))
            }
            isShuffle = !isShuffle
        }

        // 반복 버튼 클릭 이벤트 처리
        binding.songRepeatIv.setOnClickListener {
            second=0
            mills=0f
            binding.songStartTimeTv.text = String.format("00:00")
            binding.songProgressSb.progress = 0
        }

        // 다음 노래 버튼 클릭 이벤트 처리
        binding.songNextIv.setOnClickListener {
            moveSong(1)
        }

        // 이전 노래 버튼 클릭 이벤트 처리
        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        // 좋아요 버튼 클릭 이벤트 처리
        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }
    }

    // MainActivity에서 전달된 데이터를 직접 가져오기
    private fun  initSong(){
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID",songs[nowPos].id.toString())
        startTimer()
        musicService?.changeSong(this,songs[nowPos])
        setPlayer(songs[nowPos])
    }

    // MainActivity로 데이터 전달
    private fun toMainActivity(){
        songs[nowPos].second=((binding.songProgressSb.progress * songs[nowPos].playTime)/100)/1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt("songId", songs[nowPos].id)
        editor.apply()
    }

    // 좋아요 상태 업데이트
    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike=!isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        if(!isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            Toast.makeText(this, "좋아요", Toast.LENGTH_SHORT).show()
            //daoFire.add(songs[nowPos])?:Log.d("add","fail")
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            Toast.makeText(this, "좋아요 취소", Toast.LENGTH_SHORT).show()
        }
    }

    // 이전 혹은 다음 음악 재생
    private fun moveSong(direct: Int){
        if(nowPos+direct<0){
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos+direct>=songs.size){
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos+=direct
        musicService?.pauseMusic()
        musicService?.changeSong(this, songs[nowPos])

        timer.interrupt()
        startTimer()

        setPlayer(songs[nowPos])
    }

    // 현재 재생 음악의 순서값
    private fun getPlayingSongPosition(songId: Int):Int {
        for(i in 0 until songs.size){
            if(songs[i].id==songId){
                return i
            }
        }

        return 0
    }

    // UI 및 재생 상태 업데이트
    private fun setPlayer(song : Song){
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

        if(song.isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        setPlayerStatus(isPlaying)
    }

    //노래 재생 or 정지
    private fun checkPlayingState() {

        if (isPlaying) {
            binding.songMiniplayerIv.setImageResource(R.drawable.ic_stop)
            musicService?.playMusic()
        } else {
            binding.songMiniplayerIv.setImageResource(R.drawable.ic_play)
            musicService?.pauseMusic()
        }
    }

    // 노래 재생 상태 설정
    private fun setPlayerStatus(playingState : Boolean = !isPlaying){

        isPlaying = playingState
        songs[nowPos].isPlaying = playingState
        timer.isPlaying = playingState

        checkPlayingState()
    }

    private fun startTimer(){
        timer = Timer(songs[nowPos].playTime,songs[nowPos].isPlaying)
        timer.start()
    }

    // 음악의 시간 진행 처리 스레드
    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {

        override fun run(){
            super.run()
            second=0
            mills=0f
            while (true){

                if(second>=playTime){
                    break
                }
                if(isPlaying){
                    try {
                        sleep(50)
                    }catch (e: InterruptedException){
                        return
                    }
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