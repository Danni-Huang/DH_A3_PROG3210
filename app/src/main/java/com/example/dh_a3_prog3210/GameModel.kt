package com.example.dh_a3_prog3210

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.random.Random



class GameModel {

    class Tile(id: Int, row: Int, col: Int) {
        var id: Int = id
        val imageResource: Int = R.drawable.blue_grid
        val highlightedImageResource: Int = R.drawable.green_grid
        var currentImage: Int
        var clicked: Boolean
        var row: Int = row
        var col: Int = col

        init {
            currentImage = R.drawable.blue_grid
            clicked = false
        }
    }
}
