package com.example.dominobackgammonclient.ui

import androidx.lifecycle.ViewModel
import com.example.dominobackgammonclient.client.ClientThread
import com.example.dominobackgammonclient.client.pojo.Connect
import com.example.dominobackgammonclient.client.pojo.Message
import com.example.dominobackgammonclient.client.pojo.PlayerPojo
import com.example.dominobackgammonclient.game.common.Game
import com.example.dominobackgammonclient.ui.common.BGColour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BGViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(Game(BGColour.WHITE)) // temporarily initialised to white
    val gameState: StateFlow<Game> = _gameState.asStateFlow()

    // initialise client thread
    private var _client: ClientThread = ClientThread("", this)

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

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



    fun sendConnect() {
        _uiState.update { currentState ->
            currentState.copy(connecting = true)
        }
        _client = ClientThread("192.168.1.78", this)
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


    fun startGame(clientColour: PlayerPojo, opponentName: String) {
        if (clientColour == PlayerPojo.White) _gameState.update { Game(BGColour.WHITE) }
        else if (clientColour == PlayerPojo.Black) _gameState.update { Game(BGColour.BLACK) }

        _uiState.update { currentState ->
            currentState.copy(started = true)
        }

        _gameState.value.generateValidMoves()
    }


    init {

        // for test demonstration purposes: remove!
//        _gameState.value.useDomino(6, 4, Player.Client)
//        _gameState.value.getHand(Player.Client).nextDouble.unblock()
//
//        _gameState.value.board.hitPiece(6, Player.Client)

//        _gameState.value.board.movePiece(8, 4, Player.Client);
//        _gameState.value.board.movePiece(8, 4, Player.Client);
//        _gameState.value.board.movePiece(8, 4, Player.Client);
//
//        _gameState.value.board.movePiece(13, 5, Player.Client);
//        _gameState.value.board.movePiece(13, 5, Player.Client);
//        _gameState.value.board.movePiece(13, 5, Player.Client);
//        _gameState.value.board.movePiece(13, 5, Player.Client);
//        _gameState.value.board.movePiece(13, 5, Player.Client);
//
//        _gameState.value.board.movePiece(24, 3, Player.Client);
//        _gameState.value.board.movePiece(24, 3, Player.Client);

        _gameState.value.generateValidMoves()
    }
}