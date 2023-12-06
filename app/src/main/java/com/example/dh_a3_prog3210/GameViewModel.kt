package com.example.dh_a3_prog3210

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dh_a3_prog3210.GameModel.Tile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _originalTiles = MutableLiveData<List<GameModel.Tile>>()
    private val _highlightedPositionsLiveData = MutableLiveData<List<Pair<Int, Int>>>()

    val originalTiles: LiveData<List<GameModel.Tile>> get() = _originalTiles
    val highlightedPositionsLiveData: LiveData<List<Pair<Int, Int>>> get() = _highlightedPositionsLiveData

    private val gridSize = 6

    private val _clickedTiles = MutableLiveData<List<Tile>>()
    val clickedTiles: LiveData<List<Tile>> get() = _clickedTiles

    private var score: Int = 0

    private val _showWrongMessage = MutableLiveData<Unit>()
    val showWrongMessage: LiveData<Unit> get() = _showWrongMessage

    init {
        _originalTiles.value = generateTiles()
    }

    var clickedPositions: List<Pair<Int, Int>> = emptyList()
    private val _tileHighlightLiveData = MutableLiveData<List<Pair<Int, Int>>>()
    val tileHighlightLiveData: LiveData<List<Pair<Int, Int>>> get() = _tileHighlightLiveData

    private val initiallyHighlightedTiles = mutableListOf<Pair<Int, Int>>()

    private val _updateTileUILiveData = MutableLiveData<Tile>()
    val updateTileUILiveData: LiveData<Tile> get() = _updateTileUILiveData

    val playerName: String = ""

    fun updateHighlightedPositions(positions: List<Pair<Int, Int>>) {
        _highlightedPositionsLiveData.value = positions
    }

    fun generateTiles(): List<GameModel.Tile> {
        val rows = 6
        val columns = 6

        val generatedTiles = mutableListOf<GameModel.Tile>()

        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val tile = GameModel.Tile(
                    id = i * columns + j,
                    originalImageResource = R.drawable.blue_grid,
                    highlightedImageResource = R.drawable.green_grid,
                    row = i,
                    col = j,
                    isHighlighted = false,
                    clicked = false
                )
                generatedTiles.add(tile)
            }
        }

        return generatedTiles
    }

    private val _highlightedTilePositions = MutableLiveData<List<Pair<Int, Int>>>()
    val highlightedTilePositions: LiveData<List<Pair<Int, Int>>> get() = _highlightedTilePositions

    fun highlightRandomTiles() {
        Log.d("GameViewModel", "Highlighting random tiles")
        val randomPositions = GameModel().generateRandomPositions()

        _highlightedTilePositions.value = randomPositions

        for (position in randomPositions) {
            val tile = getTileAtPosition(position.first, position.second)
            tile?.isHighlighted = true
        }

        _highlightedPositionsLiveData.value = randomPositions
    }

    fun highlightExistingTiles() {
        Log.d("GameViewModel", "Game end, show the existing tiles")
        _highlightedPositionsLiveData.value?.let { positions ->
            for (position in positions) {
                val tile = getTileAtPosition(position.first, position.second)
                tile?.isHighlighted = true
            }
        }
    }

    fun highlightSelectedTile(row: Int, col: Int) {
        val tile = getTileAtPosition(row, col)
        tile?.isHighlighted = true
    }

    fun hideRandomTiles() {
        _highlightedTilePositions.value?.let { positions ->
            for (position in positions) {
                val tile = getTileAtPosition(position.first, position.second)
                tile?.isHighlighted = false
            }
        }
    }

    fun getTileAtPosition(row: Int, col: Int): GameModel.Tile? {
        val tiles = _originalTiles.value
        return if (row in 0 until gridSize && col in 0 until gridSize && tiles != null) {
            tiles[row * gridSize + col]
        } else {
            null
        }
    }

    fun resetInitiallyHighlightedTiles() {
        initiallyHighlightedTiles.clear()
        _tileHighlightLiveData.value = emptyList()
    }


    fun resetHighlightedTiles() {
        Log.d("GameViewModel", "About to reset highlighted tiles")
        _highlightedTilePositions.value?.let { positions ->
            for (position in positions) {
                val tile = getTileAtPosition(position.first, position.second)
                tile?.isHighlighted = false
                Log.d("GameViewModel", "Tile at position (${position.first}, ${position.second}) is no longer highlighted: $tile")
            }
        }

        // Update the UI to reset highlighted tiles
        _originalTiles.value = _originalTiles.value.orEmpty().map { it.copy(isHighlighted = false) }

        // Do not update the highlighted positions here
        // updateHighlightedPositions(emptyList())

        Log.d("GameViewModel", "Highlighted tiles reset completed")
    }

    fun isWin(): Boolean {
        val userSelection = clickedPositions.toSet()
        val correctAnswer = _highlightedPositionsLiveData.value!!.toSet()
        return userSelection == correctAnswer
    }

    fun onTileClicked(clickedTile: Tile): Boolean {
        // Check if the clicked tile was part of the initially highlighted positions
        val wasInitiallyHighlighted = _highlightedPositionsLiveData.value!!.contains(Pair(clickedTile.row, clickedTile.col))

        if (!wasInitiallyHighlighted) {
            return false
        }

        clickedPositions += Pair(clickedTile.row, clickedTile.col)

        return true
    }

    fun getTotalScore(): Int {
        return score
    }

    fun addScore(newScore: Int) {
        score += newScore
    }
}
