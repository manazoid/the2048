package com.example.the2048.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.the2048.data.GameRepositoryImpl

class GameViewModel(
    val application: Application
): ViewModel() {
    private val repository = GameRepositoryImpl

}