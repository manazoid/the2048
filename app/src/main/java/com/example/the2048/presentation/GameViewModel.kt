package com.example.the2048.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.the2048.data.GameRepositoryImpl
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem
import com.example.the2048.domain.usecases.GenerateNewItemUseCase

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
            mutableListOf(0, 0, 0, 0),
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

//    fun moveItems(gameField: GameField, direction: Direction) {
//        when (direction) {
//            Direction.RIGHT -> {
//
//            }
//            Direction.DOWN -> {
//
//            }
//            Direction.LEFT -> {
//
//            }
//            Direction.UP -> {
//
//            }
//        }
//    }
//
//    private fun iterateItemsX(gameField: GameField, startIndex: Int, finishIndex: Int) {
//
//    }
}