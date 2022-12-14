package com.example.the2048.domain.repository

import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem

interface GameRepository {

    fun generateNewItems(
        gameField: GameField
    ): List<NewItem>?
}