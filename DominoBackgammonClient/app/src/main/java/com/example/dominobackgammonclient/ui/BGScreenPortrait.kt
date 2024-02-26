package com.example.dominobackgammonclient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dominobackgammonclient.game.common.Player
import com.example.dominobackgammonclient.ui.board.Board
import com.example.dominobackgammonclient.ui.common.ControlButtons
import com.example.dominobackgammonclient.ui.common.PlayerStats
import com.example.dominobackgammonclient.ui.dominoes.DominoListHorizontal
import com.example.dominobackgammonclient.ui.overlays.ConnectOverlay
import com.example.dominobackgammonclient.ui.overlays.GameEndOverlay
import com.example.dominobackgammonclient.ui.overlays.WaitingOverlay

@Composable
fun BGScreenPortrait(
    bgViewModel: BGViewModel = viewModel()
) {
    val gameState by bgViewModel.gameState.collectAsState()
    val uiState by bgViewModel.uiState.collectAsState()


    // overlays
    if (!uiState.connected)
        ConnectOverlay(
            onClick = { bgViewModel.sendConnect() },
            connectionFailed = uiState.connectionFailed,
            disableButton = uiState.connecting,
            playerName = uiState.playerName,
            onNameChanged = { name -> bgViewModel.updatePlayerName(name) }
        )

    else if (!uiState.started)
        WaitingOverlay()

    else if (uiState.gameOver)
        GameEndOverlay(
            onClick = { bgViewModel.startOver() },
            clientWin = uiState.clientWin,
            clientLoss = uiState.opponentWin,
            winType = uiState.winType,
            clientDisconnect = uiState.clientDisconnect,
            opponentDisconnect = uiState.opponentDisconnect
        )


    // main screen
    else
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .padding(vertical = 10.dp)
                .statusBarsPadding()
                .windowInsetsPadding(WindowInsets.displayCutout)
        ) {

            DominoListHorizontal(
                colour = gameState.getColour(Player.Opponent),
                hand = gameState.getHand(Player.Opponent),
                onClick = { _, _ -> /* Do nothing for opponent hand */ },
                enable = false,
                modifier = Modifier.weight(.15f)
            )

            PlayerStats(
                colour = gameState.getColour(Player.Opponent),
                pipCount = gameState.board.getPipCount(Player.Opponent),
                offCount = gameState.board.getOffCount(Player.Opponent),
                modifier = Modifier
                    .weight(0.05f)
                    .padding(horizontal = 8.dp)
            )

            Board(
                board = gameState.board,
                client = gameState.getColour(Player.Client),
                opponent = gameState.getColour(Player.Opponent),
                highlightedPoints = gameState.highlightedMoves,
                onClickPoint = { p -> bgViewModel.selectPiece(p) },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .weight(.5f)
            )

            PlayerStats(
                colour = gameState.getColour(Player.Client),
                pipCount = gameState.board.getPipCount(Player.Client),
                offCount = gameState.board.getOffCount(Player.Client),
                offHighlight = gameState.highlightedMoves.contains(0),
                onClickOff = { bgViewModel.selectPiece(0) },
                modifier = Modifier
                    .weight(0.05f)
                    .padding(horizontal = 8.dp)
            )

            DominoListHorizontal(
                colour = gameState.getColour(Player.Client),
                hand = gameState.getHand(Player.Client),
                onClick = { s1, s2 -> bgViewModel.selectDomino(s1, s2) },
                enable = !uiState.waiting,
                modifier = Modifier.weight(.15f)
            )

            ControlButtons(
                onUndo = { bgViewModel.undoMove() },
                onSubmit = { bgViewModel.sendTurn() },
                enableUndo = gameState.isUndoable && !uiState.waiting,
                enableSubmit = gameState.isTurnValid && !uiState.waiting,
                modifier = Modifier.weight(.1f)
            )
        }
}