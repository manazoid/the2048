package com.example.the2048.domain.repository

import com.example.the2048.domain.entity.Direction
import com.example.the2048.domain.entity.GameField

interface GameRepository {

    fun chooseDirection(
        direction: Direction
    ): GameField

    fun generateNewField(
        gameField: GameField
    ): GameField

    fun checkGameFinished(
        gameField: GameField
    ): Boolean
}