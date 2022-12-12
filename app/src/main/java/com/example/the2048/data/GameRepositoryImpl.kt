package com.example.the2048.data

import com.example.the2048.domain.entity.Direction
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.repository.GameRepository

class GameRepositoryImpl : GameRepository {

    override fun chooseDirection(direction: Direction): GameField {
        TODO("Not yet implemented")
    }

    override fun generateNewField(gameField: GameField): GameField {
        TODO("Not yet implemented")
    }

    override fun checkGameFinished(gameField: GameField): Boolean {
        TODO("Not yet implemented")
    }
}