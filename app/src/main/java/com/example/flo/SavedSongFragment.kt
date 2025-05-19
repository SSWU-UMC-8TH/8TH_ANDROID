package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLockerSavedsongBinding

class SavedSongFragment : Fragment() {
    private lateinit var binding: FragmentLockerSavedsongBinding
    private lateinit var songDB: SongDatabase
    private lateinit var songRVAdapter: SavedSongRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)
        songDB = SongDatabase.getInstance(requireContext())!!
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview(){
        binding.lockerSavedSongRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        songRVAdapter = SavedSongRVAdapter()
        binding.lockerSavedSongRecyclerView.adapter = songRVAdapter

        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener{
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false,songId)
            }
        })

        songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)
    }

    fun selectAllSongs(select: Boolean) {
        songRVAdapter.selectAll(select)
    }

    fun deleteSelectedSongs() {
        val selectedSongs = songRVAdapter.getSelectedSongs()
        for (song in selectedSongs) {
            songDB.songDao().updateIsLikeById(false, song.id)
        }
        songRVAdapter.removeSelectedSongs()
    }
}