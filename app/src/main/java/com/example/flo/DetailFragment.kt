package com.example.flo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flo.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var detail: String? = null

    private lateinit var binding: FragmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            detail = it.getString("detail")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentDetailBinding.inflate(inflater,container,false)
        binding.detail.text = detail
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(detailText: String) = DetailFragment().apply {
                arguments = Bundle().apply {
                    putString("detail", detailText)
                }
            }
    }
}