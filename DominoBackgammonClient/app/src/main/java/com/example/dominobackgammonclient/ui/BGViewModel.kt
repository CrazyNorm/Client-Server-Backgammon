package com.example.dominobackgammonclient.ui

import androidx.lifecycle.ViewModel
import com.example.dominobackgammonclient.client.ClientThread
import com.example.dominobackgammonclient.client.pojo.*
import com.example.dominobackgammonclient.game.common.Game
import com.example.dominobackgammonclient.game.common.Player
import com.example.dominobackgammonclient.ui.common.BGColour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BGViewModel : ViewModel() {

    private val address = "192.168.1.78"

    private lateinit var _gameState: MutableStateFlow<Game>
    val gameState: StateFlow<Game> = _gameState.asStateFlow()

    // initialise client thread
    private lateinit var _client: ClientThread

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    // client side UI changes during turn
    fun selectDomino(side1: Int, side2: Int) {
        val tempGame = Game(_gameState.value)
        tempGame.selectDomino(side1, side2)
        _gameState.update { tempGame }
    }

    fun selectPiece(point: Int) {
        val tempGame = Game(_gameState.value)
        tempGame.selectPiece(point)
        _gameState.update { tempGame }
    }

    fun undoMove() {
        val tempGame = Game(_gameState.value)
        tempGame.undoMove()
        _gameState.update { tempGame }
    }


    // connecting to server
    fun sendConnect() {
        _uiState.update { currentState ->
            currentState.copy(connecting = true)
        }
        _client = ClientThread(address, this)
        _client.start()
        val m = Message()
        m.connect = Connect("test", "any")
        _client.queueMessage(m)
    }

    fun setConnected(con: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(connected = con)
        }
    }

    fun connectionFailed() {
        _uiState.update { currentState ->
            currentState.copy(
                connecting = false,
                connectionFailed = true
            )
        }
    }


    // starting game
    fun startGame(clientColour: PlayerPojo, opponentName: String) {
        println("opponent: $opponentName")
        if (clientColour == PlayerPojo.White) _gameState.update { Game(BGColour.WHITE) }
        else if (clientColour == PlayerPojo.Black) _gameState.update { Game(BGColour.BLACK) }

        _uiState.update { currentState ->
            currentState.copy(started = true)
        }

        _client.updateGame(_gameState.value)
        _gameState.value.generateValidMoves()
    }


    // sending / receiving turns
    fun sendTurn() {
        _uiState.update { currentState ->
            currentState.copy(waiting = true)
        }

        // get colour
        val colour = if (_gameState.value.getColour(Player.Client) == BGColour.WHITE) PlayerPojo.White
        else PlayerPojo.Black
        val turn = TurnPojo(colour)

        // add moves
        val moves = _gameState.value.moves
        for (move in moves) turn.addMove(MovePojo(move[0], move[1]))

        // add dominoes
        val dominoes = _gameState.value.selectedDominoes
        for (dom in dominoes)
            turn.addDomino(DominoPojo(
                dom.side1,
                dom.side2,
                dom.isAvailable
            ))

        // send message
        val m = Message()
        m.turn = turn
        _client.queueMessage(m)
    }

    fun turnDenied() {
        // todo: display failure message
        _uiState.update { currentState ->
            currentState.copy(waiting = false)
        }
    }


    init {
        _gameState.value.generateValidMoves()
    }
}