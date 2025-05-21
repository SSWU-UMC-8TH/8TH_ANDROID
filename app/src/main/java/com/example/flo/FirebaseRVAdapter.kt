package com.example.flo

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemMypageBinding

class FirebaseRVAdapter(private val albums: ArrayList<Album>):
    RecyclerView.Adapter<FirebaseRVAdapter.ViewHolder>() {

    interface MyItemClickListener{
        fun onDislikedAlbum(albumId: Int)
    }
    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

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
        holder.bind(albums[position])

        holder.binding.item.setOnClickListener {
            removeSong(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSong(position: Int){
        Log.d("delete album", albums[position].toString())
        mItemClickListener.onDislikedAlbum(albums[position].albumIdx)
        albums.removeAt(position)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = albums.size

    inner class ViewHolder(val binding: ItemMypageBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(album: Album){
            binding.itemSongTitleTv.text = album.title
            binding.itemSongSingerTv .text = album.singer

            val img = album.coverImg?:R.drawable.album1
            binding.itemSongImgIv.setImageResource(img)
        }
    }
}