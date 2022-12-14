package com.example.the2048.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.the2048.R
import com.example.the2048.databinding.FragmentItemBinding

class GameItemFragment: Fragment() {


    private var _binding: FragmentItemBinding? = null
    private val binding: FragmentItemBinding
        get() = _binding ?: throw RuntimeException("FragmentWelcomeBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        fun newInstance(): GameFragment {
            return GameFragment()
        }
    }
}