package com.example.dominobackgammonclient.ui

import androidx.lifecycle.ViewModel
import com.example.dominobackgammonclient.client.ClientThread
import com.example.dominobackgammonclient.client.pojo.*
import com.example.dominobackgammonclient.game.common.Game
import com.example.dominobackgammonclient.game.common.Player
import com.example.dominobackgammonclient.game.dominoes.Hand
import com.example.dominobackgammonclient.ui.common.BGColour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BGViewModel : ViewModel() {

    private val address = "132.226.211.91"

    private val _gameState: MutableStateFlow<Game> = MutableStateFlow(Game(BGColour.WHITE)) // temp init as white
    val gameState: StateFlow<Game> = _gameState.asStateFlow()

    // initialise client thread
    private lateinit var _client: ClientThread

    private val _uiState = MutableStateFlow(UIState(colourScheme = 1)) // TODO change colour scheme
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
        if (_uiState.value.playerName == "")
            _uiState.update { currentState ->
                currentState.copy(playerName = currentState.nameDefault)
            }

        // get opponent type
        val opponent: String
        if (!_uiState.value.aiOpponent) {
            opponent = if (_uiState.value.opponentName == "") _uiState.value.opponentDefault
            else "name:" + _uiState.value.opponentName
        } else {
            opponent = "ai:" + _uiState.value.aiDefault
        }

        m.connect = Connect(_uiState.value.playerName, opponent)
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

    fun updatePlayerName(newName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                playerName = newName
            )
        }
    }

    fun updateOpponentName(newName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                opponentName = newName
            )
        }
    }

    fun updateAIOpponent(isAI: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                aiOpponent = isAI
            )
        }
    }


    fun updateAIType(type: String) {
        _uiState.update { currentState ->
            currentState.copy(
                aiType = type
            )
        }
    }


    // starting game
    fun startGame(clientColour: PlayerPojo, opponentName: String) {
        if (clientColour == PlayerPojo.White) _gameState.update { Game(BGColour.WHITE) }
        else if (clientColour == PlayerPojo.Black) _gameState.update { Game(BGColour.BLACK) }

        _uiState.update { currentState ->
            currentState.copy(
                started = true,
                opponentName = opponentName
            )
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

        // get player
        val colour = if (turn.player == PlayerPojo.White) BGColour.WHITE else BGColour.BLACK
        val player = if (colour == _gameState.value.getColour(Player.Client)) Player.Client else Player.Opponent

        // use dominoes
        if (turn.dominoes.size == 1) {
            val dom = turn.dominoes[0]
            _gameState.value.useDomino(dom.side1, dom.side2, player)
        } else {
            val dom: DominoPojo; val dbl: DominoPojo
            if (turn.dominoes[0].side1 == turn.dominoes[0].side2) {
                dom = turn.dominoes[1]; dbl = turn.dominoes[0]
            } else {
                dom = turn.dominoes[0]; dbl = turn.dominoes[1]
            }
            _gameState.value.useDomino(dbl.side1, dom.side1, dom.side2, player)
        }

        // apply moves to server board
        for (move in turn.moves)
            _gameState.value.makeServerMove(move.start, move.end, player)
    }


    // resetting if game is inconsistent
    fun checksum(): String {
        return _gameState.value.checksum() ?: ""
    }

    fun resetGame(reset: Reset) {
        // reset game details
        _gameState.value.currentPlayer = reset.player
        _gameState.value.setTurnCount(reset.turnCount)

        // reset pieces
        val whitePieces = if (reset.pieces[0].colour == PlayerPojo.White) reset.pieces[0]
        else reset.pieces[1]
        val blackPieces = if (reset.pieces[0].colour == PlayerPojo.Black) reset.pieces[0]
        else reset.pieces[1]
        _gameState.value.setServerBoard(whitePieces.indices, blackPieces.indices)

        // reset dominoes
        val whiteHandPojo = if (reset.hands[0].colour == PlayerPojo.White) reset.hands[0]
        else reset.hands[1]
        val whiteHand = Hand(whiteHandPojo.set)
        if (reset.isSwapped) {
            // swaps twice to be sure doubles are in the right state
            whiteHand.swapDominoSet()
            whiteHand.swapDominoSet()
        }
        for (dom in whiteHandPojo.dominoes) {
            if (!dom.isAvailable) {
                if (dom.side1 != dom.side2)
                    whiteHand.useDomino(dom.side1,dom.side2)
                else whiteHand.useDouble(dom.side1)
            }
        }
        _gameState.value.setHand(whiteHand, BGColour.WHITE)

        val blackHandPojo = if (reset.hands[0].colour == PlayerPojo.Black) reset.hands[0]
        else reset.hands[1]
        val blackHand = Hand(blackHandPojo.set)
        if (reset.isSwapped) {
            // swaps twice to be sure doubles are in the right state
            blackHand.swapDominoSet()
            blackHand.swapDominoSet()
        }
        for (dom in blackHandPojo.dominoes) {
            if (!dom.isAvailable) {
                if (dom.side1 != dom.side2)
                    blackHand.useDomino(dom.side1,dom.side2)
                else blackHand.useDouble(dom.side1)
            }
        }
        _gameState.value.setHand(blackHand, BGColour.BLACK)
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

    fun swapHands() {
        val tempGame = Game(_gameState.value)
        tempGame.swapHands()
        _gameState.update { tempGame }
    }


    // ending a game
    fun gameOver() {
        _uiState.update { currentState ->
            currentState.copy(gameOver = true)
        }
    }
    fun disconnect(colour: BGColour) {
        if(colour == _gameState.value.getColour(Player.Client))
            _uiState.update { currentState ->
                currentState.copy(clientDisconnect = true)
            }
        else
            _uiState.update { currentState ->
                currentState.copy(opponentDisconnect = true)
            }
    }

    fun disconnect() {
        // default assumes client disconnect
        _uiState.update { currentState ->
            currentState.copy(clientDisconnect = true)
        }
    }

    fun win(colour: BGColour, type: Int) {
        if(colour == _gameState.value.getColour(Player.Client))
            _uiState.update { currentState ->
                currentState.copy(clientWin = true, winType = type)
            }
        else
            _uiState.update { currentState ->
                currentState.copy(opponentWin = true, winType = type)
            }
    }

    fun startOver() {
        // completely resets ui state
        _uiState.update {
            UIState(
                colourScheme = _uiState.value.colourScheme,
                playerName = _uiState.value.playerName,
                opponentName = _uiState.value.opponentName,
                aiType = _uiState.value.aiType,
                aiOpponent = _uiState.value.aiOpponent
            )
        }
    }
}