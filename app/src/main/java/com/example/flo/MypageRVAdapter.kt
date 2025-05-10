package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemMypageBinding

class MypageRVAdapter(private val songList: ArrayList<Album>): RecyclerView.Adapter<MypageRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMypageBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songList[position])
        holder.binding.itemSongMoreIv.setOnClickListener {
            songList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(val binding: ItemMypageBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(album: Album){
            binding.itemSongTitleTv.text = album.title
            binding.itemSongSingerTv .text = album.singer
            binding.itemSongImgIv.setImageResource(album.coverImg!!)
        }
    }
}