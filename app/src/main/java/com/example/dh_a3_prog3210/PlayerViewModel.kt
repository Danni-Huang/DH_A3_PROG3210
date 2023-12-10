package com.example.dh_a3_prog3210
import androidx.lifecycle.ViewModel

class PlayerViewModel: ViewModel() {
    private var playerModel = PlayerModel()

    fun enterName(name: String) {
        playerModel.playerName = name
    }

    fun addScore(roundNumber: Int) {
        if (roundNumber > 3) {
            playerModel.playerScore += 20
            return
        }
        playerModel.playerScore += 10
        return
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