package com.example.the2048.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewItem (
    val number: Int,
    val coordinates: List<Int>,
): Parcelable {

    companion object {

        const val EMPTY_ITEM = 0
        const val PRIMARY_NUMBER = 2
        const val SECONDARY_NUMBER = 4
    }
}