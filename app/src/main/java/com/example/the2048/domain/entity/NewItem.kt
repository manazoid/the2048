package com.example.the2048.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewItem (
    val number: Int,
    val coordinates: List<Int>,
): Parcelable