package com.example.the2048.presentation

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.ViewModelProvider
import com.example.the2048.R
import com.example.the2048.databinding.ActivityMainBinding
import com.example.the2048.domain.entity.Direction
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem
import kotlin.math.abs
import kotlin.properties.Delegates

private const val s = "Отмена"

class MainActivity : AppCompatActivity() {

    private lateinit var mDetector: GestureDetectorCompat
    private lateinit var prefs: SharedPreferences
    private var gestureLocked by Delegates.notNull<Boolean>()

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
        _binding = ActivityMainBinding.inflate(layoutInflater)
        prefs = defaultPreference(this)
        viewModel.setupBestScore(prefs.bestScore)
        gestureLocked = false
        setContentView(binding.root)
        clickListeners()
        applyGestureDetector()
        startGameEvent()
        observeLiveData()
        showTitle()
    }

    private fun showTitle() {
        launchNewGameItem(binding.fcvGameTitle.id, "2048", false)
    }

    private fun clickListeners() {
        binding.resetButton.setOnClickListener {
            basicAlert(
                R.string.reset_field,
                android.R.drawable.ic_dialog_alert,
                resetPositiveButtonClick
            ).show()
        }
        binding.undoButton.setOnClickListener {
            viewModel.undo.value?.let { it1 ->
                Log.d("MainActivity", "clickListeners $it1")
                viewModel.undoLatestAction(it1)
            }
        }
    }

    private val gameOverPositiveButton = { dialog: DialogInterface, which: Int ->
        viewModel.restartGame()
    }

    private val resetPositiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(
            applicationContext,
            R.string.retry_button, Toast.LENGTH_SHORT
        ).show()
    }

    private fun basicAlert(title: String, icon: Int?, doOnPositive: OnClickListener): AlertDialog.Builder {
        return AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(R.string.would_like_repeat_again)
            setPositiveButton(R.string.okay_label, doOnPositive)
            setNegativeButton(R.string.cancel_label, null)
            icon?.let { setIcon(icon) }
        }
    }

    private fun basicAlert(title: Int, icon: Int?, doOnPositive: OnClickListener): AlertDialog.Builder {
        return AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(R.string.would_like_repeat_again)
            setPositiveButton(R.string.okay_label, doOnPositive)
            setNegativeButton(R.string.cancel_label, null)
            icon?.let { setIcon(icon) }
        }
    }

    private fun observeLiveData() {
        viewModel.items.observe(this) {
            Log.d("GameFragment", "observe items $it")
        }
        viewModel.field.observe(this) {
            Log.d("GameFragment", "observe field $it")
            viewModel.items.value?.let { itemToAnimate -> fieldGenerate(it, itemToAnimate) }
        }
        viewModel.shouldGameFinish.observe(this) {
            val title = String.format(
                getString(R.string.game_over),
                viewModel.currentScore.value
            )
            basicAlert(
                title,
                null,
                gameOverPositiveButton
            ).show()
        }
        viewModel.shouldLockGestures.observe(this) {
            gestureLocked = it
        }
        viewModel.bestScore.observe(this) {
            prefs.bestScore = it
            binding.tvBestScoreValue.text = it.toString()
        }
        viewModel.currentScore.observe(this) {
            binding.tvScoreValue.text = it.toString()
        }
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
                val current = tableRow.getVirtualChildAt(y)
                if (item != 0) {
                    launchNewGameItem(
                        current.id,
                        item.toString(),
                        false
//                        y == update.coordinates[1] && x == update.coordinates[0]
                    )
                } else {
                    launchNewGameItem(current.id, "", false)
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
                // DETECT / TRUE -> ↑ / FALSE -> ↓
                val detect = detectDirection(velocityY)
                if (detect) {
                    onSwipeTop()
                } else {
                    onSwipeBottom()
                }
                Log.d("swipeDetectAxisY", "move velocityY $detect")
            } else {
                // DETECT / TRUE -> ← / FALSE -> →
                val detect = detectDirection(velocityX)
                if (detect) {
                    onSwipeLeft()
                } else {
                    onSwipeRight()
                }
                Log.d("swipeDetectAxisX", "move velocityX $detect")
            }
//            generateNewItem()
            return true
        }

        private fun detectDirection(fling: Float): Boolean {
            return fling < 0
        }

        open fun onSwipeRight() {
            moveItemsByDirection(Direction.RIGHT)
        }

        open fun onSwipeLeft() {
            moveItemsByDirection(Direction.LEFT)
        }

        open fun onSwipeTop() {
            moveItemsByDirection(Direction.UP)
        }

        open fun onSwipeBottom() {
            moveItemsByDirection(Direction.DOWN)
        }
    }

    private fun moveItemsByDirection(direction: Direction) {
        viewModel.moveItems(viewModel.field.value as GameField, direction)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun generateNewItem() {
        viewModel.generateNewItem(viewModel.field.value as GameField)
    }

    private companion object {

        private const val BEST_SCORE = "BEST_SCORE"

        fun defaultPreference(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

        fun customPreference(context: Context, name: String): SharedPreferences =
            context.getSharedPreferences(name, Context.MODE_PRIVATE)

        inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
            val editMe = edit()
            operation(editMe)
            editMe.apply()
        }

        var SharedPreferences.bestScore
            get() = getInt(BEST_SCORE, 0)
            set(value) {
                editMe {
                    it.putInt(BEST_SCORE, value)
                }
            }

        var SharedPreferences.clearValues
            get() = run { }
            set(value) {
                editMe {
                    it.clear()
                }
            }

    }
}