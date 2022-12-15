package com.example.the2048.domain.usecases

import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem
import com.example.the2048.domain.repository.GameRepository

class GenerateNewItemsUseCase(
    private val repository: GameRepository
){

    operator fun invoke(gameField: GameField): List<NewItem>? {
        return repository.generateNewItems(gameField)
    }

}