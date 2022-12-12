package com.example.the2048.domain.usecases

import com.example.the2048.domain.entity.Direction
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.repository.GameRepository

class ChooseDirectionUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(direction: Direction): GameField {
        return repository.chooseDirection(direction)
    }

    private companion object {


    }
}