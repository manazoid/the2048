package com.example.the2048.presentation

import android.app.Application
import android.util.Log
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
        _undo.value = null
        _items.value = null
    }

    fun moveItems(gameField: GameField, direction: Direction) {
        _undo.value = gameField
        val startIndex: Int
        val finishIndex: Int
        val field: List<List<Int>>
        when (direction) {
            Direction.RIGHT -> {
                startIndex = MIN_FIELD_INDEX
                finishIndex = MAX_FIELD_INDEX
                field = gameField.field
            }
            Direction.DOWN -> {
                startIndex = MAX_FIELD_INDEX
                finishIndex = MIN_FIELD_INDEX
                field = swapAxisGameField(gameField.field)
            }
            Direction.LEFT -> {
                startIndex = MAX_FIELD_INDEX
                finishIndex = MIN_FIELD_INDEX
                field = gameField.field
            }
            Direction.UP -> {
                startIndex = MIN_FIELD_INDEX
                finishIndex = MAX_FIELD_INDEX
                field = swapAxisGameField(gameField.field)
            }
        }
        val newField = if (direction == Direction.UP || direction == Direction.DOWN) {
            val swappedField = applyChanges(field, startIndex, finishIndex)
            swapAxisGameField(swappedField)
        } else {
            val appliedField = applyChanges(field, startIndex, finishIndex)
            Log.d("GameViewModel", "applyChanges. before: $field after: $appliedField")
            appliedField
        }
        _field.value = GameField(field = newField as MutableList<MutableList<Int>>)
    }

    private fun applyChanges(
        field: List<List<Int>>,
        startIndex: Int,
        finishIndex: Int
    ): List<List<Int>> {
        val newRow = mutableListOf<List<Int>>()
        field.forEach { row ->
            newRow.add(
                processRow(row, startIndex, finishIndex)
                //            iterateItemsAxis(row, startIndex, finishIndex)
            )
        }
        return newRow.toList()
    }

    private fun processRow(field: List<Int>, startIndex: Int, finishIndex: Int): MutableList<Int> {
        field as MutableList<Int>
        val c = calculateCoefficient(finishIndex)
        for (i in startIndex until finishIndex) {
            if (field[i] == EMPTY_ITEM && field[i + c] != EMPTY_ITEM) {
                field[i] = field[i + c].also { field[i + c] = field[i] }
            }
        }
//        if (field[finishIndex] == EMPTY_ITEM) {
//            for (i in finishIndex until startIndex) {
//                field[i] = field[i - c].also { field[i - c] = field[i] }
//            }
//        }
        return field
    }

    private fun swapAxisGameField(field: List<List<Int>>): List<List<Int>> {
        val swapAxis = mutableListOf<List<Int>>()
//        val initRange = (0..MAX_FIELD_INDEX)
//        for (y in if (clockwise) {
//            initRange
//        } else {
//            initRange.reversed()
//        }
        repeat(FIELD_SIZE) { y ->
            val newRow = mutableListOf<Int>()
            repeat(FIELD_SIZE) { x ->
                newRow.add(field[x][y])
            }
            swapAxis.add(newRow.toList())
        }
        return swapAxis.toList()
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