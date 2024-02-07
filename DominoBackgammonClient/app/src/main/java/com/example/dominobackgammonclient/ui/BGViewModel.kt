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

    private val _gameState: MutableStateFlow<Game> = MutableStateFlow(Game(BGColour.WHITE)) // temp init as white
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

    fun applyTurn(turn: TurnPojo) {
        // applies a turn to the server board (updates client board on next turn)

        val tempGame = Game(_gameState.value)

        // get player
        val colour = if (turn.player == PlayerPojo.White) BGColour.WHITE else BGColour.BLACK
        val player = if (colour == _gameState.value.getColour(Player.Client)) Player.Client else Player.Opponent

        // use dominoes
        if (turn.dominoes.size == 1) {
            val dom = turn.dominoes[0]
            tempGame.useDomino(dom.side1, dom.side2, player)
        } else {
            val dom: DominoPojo; val dbl: DominoPojo
            if (turn.dominoes[0].side1 == turn.dominoes[0].side2) {
                dom = turn.dominoes[1]; dbl = turn.dominoes[0]
            } else {
                dom = turn.dominoes[0]; dbl = turn.dominoes[1]
            }
            tempGame.useDomino(dbl.side1, dom.side1, dom.side2, player)
        }

        // apply moves to server board
        for (move in turn.moves)
            tempGame.makeServerMove(move.start, move.end, player)
    }

    fun checksum(): String {
        return _gameState.value.checksum() ?: ""
    }



    // changing to next turn
    fun nextTurn() {
        val tempGame = Game(_gameState.value)
        tempGame.nextTurn()
        _gameState.update { tempGame }

        _uiState.update { currentState ->
            currentState.copy(waiting = false)
        }

        _gameState.value.generateValidMoves()
    }


    init {
        _gameState.value.generateValidMoves()
    }
}