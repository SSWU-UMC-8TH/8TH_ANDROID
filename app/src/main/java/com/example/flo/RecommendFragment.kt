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

        binding.mainImage.setImageResource(panel.panelImg)
        binding.ment.text = panel.ment
        binding.albumCover1.setImageResource(R.drawable.img_lifes_like)
        binding.albumCover2.setImageResource(R.drawable.img_modal_soul)
        binding.title1.text = panel.album1.albumFragment.title
        binding.title2.text = panel.album2.albumFragment.title
        binding.singer1.text = panel.album1.albumFragment.singer
        binding.singer2.text = panel.album2.albumFragment.singer

        return binding.root
    }
}