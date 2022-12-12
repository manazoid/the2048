package com.example.the2048.domain.entity

data class GameResult(
    val time: String,
    val steps: Int,
    val winner: Boolean,
)
