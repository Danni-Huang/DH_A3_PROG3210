package com.example.dh_a3_prog3210
import androidx.lifecycle.ViewModel
class HighscoreModel{
    var playerTopScores: MutableList<Pair<String, Int>>

    init {
        playerTopScores = mutableListOf(
            "No Player" to 0,
            "No Player" to 0,
            "No Player" to 0
        )
    }
}
