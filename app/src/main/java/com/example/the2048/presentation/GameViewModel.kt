package com.example.the2048.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.the2048.data.GameRepositoryImpl
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem
import com.example.the2048.domain.usecases.GenerateNewItemsUseCase

class GameViewModel(
    val application: Application
) : ViewModel() {
    private val repository = GameRepositoryImpl

    private val generateNewItemsUseCase = GenerateNewItemsUseCase(repository)

    private val _field = MutableLiveData<GameField>()
    val field: LiveData<GameField>
        get() = _field

    private val _items = MutableLiveData<List<NewItem>>()
    val items: LiveData<List<NewItem>>
        get() = _items

    fun startGame() {
        val field = mutableListOf(
            mutableListOf(0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0),
            mutableListOf(0, 0, 0, 0),
        )
        Log.d("GameViewModel", "GameField $field")
        _field.value = GameField(field)
    }

    fun generateNewItem(gameField: GameField) {
        val newItems = generateNewItemsUseCase(gameField)
        _items.value = newItems
        newItems?.forEach {
            val x = it.coordinates[0]
            val y = it.coordinates[1]
            _field.value?.field?.get(x)?.set(y, it.number)
        }
    }
}