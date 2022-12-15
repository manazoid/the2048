package com.example.the2048.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameResult(
    val time: String,
    val steps: Int,
    val winner: Boolean,
): Parcelable
