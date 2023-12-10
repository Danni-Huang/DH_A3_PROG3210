package com.example.dh_a3_prog3210
import androidx.lifecycle.ViewModel
class HighscoreViewModel: ViewModel() {
    private var playerTopScores = HighscoreModel().playerTopScores

    fun updatePlayerScore(player: String, score: Int) {
        // If player name is not enter, do not update player final score for rendering on score board
        if (player == "") {
            return
        }

        val newPlayerScore = player to score

        val lowestScoreIndex = playerTopScores.indexOf(playerTopScores.minByOrNull { it.second }!!)

        if (score >= playerTopScores[lowestScoreIndex].second) {
            playerTopScores[lowestScoreIndex] = newPlayerScore
        }
        playerTopScores.sortByDescending { it.second }
    }

    fun getPlayerScores(): MutableList<Pair<String, Int>> {
        return playerTopScores
    }
}
