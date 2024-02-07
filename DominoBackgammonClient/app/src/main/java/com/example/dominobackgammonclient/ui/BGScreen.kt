package com.example.dominobackgammonclient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.example.dominobackgammonclient.ui.dominoes.DominoList
import com.example.dominobackgammonclient.ui.overlays.ConnectOverlay
import com.example.dominobackgammonclient.ui.overlays.WaitingOverlay
import com.example.dominobackgammonclient.ui.theme.DarkGreen
import com.example.dominobackgammonclient.ui.theme.DarkRed

@Composable
fun BGScreen(
    bgViewModel: BGViewModel = viewModel()
) {
    val gameState by bgViewModel.gameState.collectAsState()
    val uiState by bgViewModel.uiState.collectAsState()

    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(DarkGreen)
                .padding(vertical = 10.dp)
        ) {

            DominoList(
                colour = gameState.getColour(Player.Opponent),
                hand = gameState.getHand(Player.Opponent),
                onClick = { _, _ -> /* Do nothing for opponent hand */ },
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

            //        if (gameState.isPieceSelected)
            //            Board(
            //                board = gameState.board,
            //                client = gameState.getColour(Player.Client),
            //                opponent = gameState.getColour(Player.Opponent),
            //                highlightedPieces = gameState.highlightedMoves,
            //                onClickPoint = { p -> bgViewModel.selectPiece(p) },
            //                modifier = Modifier
            //                    .background(Color(0xFF673AB7))
            //                    .weight(.5f)
            //            )
            //        else
            Board(
                board = gameState.board,
                client = gameState.getColour(Player.Client),
                opponent = gameState.getColour(Player.Opponent),
                highlightedPoints = gameState.highlightedMoves,
                onClickPoint = { p -> bgViewModel.selectPiece(p) },
                modifier = Modifier
                    .background(DarkRed)
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

            DominoList(
                colour = gameState.getColour(Player.Client),
                hand = gameState.getHand(Player.Client),
                onClick = { s1, s2 -> bgViewModel.selectDomino(s1, s2) },
                modifier = Modifier.weight(.15f)
            )

            ControlButtons(
                onUndo = { bgViewModel.undoMove() },
                onSubmit = { bgViewModel.sendTurn() },
                enableUndo = gameState.isUndoable,
                enableSubmit = gameState.isTurnValid && !uiState.waiting,
                modifier = Modifier.weight(.1f)
            )
        }


        // overlays
        if (!uiState.connected)
            ConnectOverlay(
                onClick = { bgViewModel.sendConnect() },
                connectionFailed = uiState.connectionFailed,
                disableButton = uiState.connecting
            )

        else if (!uiState.started)
            WaitingOverlay()
    }
}