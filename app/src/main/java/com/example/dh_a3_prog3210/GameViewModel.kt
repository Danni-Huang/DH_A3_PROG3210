package com.example.dh_a3_prog3210

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _originalTiles = MutableLiveData<List<Tile>>()
    val originalTiles: LiveData<List<Tile>> get() = _originalTiles

    // Example: fun startTileHighlighting()
    // Example: fun onTileClicked(tile: Tile)

    private val _tiles = MutableLiveData<List<Tile>>()
    val tiles: LiveData<List<Tile>> get() = _tiles


    // Function to generate tiles
    fun generateTiles() {
        val rows = 6
        val columns = 6

        val generatedTiles = mutableListOf<Tile>()

        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val tile = Tile(
                    id = i * columns + j,
                    originalImageResource = R.drawable.blue_grid,
                    highlightedImageResource = R.drawable.green_grid
                )
                generatedTiles.add(tile)
            }
        }

        // Update LiveData with the generated tiles
        _tiles.value = generatedTiles
        _originalTiles.value = generatedTiles
    }
}
