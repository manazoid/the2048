package com.example.the2048.domain.usecases

import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.repository.GameRepository

class GenerateNewFieldUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(gameField: GameField): GameField {
        return repository.generateNewField(gameField)
    }

    private companion object {


    }
}