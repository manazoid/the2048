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
//            mutableListOf(2, 2, 2, 2),
//            mutableListOf(8, 0, 0, 8),
//            mutableListOf(8, 0, 0, 8),
//            mutableListOf(8, 4, 4, 8),
            mutableListOf(0, 0, 2, 0),
            mutableListOf(0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0),
        )
        val newField = GameField(field)
        _field.value = newField
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

    fun moveItems(gameField: GameField, direction: Direction) {
        val fieldRotation = when (direction) {
            Direction.RIGHT -> 0
            Direction.DOWN -> 1
            Direction.LEFT -> 2
            Direction.UP -> 3
        }
//        val field = swapAxisGameField(gameField.field, fieldRotation)
//        val newField = if (direction == Direction.UP || direction == Direction.DOWN) {
//            val swappedField = applyChanges(field)
//            swapAxisGameField(swappedField)
//        } else {
//            val appliedField = applyChanges(field)
//            Log.d("GameViewModel", "applyChanges. before: $field after: $appliedField")
//            appliedField
//        }
        _field.value = GameField(
            field = swapAxisGameField(
                gameField.field,
                fieldRotation
            )
        )
    }

    private fun swapAxisGameField(
        field: List<List<Int>>,
        rotateIterationCount: Int
    ): MutableList<MutableList<Int>> {
        var newField = field as MutableList<MutableList<Int>>
        var swapAxis: MutableList<MutableList<Int>>
        repeat(rotateIterationCount) {
            swapAxis = mutableListOf<MutableList<Int>>()
            field.forEachIndexed { x, row ->
                val newRow = mutableListOf<Int>()
                row.forEachIndexed { y, item ->
                    newRow.add(field[y][MAX_FIELD_INDEX-x])
                }
                swapAxis.add(newRow)
            }
            newField = swapAxis
        }
        return newField
    }

    private fun iterateItemsAxis(row: List<Int>, startIndex: Int, finishIndex: Int): List<Int> {
        val newRow = row as MutableList<Int>
        val joinIndexes = mutableListOf<Int>()
        val c = calculateCoefficient(finishIndex)
        for (i in startIndex until finishIndex) {
            val item = row[i]
            val nextItem = row[i + c]
            if (item != 0 && item == nextItem) {
                joinIndexes.add(i)
            }
        }
        joinIndexes.toList().forEach {
            newRow[it] = row[it] + row[it + c]
            newRow[it + c] = 0
        }
        Log.d("GameViewModel", "iterateItemsAxis $row -> $newRow")
        Log.d("GameViewModel", "joinIndexes $joinIndexes")
        return newRow.toList()
    }

    private fun calculateCoefficient(finishIndex: Int) = if (finishIndex == MAX_FIELD_INDEX) {
        1
    } else {
        -1
    }

    private companion object {

        private const val MIN_FIELD_INDEX = 0
        private const val MAX_FIELD_INDEX = 3
        private const val FIELD_SIZE = 4
    }
}