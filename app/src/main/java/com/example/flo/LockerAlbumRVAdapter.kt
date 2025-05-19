package com.example.flo


import android.annotation.SuppressLint
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.flo.databinding.ItemSongBinding

class LockerAlbumRVAdapter () : RecyclerView.Adapter<LockerAlbumRVAdapter.ViewHolder>(){

    private val switchStatus = SparseBooleanArray()
    private val songs = ArrayList<Song>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LockerAlbumRVAdapter.ViewHolder {
        val binding: ItemSongBinding = ItemSongBinding
            .inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LockerAlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])

        holder.binding.itemSongMoreIv.setOnClickListener {
            itemClickListener.onRemoveAlbum(songs[position].id)
            removeSong(position) // 화면에서 아이템 제거
        }

        val switch = holder.binding.switchRV
        switch.isChecked = switchStatus[position]
        switch.setOnClickListener {
            if(switch.isChecked){
                switchStatus.put(position,true)
            }
            else{
                switchStatus.put(position,false)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int  = songs.size

    inner class ViewHolder(val binding:ItemSongBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(song: Song){
            binding.itemSongTitleTv.text = song.title
            binding.itemSongSingerTv.text = song.singer
            binding.itemSongImgIv.setImageResource(song.coverImg!!)
        }
    }

    interface OnItemClickListener {
        fun onRemoveAlbum(SongId: Int) {
        }
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>){
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeSong(position: Int){
        songs.removeAt(position)
        notifyDataSetChanged()
    }

    fun selectAll(select: Boolean) {
        for (i in 0 until songs.size) {
            switchStatus.put(i, select)
        }
        notifyDataSetChanged()
    }

    fun getSelectedSongs(): List<Song> {
        val selectedSongs = mutableListOf<Song>()
        for (i in 0 until songs.size) {
            if (switchStatus[i]) {
                selectedSongs.add(songs[i])
            }
        }
        return selectedSongs
    }

    fun removeSelectedSongs() {
        val iterator = songs.iterator()
        var index = 0
        while (iterator.hasNext()) {
            iterator.next()
            if (switchStatus[index]) {
                iterator.remove()
                switchStatus.delete(index)
            }
            index++
        }
        notifyDataSetChanged()
    }

}