package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemMypageBinding

class MypageRVAdapter(private val songList: ArrayList<Album>): RecyclerView.Adapter<MypageRVAdapter.ViewHolder>() {

    private var isClicked :ArrayList<Boolean> = ArrayList<Boolean>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMypageBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        for(i in 0 until songList.size){
            isClicked.add(false)
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(songList[position])

        holder.binding.itemSongSwitch.isChecked = isClicked[position]

        holder.binding.itemSongSwitch.setOnClickListener {isClicked[position] = !isClicked[position]}

        holder.binding.itemSongMoreIv.setOnClickListener {
            songList.removeAt(position)
            isClicked.removeAt(position)
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