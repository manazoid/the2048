package com.example.the2048.presentation

import android.app.Application
import android.util.Log
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.the2048.data.GameRepositoryImpl
import com.example.the2048.domain.entity.Direction
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem
import com.example.the2048.domain.entity.NewItem.Companion.EMPTY_ITEM
import com.example.the2048.domain.usecases.GenerateNewItemUseCase

private const val i1 = 0

class GameViewModel(
    val application: Application
) : ViewModel() {

    private val repository = GameRepositoryImpl
    private val generateNewItemUseCase = GenerateNewItemUseCase(repository)

    private val _field = MutableLiveData<GameField>()
    val field: LiveData<GameField>
        get() = _field

    private val _undo = MutableLiveData<GameField>()
    val undo: LiveData<GameField>
        get() = _undo

    private val _items = MutableLiveData<NewItem>()
    val items: LiveData<NewItem>
        get() = _items

    private val _shouldLockGestures = MutableLiveData<Boolean>()
    val shouldLockGestures: LiveData<Boolean>
        get() = _shouldLockGestures

    private val _shouldGameFinish = MutableLiveData<Unit>()
    val shouldGameFinish: LiveData<Unit>
        get() = _shouldGameFinish

    private val _currentScore = MutableLiveData<Int>()
    val currentScore: LiveData<Int>
        get() = _currentScore

    private val _bestScore = MutableLiveData<Int>()
    val bestScore: LiveData<Int>
        get() = _bestScore

    fun startGame() {
        val field = mutableListOf(
            mutableListOf(8, 0, 0, 8),
//            mutableListOf(2, 2, 2, 2),
            mutableListOf(8, 0, 4, 4),
//            mutableListOf(8, 4, 4, 8),
//            mutableListOf(0, 0, 2, 0),
//            mutableListOf(0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0),
        )
        val newField = GameField(field)
        _field.value = newField
        _currentScore.value = INITIAL_SCORE
        repeat(2) {
            generateNewItem(newField)
        }
    }

    fun restartGame() {
        nullGameData()
        startGame()
    }

    fun generateNewItem(gameField: GameField) {
        Log.d("GameViewModel", "generateNewItem $gameField")
        val newItems = generateNewItemUseCase(gameField)
        if (newItems == null) {
            _shouldGameFinish.value = Unit
        } else {
            _items.value = newItems
            val x = newItems.coordinates[0]
            val y = newItems.coordinates[1]
            gameField.field[x][y] = newItems.number
            _field.value = gameField
        }
    }

    fun undoLatestAction(undo: GameField) {
        _field.value = undo
        nullGameData()
    }

    fun setupBestScore(score: Int) {
        _bestScore.value = score
    }

    private fun nullGameData() {
        _undo.value = null
        _items.value = null
    }

    fun moveItems(gameField: GameField, direction: Direction, lastScore: Int) {
        val fieldRotation = when (direction) {
            Direction.RIGHT -> 0
            Direction.DOWN -> 1
            Direction.LEFT -> 2
            Direction.UP -> 3
        }
//        var field = swapAxisGameField(gameField.field, fieldRotation)
        var field = gameField.field
        field = moveAndSumItems(field, lastScore)
//        val rotateReverse = FIELD_SIZE - fieldRotation
//        field = swapAxisGameField(field, rotateReverse)
        _field.value = GameField(field)
    }

    private fun moveAndSumItems(field: MutableList<MutableList<Int>>, lastScore: Int): MutableList<MutableList<Int>> {
        val range = MIN_FIELD_INDEX until MAX_FIELD_INDEX
        val reversedRange = MAX_FIELD_INDEX downTo DROP_AMOUNT
        for (x in range) {
            val row = field[x]
            field[x] = moveItemsList(row, range)
            field[x] = sumItemsList(row, reversedRange, range, lastScore)
        }
        return field
    }


    private fun sumItemsList(
        row: MutableList<Int>,
        reversedRange: IntProgression,
        range: kotlin.ranges.IntRange,
        lastScore: Int,
    ): MutableList<Int> {
        var count = MIN_SUM_ITERATE
        for (i in reversedRange) {
            val first = row[i]
            val second = row[i - C]
            if (first != EMPTY_ITEM && second != EMPTY_ITEM && first == second) {
                count++
                val sum = first + second
                row[i] = sum
                _currentScore.value = lastScore + sum
                row[i - C] = EMPTY_ITEM
            }
        }
        if (count != MIN_SUM_ITERATE) {
            return moveItemsList(row, range)
        }
        return row
    }

    private fun moveItemsList(
        row: MutableList<Int>,
        range: kotlin.ranges.IntRange
    ): MutableList<Int> {
        var count = MIN_NOT_MOVED_AMOUNT
        for (i in range) {
            val second = row[i + C]
            if (second == EMPTY_ITEM) {
                row[i] = row[i + C].also { row[i + C] = row[i] }
            }
        }
        for (i in MIN_FIELD_INDEX..C) {
            if (row[i] != EMPTY_ITEM && row[i + C] == EMPTY_ITEM) {
                count++
            }
        }
        if (count != MIN_NOT_MOVED_AMOUNT) {
            var newRow = row
            newRow = moveItemsList(newRow, range)
            return newRow
        }
        return row
    }

    private fun swapAxisGameField(
        field: MutableList<MutableList<Int>>,
        rotateIterationCount: Int
    ): MutableList<MutableList<Int>> {
        if (rotateIterationCount == MAX_ROTATE) {
            return field
        }
        var newField = field
        var swapAxis: MutableList<MutableList<Int>>
        val rotate = when (rotateIterationCount) {
            COUNTER_CLOCKWISE_ROTATE -> 1
            else -> rotateIterationCount
        }
        repeat(rotate) {
            swapAxis = mutableListOf<MutableList<Int>>()
            field.forEachIndexed { x, row ->
                val newRow = mutableListOf<Int>()
                row.forEachIndexed { y, item ->
                    if (rotateIterationCount < COUNTER_CLOCKWISE_ROTATE) {
                        newRow.add(field[MAX_FIELD_INDEX - y][x])
                    } else {
                        newRow.add(field[y][MAX_FIELD_INDEX - x])
                    }
                }
                swapAxis.add(newRow)
            }
            newField = swapAxis
        }
        return newField
    }

    private companion object {

        private const val FIELD_SIZE = 4
        private const val MAX_ROTATE = 4
        private const val MIN_FIELD_INDEX = 0
        private const val MAX_FIELD_INDEX = FIELD_SIZE - 1
        private const val COUNTER_CLOCKWISE_ROTATE = 3
        private const val DROP_AMOUNT = MIN_FIELD_INDEX + 1
        private const val MIN_NOT_MOVED_AMOUNT = 0
        private const val MIN_SUM_ITERATE = 0
        private const val INITIAL_SCORE = 0
        private const val C = 1
    }
}