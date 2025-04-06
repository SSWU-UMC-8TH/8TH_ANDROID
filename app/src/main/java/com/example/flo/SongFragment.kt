package com.example.flo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flo.databinding.FragmentSongBinding

class SongFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var isMix = true

    private lateinit var binding: FragmentSongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSongBinding.inflate(inflater,container,false)
        binding.songMixoffTg.setOnClickListener {
            if(isMix){
                binding.songMixoffTg.setImageResource(R.drawable.btn_toggle_on)
            }else{
                binding.songMixoffTg.setImageResource(R.drawable.btn_toggle_off)
            }
            isMix=!isMix
        }

        return binding.root
    }
}