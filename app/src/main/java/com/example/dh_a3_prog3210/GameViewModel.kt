package com.example.dh_a3_prog3210

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dh_a3_prog3210.GameModel.Tile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Dictionary
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _tiles = mutableListOf<GameModel.Tile>()
    private val _generatedPositions =  mutableListOf<Pair<Int, Int>>()
    private val _clickedPositions =  mutableListOf<Pair<Int, Int>>()
    private val _gridSize = 6
    private var _score = 0
    private var _roundNumber = 0

    fun generateTiles(): List<GameModel.Tile> {
        for (i in 0 until _gridSize) {
            for (j in 0 until _gridSize) {
                val tile = GameModel.Tile(
                    id = i * _gridSize + j,
                    row = i,
                    col = j,)
                _tiles.add(tile)
            }
        }

        return _tiles
    }

    fun getTiles(): List<GameModel.Tile> {
        return _tiles
    }

    fun clearTileHighlights(): List<GameModel.Tile> {
        for (tile in _tiles) {
            tile.clicked = false
            tile.currentImage = tile.imageResource
        }
        return _tiles
    }

    fun clearAllPositions() {
        clearTileHighlights()
        _generatedPositions.clear()
        _clickedPositions.clear()

    }

    fun clearRound() {
        _roundNumber = 0
    }

    fun getCorrectPositions(): List<Pair<Int, Int>> {
        return _generatedPositions
    }

    fun generateRandomPositions(numPositionsToHighlight: Int = 4): List<Pair<Int, Int>> {
        while (_generatedPositions.size < numPositionsToHighlight) {
            val randomRow = Random.nextInt(_gridSize)
            val randomCol = Random.nextInt(_gridSize)
            val newPosition = Pair(randomRow, randomCol)

            if (!_generatedPositions.contains(newPosition)) {
                _generatedPositions.add(newPosition)
            }
        }

        return _generatedPositions
    }

    fun markTilesAsHighlighted(positions: List<Pair<Int, Int>>) {
        val highlightedPositions = mutableListOf<Int>()
        for (position in positions) {
            val tileId = position.first * _gridSize + position.second
            highlightedPositions.add(tileId)
        }

        for (tile in _tiles) {
            val tileId = tile.id
            if (highlightedPositions.contains(tileId)) {
                tile.clicked = true
                tile.currentImage = tile.highlightedImageResource
            }
        }
    }

    private fun markTileAsHighlighted(position: Pair<Int, Int>) {
        val positionTileId = position.first * _gridSize + position.second
        for (tile in _tiles) {
            if (tile.id == positionTileId) {
                tile.clicked = true
                tile.currentImage = tile.highlightedImageResource
            }
        }
    }

    fun getRoundNumber(): Int {
        return _roundNumber
    }

    fun isGameWin(): Boolean {
        val userSelection = _clickedPositions.toSet()
        val correctAnswer = _generatedPositions.toSet()
        if (userSelection == correctAnswer) {
            _roundNumber += 1
            return true
        }
        return false
    }

    fun onTileClick(tile: Tile): Boolean {
        // Check if the clicked tile was part of the initially highlighted positions
        val newTile = !_clickedPositions.contains(Pair(tile.row, tile.col))
        if (newTile) {
            markTileAsHighlighted(Pair(tile.row, tile.col))
            if (_generatedPositions.contains(Pair(tile.row, tile.col))) {
                _clickedPositions += Pair(tile.row, tile.col)
            } else {
                return false
            }
        }
        return true
    }

    fun getHighLightTileCountByRoundNumber(): Int {
        if (_roundNumber >= 3) {
             return 5
        }
        return 4
    }
}
