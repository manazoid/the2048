package com.example.the2048.domain.entity

import android.opengl.Matrix
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameField (
    val field: MutableList<MutableList<Int>>
): Parcelable
