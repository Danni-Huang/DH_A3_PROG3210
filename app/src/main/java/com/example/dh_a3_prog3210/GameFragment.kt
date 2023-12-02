package com.example.dh_a3_prog3210

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider


class GameFragment : Fragment() {

    private lateinit var tvTiles: TextView
    private lateinit var tvScore: TextView
    private lateinit var gameViewModel: GameViewModel
    private lateinit var gridLayout: GridLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        gameViewModel.generateTiles()

        gridLayout = view.findViewById(R.id.gridLayout)
        gameViewModel.originalTiles.observe(viewLifecycleOwner) { tiles ->
            // Call the method to display the original tiles
            displayOriginalTiles(tiles)
        }
    }

    private fun displayOriginalTiles(tiles: List<Tile>) {
        gridLayout.removeAllViews()

        // Set the number of rows and columns
        val rows = 6
        val columns = 6
        gridLayout.rowCount = rows
        gridLayout.columnCount = columns

        val imageSize = 160

        // Calculate the margin size
        val marginSize = 4

        // Loop through to create and add ImageViews
        for (tile in tiles) {
            val imageView = ImageView(requireContext())
            imageView.layoutParams = GridLayout.LayoutParams().apply {
                width = imageSize
                height = imageSize
                setMargins(marginSize, marginSize, marginSize, marginSize)
            }

            imageView.setImageResource(tile.originalImageResource)

            // Add the ImageView to the GridLayout
            gridLayout.addView(imageView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_game, container, false)
  /*      val gridLayout = fragmentView.findViewById<GridLayout>(R.id.gridLayout)

        // Set the number of rows and columns
        val rows = 6
        val columns = 6
        gridLayout.rowCount = rows
        gridLayout.columnCount = columns*/

        val imageSize = 160

        // Initialize TextViews
        tvTiles = fragmentView.findViewById(R.id.tvTiles)
        tvScore = fragmentView.findViewById(R.id.tvScore)

        // Update TextViews
        // tvTiles.text = "Tiles: 5"
        // tvScore.text = "Score: $updatedScore"

        // Calculate the margin size
/*        val marginSize = 4

        // Loop through to create and add ImageViews
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val imageView = ImageView(requireContext())
                imageView.layoutParams = GridLayout.LayoutParams().apply {
                    width = imageSize
                    height = imageSize
                    setMargins(marginSize, marginSize, marginSize, marginSize)
                }

                imageView.setImageResource(R.drawable.blue_grid)

                // Add the ImageView to the GridLayout
                gridLayout.addView(imageView)
            }
        }*/

        // Inflate the layout for this fragment
        return fragmentView
    }

    private fun updateHighlightedTilesUI(highlightedTiles: List<Tile>) {
        // Implement UI update logic to highlight the specified tiles
        // For example, change the background color of the corresponding views
    }
}