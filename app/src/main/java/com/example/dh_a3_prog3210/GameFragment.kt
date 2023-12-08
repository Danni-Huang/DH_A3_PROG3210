package com.example.dh_a3_prog3210

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private lateinit var tvTiles: TextView
    private lateinit var tvScore: TextView
    private lateinit var gridLayout: GridLayout
    private lateinit var startGameButton: Button
    private lateinit var tvCountdown: TextView
    private var showResult = false
    private var gameOngoing= false
    private var blockStartButton = false

    private val gameViewModel by lazy {
        ViewModelProvider(this).get(GameViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridLayout = view.findViewById(R.id.gridLayout)
        startGameButton = view.findViewById(R.id.startGameButton)
        tvCountdown = view.findViewById(R.id.tvCountdown)
        tvScore = view.findViewById(R.id.tvScore)

        val playerViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)
        val highscoreViewModel = ViewModelProvider(requireActivity()).get(HighscoreViewModel::class.java)

        gridLayout.removeAllViews()

        val imageSize = 160
        val marginSize = 4
        var score: Int

        val tiles = gameViewModel.originalTiles.value ?: emptyList()

        for (tile in tiles) {
            val imageView = ImageView(context)
            imageView.layoutParams = GridLayout.LayoutParams().apply {
                width = imageSize
                height = imageSize
                setMargins(marginSize, marginSize, marginSize, marginSize)
                rowSpec = GridLayout.spec(tile.id / 6)  // Assuming id is unique for each tile
                columnSpec = GridLayout.spec(tile.id % 6)
            }

            imageView.setImageResource(tile.originalImageResource)
            // Add the imageView to your grid layout
            gridLayout.addView(imageView)

            imageView.setOnClickListener {
                // Handle the click event by calling a method in the ViewModel
                if (gameOngoing) {
                    val gameContinue = gameViewModel.onTileClicked(tile)

                    if (gameContinue) {
                        for (position in gameViewModel.clickedPositions) {
                            gameViewModel.highlightSelectedTile(position.first, position.second)
                        }
                        highlightTiles(gameViewModel.clickedPositions)
                        Log.d("gameResult", "right click, game continue")
                    } else {
                        showResult = true
                        // Reset the countdown text and hide the TextView
                        tvCountdown.text = "You lose! Your total score is " + playerViewModel.score().toString()
                        if (playerViewModel.name() != "") {
                            highscoreViewModel.updatePlayerScore(playerViewModel.name(), playerViewModel.score())
                        }
                        playerViewModel.clearScore()
                        score = 0
                        tvScore.text = score.toString()
                        tvCountdown.visibility = View.VISIBLE
                        gameViewModel.clickedPositions = emptyList()
                        Log.d("gameResult", "wrong click, game stop")
                    }

                    val gameResult = gameViewModel.isWin()
                    if (gameResult) {
                        showResult = true
                        // Reset the countdown text and hide the TextView
                        tvCountdown.text = "You win!"
                        playerViewModel.addScore(10)
                        score = playerViewModel.score()
                        tvScore.text = score.toString()

                        tvCountdown.visibility = View.VISIBLE
                        gameViewModel.clickedPositions = emptyList()
                    }

                    Log.d("gameResult", gameResult.toString())
                }
            }
        }

        gameViewModel.originalTiles.observe(viewLifecycleOwner) { tiles ->
            // Update UI based on the changes in originalTiles
            // For example, iterate through tiles and update ImageView src or background color
            tiles.forEach { tile ->
                val imageView = getImageViewForTile(tile)
                if (imageView != null) {
                    imageView.setImageResource(tile.originalImageResource)
                }
            }
        }

        // Set click listener for the "Start Game" button
        startGameButton.setOnClickListener {
            if (!blockStartButton) {
                Log.d("GameStart", "Player ${playerViewModel.name()} starting the game!")
                blockStartButton = true
                showResult = false
                gameViewModel.resetHighlightedTiles()

                gameViewModel.highlightedTilePositions.value?.let { positions ->
                    highlightTiles(positions)
                }

                // Start the game by triggering the highlighting of tiles
                gameViewModel.highlightRandomTiles()

                // Display the countdown text
                tvCountdown.visibility = View.VISIBLE

                // Use a Coroutine to remove the initial highlight after 3 seconds
                viewLifecycleOwner.lifecycleScope.launch {
                    for (i in 3 downTo 1) {
                        tvCountdown.text = i.toString()
                        delay(1000)
                    }

                    gameViewModel.hideRandomTiles()

                    gameViewModel.highlightedTilePositions.value?.let { positions ->
                        highlightTiles(positions)
                    }

                    Log.d("GameFragment", "About to reset initially highlighted tiles")
                    // Reset the initial highlight
                    gameViewModel.resetInitiallyHighlightedTiles()

                    Log.d("GameAnswer", gameViewModel.highlightedPositionsLiveData.value.toString())

                    for (i in 6 downTo 1) {
                        if (!showResult) {
                            if (i == 6) {
                                tvCountdown.text = "Start guessing!"
                                gameOngoing = true
                                delay(1000)
                                continue
                            }
                            tvCountdown.text = i.toString()
                            delay(1000)
                        }
                    }

                    gameOngoing = false
                    blockStartButton = false

                    if (!showResult) {
                        // Reset the countdown text and hide the TextView
                        tvCountdown.text = ""
                        tvCountdown.visibility = View.INVISIBLE
                    }

                    gameViewModel.highlightExistingTiles()

                    gameViewModel.highlightedPositionsLiveData.value?.let { positions ->
                        highlightTiles(positions)
                    }
                }
            }
        }

        // Observe changes in highlighted positions
        gameViewModel.highlightedPositionsLiveData.observe(viewLifecycleOwner) { positions ->
            // Update UI based on the highlighted positions
            Log.d("GameFragment", "Observed highlighted positions: $positions")
            highlightTiles(positions)
        }

        Log.d("GameFragment", "Child count of gridLayout: ${gridLayout.childCount}")
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            Log.d("GameFragment", "Child $i type: ${child.javaClass.simpleName}")
        }

        gameViewModel.showWrongMessage.observe(viewLifecycleOwner) {
            Log.d("GameFragment", "Wrong message triggered")
            Toast.makeText(requireContext(), "Wrong", Toast.LENGTH_LONG).show()

            // Delay the reset to ensure the wrong message is displayed
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000) // Adjust the delay as needed
                gameViewModel.resetInitiallyHighlightedTiles()
            }
        }

        gameViewModel.updateTileUILiveData.observe(viewLifecycleOwner) { updatedTile ->
            // Update the UI for the specific tile (e.g., change the image resource)
            updateTileUI(updatedTile)
        }
    }

    private fun displayOriginalTiles(tiles: List<GameModel.Tile>) {
        // Set the number of rows and columns
        val rows = 6
        val columns = 6
        gridLayout.rowCount = rows
        gridLayout.columnCount = columns

        val imageSize = 160

        // Calculate the margin size
        val marginSize = 4

        // Iterate through existing child views
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i) as? ImageView
            child?.let {
                val tile = tiles[i % columns + (i / columns) * columns]
                it.layoutParams = GridLayout.LayoutParams().apply {
                    width = imageSize
                    height = imageSize
                    setMargins(marginSize, marginSize, marginSize, marginSize)
                }
                it.setImageResource(tile.originalImageResource)
            }
        }
    }

    private fun highlightTiles(positions: List<Pair<Int, Int>>) {
        for ((row, col) in positions) {
            val tile = gameViewModel.getTileAtPosition(row, col)
            tile?.let {
                // Update the UI based on the Tile data (e.g., change background color or image)
                updateTileUI(tile)
            }
        }
    }

    private fun updateTileUI(tile: GameModel.Tile) {
        Log.d("GameFragment", "Updating UI for tile ${tile.id}")
        val imageView = gridLayout.getChildAt(tile.id) as? ImageView
        imageView?.let {
            Log.d("GameFragment", "ImageView found for tile ${tile.id}")
            viewLifecycleOwner.lifecycleScope.launch {
                if (tile.isHighlighted) {
                    // If the tile is highlighted, use the highlightedImageResource
                    it.setImageResource(tile.highlightedImageResource)
                    Log.d("GameFragment", "Tile ${tile.id} is highlighted. Image resource set to green_grid.")
                } else {
                    // Otherwise, use the originalImageResource
                    it.setImageResource(tile.originalImageResource)
                    Log.d("GameFragment", "Tile ${tile.id} is not highlighted. Image resource set to blue_grid.")
                }
            }
        } ?: Log.e("GameFragment", "ImageView not found for tile ${tile.id}")
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
        gridLayout = fragmentView.findViewById(R.id.gridLayout)

        val imageSize = 160

        // Initialize TextViews
        tvTiles = fragmentView.findViewById(R.id.tvTiles)
        tvScore = fragmentView.findViewById(R.id.tvScore)

        // Inflate the layout for this fragment
        return fragmentView
    }

    private fun updateHighlightedTilesUI(highlightedTiles: List<GameModel.Tile>) {
        // Implement UI update logic to highlight the specified tiles
        // For example, change the background color of the corresponding views
    }

    private fun getImageViewForTile(tile: GameModel.Tile): ImageView? {
        // Implement the logic to find the ImageView for the given tile
        // You can use tags, IDs, or other mechanisms to identify ImageViews
        // Return the corresponding ImageView or null if not found
        // For example, you might have a list of ImageViews associated with each tile
        // and you can find the correct one based on the tile's position or ID.
        // ...

        return null
    }
}
