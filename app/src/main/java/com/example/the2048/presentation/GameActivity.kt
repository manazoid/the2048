package com.example.the2048.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.the2048.R
import com.example.the2048.databinding.ActivityGameBinding
import com.example.the2048.databinding.FragmentFieldBinding
import com.example.the2048.domain.entity.GameField
import kotlin.math.abs

class GameActivity : AppCompatActivity() {

    private lateinit var mDetector: GestureDetectorCompat

    private val viewModelFactory by lazy {
        GameViewModelFactory(application)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }
    private var _binding: ActivityGameBinding ? = null
    private val binding: ActivityGameBinding
        get() = _binding ?: throw RuntimeException("ActivityGameBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        mDetector = GestureDetectorCompat(viewModel.application, MyGestureListener())
        viewModel.startGame()
//        viewModel.generateNewItem(viewModel.field.value as GameField)
//        viewModel.items.observe(viewLifecycleOwner) {
//            Log.d("GameFragment", "observe items $it")
//        }
        viewModel.field.observe(this) {
            Log.d("GameFragment", "observe field $it")

//            val item = it.field[0][0]
//            launchNewGameItem(binding.fcvItem00.id, item.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        _binding = ActivityGameBinding.inflate(layoutInflater)
        launchNewGameItem(R.id.fcv_item_00, "2048")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun launchNewGameItem(resId: Int, itemText: String) {
        supportFragmentManager.beginTransaction()
            .replace(resId, GameItemFragment.newInstance(itemText))
            .addToBackStack(null)
            .commit()
    }

    private open inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.d("MyGestureListener", "onFling: $velocityX – $velocityY")
            val delta = abs(velocityX) - abs(velocityY)
            val swipeDetectAxisY = detectDirection(delta)
            if (swipeDetectAxisY) {
                // DETECT
                // TRUE -> ↑
                // FALSE -> ↓
                val detect = detectDirection(velocityY)
                if (detect) {
                    onSwipeTop()
                } else {
                    onSwipeBottom()
                }
                Log.d("swipeDetectAxisY", "move velocityY $detect")
            } else {
                // DETECT
                // TRUE -> ←
                // FALSE -> →
                val detect = detectDirection(velocityX)
                if (detect) {
                    onSwipeLeft()
                } else {
                    onSwipeRight()
                }
                Log.d("swipeDetectAxisX", "move velocityX $detect")
            }
            generateNewItem()
            return true
        }

        private fun detectDirection(fling: Float): Boolean {
            return fling < 0
        }

        open fun onSwipeRight() {
        }

        open fun onSwipeLeft() {
        }

        open fun onSwipeTop() {
        }

        open fun onSwipeBottom() {
        }
    }

    private fun generateNewItem() {
        viewModel.generateNewItem(viewModel.field.value as GameField)
    }
}