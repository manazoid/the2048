package com.example.the2048.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.the2048.R
import com.example.the2048.databinding.FragmentEmptyItemBinding
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
        val text = arguments?.getString(ITEM_TEXT)
        binding.cardText.text = when (text) {
            "0" -> ""
            else -> text
        }
        val colorId = when (text) {
                ITEM_1 -> R.color.step_1
                ITEM_2 -> R.color.step_2
                ITEM_3 -> R.color.step_3
                ITEM_4 -> R.color.step_4
                ITEM_5 -> R.color.step_5
                ITEM_6 -> R.color.step_6
                ITEM_7 -> R.color.step_7
                ITEM_8 -> R.color.step_8
                ITEM_9 -> R.color.step_9
                ITEM_10 -> R.color.step_10
                ITEM_11 -> R.color.step_11
                else -> R.color.tile
            }
        binding.cvEmpty.setBackgroundResource(colorId)
    }

    companion object {

        private const val ITEM_TEXT = "item_text"
        private const val ITEM_1 = "2"
        private const val ITEM_2 = "4"
        private const val ITEM_3 = "8"
        private const val ITEM_4 = "16"
        private const val ITEM_5 = "32"
        private const val ITEM_6 = "64"
        private const val ITEM_7 = "128"
        private const val ITEM_8 = "256"
        private const val ITEM_9 = "512"
        private const val ITEM_10 = "1024"
        private const val ITEM_11 = "2048"

        fun newInstance(itemText: String?): GameItemFragment {
            return GameItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ITEM_TEXT, itemText)
                }
            }
        }
    }
}