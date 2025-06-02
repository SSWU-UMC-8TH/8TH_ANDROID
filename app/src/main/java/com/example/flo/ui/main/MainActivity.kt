package com.example.flo.ui.main

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.flo.utils.MusicService
import com.example.flo.R
import com.example.flo.ui.song.SongActivity
import com.example.flo.utils.db.SongDatabase
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivityMainBinding
import com.example.flo.ui.main.around.AroundFragment
import com.example.flo.ui.main.home.HomeFragment
import com.example.flo.ui.main.mypage.MypageFragment
import com.example.flo.ui.main.search.SearchFragment

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG_HOME = "home_fragment"
        private const val TAG_AROUND = "around_fragment"
        private const val TAG_SEARCH = "search_fragment"
        private const val TAG_MYPAGE = "mypage_fragment"

        private const val KEY_TITLE="title"
        private const val KEY_SINGER="singer"
        private const val KEY_PLAY="play"
        private const val KEY_SECOND="second"
        private const val KEY_PLAYTIME="playTime"
        private const val KEY_MUSIC="music"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    var songs=arrayListOf<Song>()
    var nowPos = 0
    lateinit var songDB: SongDatabase

    private val albums=arrayListOf<Album>()
    private var nowAlbum = 0
    private lateinit var albumDB: SongDatabase
    var isPlaying = true

    var musicService: MusicService? = null
    private var isBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val musicBinder = binder as MusicService.MusicBinder
            musicService = musicBinder.getService()
            isBound = true
            if(musicService?.let{
                    it.changeSong(this@MainActivity, songs[nowPos])
                    Log.d("now Song",songs[nowPos].toString())
                }==null) Log.d("musicService", "null")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        inputDummySong()                // 음악 데이터베이스 초기화
        setPlayList()
        inputDummyAlbum()               // 앨범 데이터베이스 초기화
        initFragment()                  // 프라그먼트 초기화
        initBottomNavigation()          // 네비게이션 바 초기화
        initClickListener()             // 클릭 이벤트 할당

        Log.d("MAIN/JWT_TO_SERVER", getJwt().toString())
    }

    override fun onStart() {
        super.onStart()



        initSong() // SongActivity에서 전달된 데이터를 직접 가져오기
        setPlayer(songs[nowPos])
//        setPlayerStatus(false)

        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)

        }

//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
//        val songJson = sharedPreferences.getString("songData", null)
//        song = songJson?.let { gson.fromJson(it, Song::class.java) } ?: Song("Hypeboy", "뉴진스", 0, 180, false, "music_hypeboy")
    }

    override fun onPause() {
        super.onPause()
        toSongActivity() // SongActivity로 데이터 전달
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    private fun getJwt(): String?{
        val spf = getSharedPreferences("auth2", MODE_PRIVATE)
        return spf.getString("jwt","")
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

    // SongActivity에서 전달된 데이터를 직접 가져오기
    private fun initSong(){
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        nowPos = getPlayingSongPosition(songId)
    }

    // SongActivity로 데이터 전달
    private fun toSongActivity(){
        songs[nowPos].second=((binding.mainProgressSb.progress * songs[nowPos].playTime)/100)/1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt("songId", songs[nowPos].id)
        editor.apply()
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
        setPlayer(songs[nowPos])
        setPlayerStatus(isPlaying)
    }

    // 클릭 이벤트 할당 함수
    private fun initClickListener(){
        binding.mainPreBtn.setOnClickListener {
            moveSong(-1)
        }

        binding.mainNextBtn.setOnClickListener {
            moveSong(1)
        }

        binding.mainMiniplayerBtn.setOnClickListener {
            setPlayerStatus()
        }

        binding.miniPlayer.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

    }

    // 재생할 음악 리스트 초기화
    private fun setPlayList(){
        songDB = SongDatabase.Companion.getInstance(this)
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initFragment() {
        setFragment(TAG_HOME, HomeFragment())
    }

    private fun initBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.aroundFragment -> setFragment(TAG_AROUND, AroundFragment())
                R.id.searchFragment -> setFragment(TAG_SEARCH, SearchFragment())
                R.id.mypageFragment -> setFragment(TAG_MYPAGE, MypageFragment())
            }
            true
        }
    }

    private fun initActivityResultLauncher() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    songs[nowPos] = Song(
                        title = data.getStringExtra(KEY_TITLE) ?: songs[nowPos].title,
                        singer = data.getStringExtra(KEY_SINGER) ?: songs[nowPos].singer,
                        isPlaying = data.getBooleanExtra(KEY_PLAY, songs[nowPos].isPlaying),
                        second = data.getIntExtra(KEY_SECOND, songs[nowPos].second),
                        playTime = data.getIntExtra(KEY_PLAYTIME, songs[nowPos].playTime),
                        music = data.getStringExtra(KEY_MUSIC) ?: songs[nowPos].music
                    )
                    setPlayerStatus()
                    showToast("SongActivity에서 받은 제목: ${songs[nowPos].title}, 가수: ${songs[nowPos].singer}")
                }
            }
        }
    }

    fun setPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainStartTimeTv.text = formatTime(song.second)
        binding.mainEndTimeTv.text = formatTime(song.playTime)
        binding.mainProgressSb.progress = (song.second * 100000) / song.playTime
    }

    fun setPlayerStatus(isPlaying: Boolean = !this.isPlaying) {
        val icon: Int
        if (isPlaying) {
            musicService?.playMusic()
            icon= R.drawable.ic_stop
        }else {
            musicService?.pauseMusic()
            icon= R.drawable.ic_play
        }
        this.isPlaying = isPlaying
        songs[nowPos].isPlaying = isPlaying
        binding.mainMiniplayerBtn.setImageResource(icon)
    }

    private fun formatTime(seconds: Int): String = String.format("%02d:%02d", seconds / 60, seconds % 60)

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val transaction = manager.beginTransaction()

        listOf(TAG_HOME, TAG_AROUND, TAG_SEARCH, TAG_MYPAGE).forEach { tagName ->
            manager.findFragmentByTag(tagName)?.let { transaction.hide(it) }
        }

        val selectedFragment = manager.findFragmentByTag(tag)
        if (selectedFragment == null) {
            transaction.add(R.id.mainFrame, fragment, tag)
        } else {
            transaction.show(selectedFragment)
        }
        transaction.commit()
    }

    // 음악 데이터베이스 초기화
    private fun inputDummySong(){

        val songDB = SongDatabase.Companion.getInstance(this)

        songDB.songDao().deleteAll()
        songDB.songDao().resetAutoIncrement()

        val songs = songDB.songDao().getSongs()

        if(songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Weekend",
                "태연",
                0,
                20,
                false,
                "music_weekend",
                R.drawable.img_album_exp6,
                6
            )
        )
        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유(IU)",
                0,
                20,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                2
            )
        )
        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파(AESPA)",
                0,
                20,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                3
            )
        )
        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "방탄소년단",
                0,
                20,
                false,
                "music_boy",
                R.drawable.img_album_exp4,
                4
            )
        )
        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드",
                0,
                20,
                false,
                "music_bboom",
                R.drawable.img_album_exp5,
                5
            )
        )
        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단",
                0,
                20,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                4
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("songDB data", _songs.toString())
    }

    // 앨범 데이터베이스 초기화
    private fun inputDummyAlbum(){
        val albumDB = SongDatabase.Companion.getInstance(this)

        albumDB.albumDao().deleteAll()

        val albums = albumDB.albumDao().getAlbums()

        if(albums.isNotEmpty()) return

        albumDB.albumDao().insert(Album(1, "Butter", "BTS", R.drawable.img_album_exp))
        albumDB.albumDao().insert(Album(2, "Lilac", "아이유(IU)", R.drawable.img_album_exp2))
        albumDB.albumDao().insert(Album(3, "Next Level", "에스파(AESPA)", R.drawable.img_album_exp3))
        albumDB.albumDao().insert(Album(4, "Boy with Luv", "BTS", R.drawable.img_album_exp4))
        albumDB.albumDao().insert(Album(5, "BBoom BBoom", "모모랜드", R.drawable.img_album_exp5))
        albumDB.albumDao().insert(Album(6, "Weekend", "태연", R.drawable.img_album_exp6))
        albumDB.albumDao().insert(Album(7, "Modal Soul", "Nujabes", R.drawable.img_modal_soul))
        albumDB.albumDao().insert(Album(8, "Lifes Like", "Jazzyfact", R.drawable.img_lifes_like))
        albumDB.albumDao().insert(Album(9, "WW3", "YE", R.drawable.img_ww3))
        albumDB.albumDao().insert(
            Album(
                10,
                "I am Music",
                "Playboy Carti",
                R.drawable.img_i_am_music
            )
        )

        val _albums = albumDB.albumDao().getAlbums()
        Log.d("albumDB data", _albums.toString())
    }
}