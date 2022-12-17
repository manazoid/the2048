package com.example.the2048.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Direction: Parcelable {

    LEFT,RIGHT,UP,DOWN
}