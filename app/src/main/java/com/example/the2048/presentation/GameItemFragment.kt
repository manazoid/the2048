package com.example.the2048.presentation

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.the2048.R
import com.example.the2048.databinding.FragmentItemBinding

class GameItemFragment : Fragment() {

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
        val text: String?
        val animation: Boolean
        with(arguments) {
            text = this?.getString(ITEM_TEXT)
            animation = this?.getBoolean(ITEM_ANIMATION) == true
        }
        binding.cardText.text = text
        val textColorId = if (text?.length == 1) {
            R.color.title_dark
        } else {
            R.color.title_light
        }
        val bgColorId = when (text) {
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
        val resColor = resources.getColor(textColorId, null)
        binding.cardText.setTextColor(resColor)
        binding.cvEmpty.setBackgroundResource(bgColorId)
        if (animation) {
            val scale = AnimationUtils.loadAnimation(view.context, R.anim.scale)
            view.startAnimation(scale)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ITEM_TEXT = "item_text"
        private const val ITEM_ANIMATION = "item_animation"
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

        fun newInstance(itemText: String?, animation: Boolean?): GameItemFragment {
            return GameItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ITEM_TEXT, itemText)
                    if (animation != null) {
                        putBoolean(ITEM_ANIMATION, animation)
                    }
                }
            }
        }
    }
}