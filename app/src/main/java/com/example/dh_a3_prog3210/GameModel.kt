package com.example.dh_a3_prog3210

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.random.Random



class GameModel {

    data class Tile(
        val id: Int,
        val originalImageResource: Int,
        val highlightedImageResource: Int,
        val row: Int,
        val col: Int,
        var isHighlighted: Boolean = false,
        var clicked: Boolean
    ) {
        override fun equals(other: Any?): Boolean {
            return if (this === other) true
            else (other is Tile) && (id == other.id)
        }

        override fun hashCode(): Int {
            return id
        }
    }

    private val _highlightedPositionsLiveData = MutableLiveData<List<Pair<Int, Int>>>()
    val highlightedPositionsLiveData: LiveData<List<Pair<Int, Int>>> get() = _highlightedPositionsLiveData

    val gridSize = 6

    fun generateRandomPositions(): List<Pair<Int, Int>> {
        val numPositionsToHighlight = 4
        val positions = mutableListOf<Pair<Int, Int>>()

        while (positions.size < numPositionsToHighlight) {
            val randomRow = Random.nextInt(gridSize)
            val randomCol = Random.nextInt(gridSize)
            val newPosition = Pair(randomRow, randomCol)

            if (!positions.contains(newPosition)) {
                positions.add(newPosition)
            }
        }

        return positions
    }
}
