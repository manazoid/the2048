package com.example.the2048.data

import android.util.Log
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem
import com.example.the2048.domain.entity.NewItem.Companion.EMPTY_ITEM
import com.example.the2048.domain.entity.NewItem.Companion.PRIMARY_NUMBER
import com.example.the2048.domain.entity.NewItem.Companion.SECONDARY_NUMBER
import com.example.the2048.domain.repository.GameRepository
import kotlin.random.Random

object GameRepositoryImpl : GameRepository {

    override fun generateNewItem(gameField: GameField): NewItem? {
        val freeItemsIndexes = HashSet<List<Int>>()
        gameField.field.forEachIndexed { x, row ->
            row.forEachIndexed { y, item ->
                if (item == EMPTY_ITEM) {
                    val vector = listOf(x, y)
                    freeItemsIndexes.add(vector)
                }
            }
        }
        if (freeItemsIndexes.isEmpty()) {
            return null
        }
        val chance = Random.nextFloat()
        val x = Random.nextInt(freeItemsIndexes.size)
        val cords = freeItemsIndexes.toList()[x]
        Log.d("chance", chance.toString())
        return if (chance >= .9) {
            NewItem(SECONDARY_NUMBER, cords)
        } else {
            NewItem(PRIMARY_NUMBER, cords)
        }
    }
}