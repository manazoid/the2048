package com.example.the2048.data

import android.util.Log
import com.example.the2048.domain.entity.Direction
import com.example.the2048.domain.entity.GameField
import com.example.the2048.domain.entity.NewItem
import com.example.the2048.domain.repository.GameRepository
import kotlin.random.Random

object GameRepositoryImpl : GameRepository {

    private const val PRIMARY_NUMBER = 2
    private const val SECONDARY_NUMBER = 4
    private const val MIN_COORD_INCLUSIVE = 0
    private const val MAX_COORD_EXCLUSIVE = 4
    private const val MIN_ITEM_ITERATION = 0
    private const val AVERAGE_ITEM_ITERATION = 1
    private const val MAX_ITEM_ITERATION = 2

    override fun generateNewItems(gameField: GameField): List<NewItem>? {
        val freeItemsIndexes = HashSet<List<Int>>()
        gameField.field.forEachIndexed { i, row ->
            row.forEachIndexed { k, item ->
                if (item == MIN_ITEM_ITERATION) {
                    val vector = HashSet<Int>()
                    vector.add(i)
                    vector.add(k)
                    freeItemsIndexes.add(vector.toList())
                }
            }
        }
        val newItemList = HashSet<NewItem>()
        if (freeItemsIndexes.isEmpty()) {
            return null
        }
        var freeItemsCount = when (freeItemsIndexes.size) {
            AVERAGE_ITEM_ITERATION -> AVERAGE_ITEM_ITERATION
            else -> MAX_ITEM_ITERATION
        }
        while (freeItemsCount != MIN_ITEM_ITERATION) {
            freeItemsCount--
            val chance = Random.nextFloat()
//            val x = Random.nextInt()
            if (chance >= .9) {
                Log.d("chance", chance.toString())
//                newItemList.add(NewItem(0, List<Int>(1)))
            }
        }
        return parseNewItemList(newItemList)
    }

    private fun parseNewItemList(newItemList: HashSet<NewItem>) =
        newItemList.toList()
}