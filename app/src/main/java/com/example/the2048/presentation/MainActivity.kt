package com.example.the2048.presentation

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.example.the2048.R
import com.example.the2048.databinding.ActivityMainBinding
import com.example.the2048.domain.entity.GameField
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var mDetector: GestureDetectorCompat

    private val viewModelFactory by lazy {
        GameViewModelFactory(application)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw RuntimeException("ActivityGameBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDetector = GestureDetectorCompat(viewModel.application, MyGestureListener())
        viewModel.startGame()
        viewModel.generateNewItem(viewModel.field.value as GameField)
//        viewModel.items.observe(viewLifecycleOwner) {
//            Log.d("GameFragment", "observe items $it")
//        }
        viewModel.field.observe(this) {
            Log.d("GameFragment", "observe field $it")

            it.field.forEachIndexed { x, row ->
                val tableRow = when (x) {
                    0 -> binding.tr1
                    1 -> binding.tr2
                    2 -> binding.tr3
                    3 -> binding.tr4
                    else -> throw RuntimeException(
                        "viewModel.field limit 4 items. invalid index $x"
                    )
                }
                row.forEachIndexed { y, item ->
                    val current = tableRow.getVirtualChildAt(y)
                    launchNewGameItem(current.id, item.toString())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        _binding = ActivityMainBinding.inflate(layoutInflater)
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