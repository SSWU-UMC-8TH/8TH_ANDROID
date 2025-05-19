package com.example.flo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemMypageBinding

class FirebaseRVAdapter( private val context: Context, private val songs: ArrayList<Song>):
    RecyclerView.Adapter<FirebaseRVAdapter.ViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemMypageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    inner class ViewHolder(val binding: ItemMypageBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(song: Song){
            binding.itemSongTitleTv.text = song.title
            binding.itemSongSingerTv .text = song.singer

            val img = song.coverImg?:R.drawable.album1
            binding.itemSongImgIv.setImageResource(img)
        }
    }
}