package com.example.dominobackgammonclient.ui

import androidx.lifecycle.ViewModel
import com.example.dominobackgammonclient.game.board.Board
import com.example.dominobackgammonclient.game.common.Game
import com.example.dominobackgammonclient.game.common.Player
import com.example.dominobackgammonclient.game.dominoes.Domino
import com.example.dominobackgammonclient.game.dominoes.Hand
import com.example.dominobackgammonclient.ui.common.BGColour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BGViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(Game(BGColour.WHITE)) // TODO hard-coded to white
    val gameState: StateFlow<Game> = _gameState.asStateFlow()

    fun selectDomino(side1: Int, side2: Int) {
        val tempGame = Game(_gameState.value)
        tempGame.selectDomino(side1, side2)
        _gameState.update { tempGame }
    }

    init {
        // for test demonstration purposes: remove!
        _gameState.value.useDomino(6, 4, Player.Client)
        _gameState.value.getHand(Player.Client).nextDouble.unblock()


        gameState.value.generateValidMoves()
    }
}