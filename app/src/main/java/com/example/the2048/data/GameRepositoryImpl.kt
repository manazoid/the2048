package com.example.the2048.data

import android.util.Log
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem
import com.example.the2048.domain.repository.GameRepository
import kotlin.random.Random

object GameRepositoryImpl : GameRepository {

    private const val PRIMARY_NUMBER = 2
    private const val SECONDARY_NUMBER = 4
    private const val MIN_CORD_INCLUSIVE = 0
    private const val MAX_CORD_EXCLUSIVE = 4
    private const val EMPTY_ITEM = 0
    private const val MIN_ITEM_ITERATION = 0
    private const val AVERAGE_ITEM_ITERATION = 1
    private const val MAX_ITEM_ITERATION = 2

    override fun generateNewItems(gameField: GameField): List<NewItem>? {
        val freeItemsIndexes = HashSet<List<Int>>()
        Log.d("GameRepositoryImpl", "generateNewItems $gameField")
        gameField.field.forEachIndexed { x, row ->
            row.forEachIndexed { y, item ->
                if (item == EMPTY_ITEM) {
                    val vector = listOf(x, y)
                    freeItemsIndexes.add(vector)
                }
            }
        }
        val newItemList = HashSet<NewItem>()
        if (freeItemsIndexes.isEmpty()) {
            return null
        }
        var freeItemsCount = if (freeItemsIndexes.size == AVERAGE_ITEM_ITERATION) {
            AVERAGE_ITEM_ITERATION
        } else {
            MAX_ITEM_ITERATION
        }
        while (freeItemsCount != MIN_ITEM_ITERATION) {
            freeItemsCount--
            val chance = Random.nextFloat()
            val x = Random.nextInt(freeItemsIndexes.size)
            val cords = freeItemsIndexes.toList()[x]
            Log.d("chance", chance.toString())
            if (chance >= .9) {
                newItemList.add(NewItem(SECONDARY_NUMBER, cords))
            } else {
                newItemList.add(NewItem(PRIMARY_NUMBER, cords))
            }
        }
        return newItemList.toList()
    }
}