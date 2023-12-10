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
    private var winRoundNumber = 0
    private var gameContinue = true
    private var timeUp = true

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
        tvTiles = view.findViewById(R.id.tvTiles)

        val playerViewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)
        val highscoreViewModel = ViewModelProvider(requireActivity()).get(HighscoreViewModel::class.java)

        gridLayout.removeAllViews()

        val imageSize = 160
        val marginSize = 4

        val tiles = gameViewModel.generateTiles()

        for (tile in tiles) {
            val imageView = ImageView(context)
            imageView.layoutParams = GridLayout.LayoutParams().apply {
                width = imageSize
                height = imageSize
                setMargins(marginSize, marginSize, marginSize, marginSize)
                rowSpec = GridLayout.spec(tile.row)  // Assuming id is unique for each tile
                columnSpec = GridLayout.spec(tile.col)
            }

            imageView.setImageResource(tile.currentImage)
            // Add the imageView to your grid layout
            gridLayout.addView(imageView)

            imageView.setOnClickListener {
                if (gameOngoing) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val roundContinue = gameViewModel.onTileClick(tile)
                        updateTileUI(tile)
                        if (roundContinue && gameViewModel.isGameWin()) {
                            gameOngoing= false
                            gameContinue = true
                            winRoundNumber += 1
                            Toast.makeText(requireContext(), "Great! Continue to next round!", Toast.LENGTH_LONG).show()
                            val score = gameViewModel.getScore()
                            renderScore(score.toString())
                            delay(500)
                            gameViewModel.clearAllPositions()
                        }

                        if (!roundContinue){
                            gameOngoing= false
                            gameContinue = false
                            timeUp = false
                            renderLost()
                            val score = gameViewModel.getScore()
                            playerViewModel.addScore(score)
                            highscoreViewModel.updatePlayerScore(playerViewModel.name(), playerViewModel.score())
                            delay(2000)

                            clearGame()
                            renderScore(gameViewModel.getScore().toString())
                        }
                    }
                }
            }
        }

        // Set click listener for the "Start Game" button
        startGameButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                startGameButton.isEnabled = false
                gameContinue = true
                while (gameContinue) {
                    gameOngoing = false
                    val highlightTileCount = gameViewModel.getHightTilesByRoundNumber()
                    renderTileCount(highlightTileCount.toString())
                    gameContinue = false
                    val positions = gameViewModel.generateRandomPositions(highlightTileCount)
                    gameViewModel.markTilesAsHighlighted(positions)
                    val allTiles = gameViewModel.getTiles()

                    // Render highlighted tiles and wait 3 seconds
                    renderTiles(allTiles)
                    delay(3000)

                    // Set the game as ongoing to start the game.
                    gameOngoing = true
                    gameViewModel.clearTileHighlights()
                    renderTiles(allTiles)

                    // 5 Seconds for user to select tiles
                    delay(5000)
                }
                if (timeUp) {
                    renderLost()
                    delay(2000)
                    gameOngoing= false
                    timeUp = true
                    clearGame()
                    val score = gameViewModel.getScore()
                    renderScore(score.toString())
                    playerViewModel.addScore(score)
                    highscoreViewModel.updatePlayerScore(playerViewModel.name(), playerViewModel.score())
                }
                startGameButton.isEnabled = true
            }
        }
    }

    fun flashTiles(positions: List<Pair<Int, Int>> ) {
        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 1..4) {
                gameViewModel.markTilesAsHighlighted(positions)
                val allTiles = gameViewModel.getTiles()
                renderTiles(allTiles)
                delay(500)
                gameViewModel.clearTileHighlights()
                renderTiles(allTiles)
                delay(500)
            }
        }
    }

    private fun renderLost() {
        winRoundNumber = 0

        val correctPositions = gameViewModel.getCorrectPositions().map { it.copy() }

        gameViewModel.clearAllPositions()
        val allTiles = gameViewModel.getTiles()
        renderTiles(allTiles)

        Toast.makeText(requireContext(), "Sorry! You lost.", Toast.LENGTH_LONG).show()
        flashTiles(correctPositions)
    }

    private fun clearGame() {
        gameViewModel.clearScoresAndRound()
        gameViewModel.clearAllPositions()
        renderTileCount(gameViewModel.getHightTilesByRoundNumber().toString())
        val allTiles = gameViewModel.getTiles()
        renderTiles(allTiles)
    }


    private fun renderTileCount(number: String) {
        tvTiles.text = "Tiles: ${number}"
    }

    private fun renderScore(score: String) {
        tvScore.text = "Score: ${score}"
    }


    private fun renderTiles(tiles: List<GameModel.Tile>) {
        for (tile in tiles) {
            updateTileUI(tile)
        }
    }

    private fun updateTileUI(tile: GameModel.Tile) {
        val imageView = gridLayout.getChildAt(tile.id) as? ImageView
        imageView?.let {
            viewLifecycleOwner.lifecycleScope.launch {
                it.setImageResource(tile.currentImage)
            }
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
        gridLayout = fragmentView.findViewById(R.id.gridLayout)

        val imageSize = 160

        // Initialize TextViews
        tvTiles = fragmentView.findViewById(R.id.tvTiles)
        tvScore = fragmentView.findViewById(R.id.tvScore)
        renderTileCount(gameViewModel.getHightTilesByRoundNumber().toString())

        // Inflate the layout for this fragment
        return fragmentView
    }
}
