package com.example.flo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flo.databinding.FragmentRecommendBinding

class RecommendFragment(
    val panel: RecommendPanel
    ) : Fragment() {

    private lateinit var binding: FragmentRecommendBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecommendBinding.inflate(inflater, container, false)

        binding.ment.text = panel.ment

        val panelImg = panel.panelImg
        val album1 = panel.album1
        val album2 = panel.album2

        panelImg?.let{ binding.mainImage.setImageResource(it) }
        album1?.let{ binding.albumCover1.setImageResource(it.coverImg!!) }
        album2?.let{ binding.albumCover2.setImageResource(it.coverImg!!) }
        binding.title1.text = album1?.title
        binding.title2.text = album2?.title
        binding.singer1.text = album1?.singer
        binding.singer2.text = album2?.singer

        return binding.root
    }
}