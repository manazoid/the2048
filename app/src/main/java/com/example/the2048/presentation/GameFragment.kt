package com.example.the2048.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.the2048.R
import com.example.the2048.databinding.FragmentFieldBinding

class GameFragment : Fragment() {

    private lateinit var mDetector: GestureDetectorCompat

    private val viewModelFactory by lazy {
        GameViewModelFactory(requireActivity().application)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }
    private var _binding: FragmentFieldBinding? = null
    private val binding: FragmentFieldBinding
        get() = _binding ?: throw RuntimeException("FragmentWelcomeBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetector = GestureDetectorCompat(viewModel.application, MyGestureListener())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        launchGameField()
        binding.root.setOnTouchListener { _, event ->
            mDetector.onTouchEvent(event)
            true
        }
    }

    private fun launchGameField() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameItemFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent): Boolean {
            Log.d("MyGestureListener", "onDown: $event")
            return true
        }

        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.d("MyGestureListener", "onFling: $event1 – $event2")
            return true
        }
    }

    companion object {

        fun newInstance(): GameFragment {
            return GameFragment()
        }
    }
}