package com.example.the2048.domain.usecases

import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.repository.GameRepository

class CheckGameFinishedUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(gameField: GameField): Boolean {
        return repository.checkGameFinished(gameField)
    }
}