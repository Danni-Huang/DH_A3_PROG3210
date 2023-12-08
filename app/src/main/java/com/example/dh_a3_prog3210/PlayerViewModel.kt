package com.example.dh_a3_prog3210
import androidx.lifecycle.ViewModel

class PlayerViewModel: ViewModel() {
    private var playerModel = PlayerModel()

    fun enterName(name: String) {
        playerModel.playerName = name
    }

    fun addScore(score: Int) {
        playerModel.playerScore += score
    }

    fun name(): String {
        return playerModel.playerName
    }

    fun score(): Int {
        return playerModel.playerScore
    }

    fun clearScore() {
        playerModel.playerScore = 0
    }
}