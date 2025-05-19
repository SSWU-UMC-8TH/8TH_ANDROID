package com.example.flo

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemMypageBinding

class MypageRVAdapter(): RecyclerView.Adapter<MypageRVAdapter.ViewHolder>() {

    val songs = ArrayList<Song>()
    private var isClicked :ArrayList<Boolean> = ArrayList<Boolean>()

    interface MyItemClickListener{
        fun onRemoveSong(songId: Int)
    }
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMypageBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        for(i in 0 until songs.size){
            isClicked.add(false)
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songs[position])

        holder.binding.itemSongSwitch.isChecked = isClicked[position]

        holder.binding.itemSongSwitch.setOnClickListener {isClicked[position] = !isClicked[position]}

        holder.binding.itemSongMoreIv.setOnClickListener {
            removeSong(position)
        }
    }

    override fun getItemCount(): Int = songs.size

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>){
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSong(position: Int){
        Log.d("delete song", songs[position].toString())
        mItemClickListener.onRemoveSong(songs[position].id)
        songs.removeAt(position)
        isClicked.removeAt(position)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemMypageBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(song: Song){
            binding.itemSongTitleTv.text = song.title
            binding.itemSongSingerTv .text = song.singer
            binding.itemSongImgIv.setImageResource(song.coverImg!!)
        }
    }
}