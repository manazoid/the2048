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

    private val _items = MutableLiveData<NewItem>()
    val items: LiveData<NewItem>
        get() = _items

    private val _shouldGameFinish = MutableLiveData<Unit>()
    val shouldGameFinish: LiveData<Unit>
        get() = _shouldGameFinish

    fun startGame() {
        val field = mutableListOf(
            mutableListOf(2, 2, 0, 0),
            mutableListOf(0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0),
            mutableListOf(0, 0, 4, 4),
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

    fun moveItems(gameField: GameField, direction: Direction) {
        val startIndex: Int
        val finishIndex: Int
        val field: List<List<Int>>
        when (direction) {
            Direction.RIGHT -> {
                startIndex = MAX_FIELD_INDEX
                finishIndex = MIN_FIELD_INDEX
                field = gameField.field
            }
            Direction.DOWN -> {
                startIndex = MAX_FIELD_INDEX
                finishIndex = MIN_FIELD_INDEX
                field = swapAxisGameField(gameField.field)
            }
            Direction.LEFT -> {
                startIndex = MIN_FIELD_INDEX
                finishIndex = MAX_FIELD_INDEX
                field = gameField.field
            }
            Direction.UP -> {
                startIndex = MIN_FIELD_INDEX
                finishIndex = MAX_FIELD_INDEX
                field = swapAxisGameField(gameField.field)
            }
        }
        val newField = if (direction == Direction.UP || direction == Direction.DOWN) {
            val newField = applyChanges(field, startIndex, finishIndex)
            Log.d("GameViewModel", "applyChanges. before: $field after: $newField")
            newField
        } else {
            val swappedField = applyChanges(field, startIndex, finishIndex)
            swapAxisGameField(swappedField)
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
            newRow.add(iterateItemsAxis(row, startIndex, finishIndex))
        }
        return newRow.toList()
    }

    private fun swapAxisGameField(field: List<List<Int>>): List<List<Int>> {
        val swapAxis = mutableListOf<List<Int>>()
        repeat(4) { y ->
            val newRow = mutableListOf<Int>()
            repeat(4) { x ->
                newRow.add(field[x][y])
            }
            swapAxis.add(newRow.toList())
        }
        return swapAxis.toList()
    }

    private fun iterateItemsAxis(row: List<Int>, startIndex: Int, finishIndex: Int): List<Int> {
        val newRow = row as MutableList<Int>
        val joinIndexes = mutableListOf<Int>()
        val c = if (finishIndex == MAX_FIELD_INDEX) {
            1
        } else {
            -1
        }
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

    private companion object {

        private const val MIN_FIELD_INDEX = 0
        private const val MAX_FIELD_INDEX = 3
    }
}