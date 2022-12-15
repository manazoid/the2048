package com.example.the2048.presentation

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.ViewModelProvider
import com.example.the2048.R
import com.example.the2048.databinding.ActivityMainBinding
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem
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
        applyGestureDetector()
        startGameEvent()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.items.observe(this) {
            Log.d("GameFragment", "observe items $it")
        }
        viewModel.field.observe(this) {
            Log.d("GameFragment", "observe field $it")
            fieldGenerate(it, viewModel.items.value!!)
        }
        viewModel.shouldGameFinish.observe(this) {
            Log.d("GameFragment", "shouldGameFinish")
        }
    }

    private fun startTileAnimation(it: NewItem) {
        val x = it.coordinates[0]
        val y = it.coordinates[1]
        val scale = AnimationUtils.loadAnimation(this, R.anim.scale)
        val tableRow = tableRow(x)
        val current = tableRow.getVirtualChildAt(y)
        current.startAnimation(scale)
    }

    private fun tableRow(x: Int) = when (x) {
        0 -> binding.tr1
        1 -> binding.tr2
        2 -> binding.tr3
        3 -> binding.tr4
        else -> throw RuntimeException(
            "tableRow limit 4 items. invalid index $x"
        )
    }

    private fun fieldGenerate(gameField: GameField, update: NewItem) {
        gameField.field.forEachIndexed { x, row ->
            val tableRow = tableRow(x)
            row.forEachIndexed { y, item ->
                if (item != 0) {
                    var animation = false
                    if (x == update.coordinates[0]) {
                        if (y == update.coordinates[1]) {
                            animation = true
                        }
                    }
                    val current = tableRow.getVirtualChildAt(y)
                    launchNewGameItem(current.id, item.toString(), animation)
                }
            }
        }
    }

    private fun startGameEvent() {
        viewModel.startGame()
    }

    private fun applyGestureDetector() {
        mDetector = GestureDetectorCompat(viewModel.application, MyGestureListener())
    }

    override fun onStart() {
        super.onStart()
        _binding = ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun launchNewGameItem(resId: Int, itemText: String, animation: Boolean) {
        supportFragmentManager.beginTransaction()
            .replace(resId, GameItemFragment.newInstance(itemText, animation))
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun generateNewItem() {
        viewModel.generateNewItem(viewModel.field.value as GameField)
    }
}