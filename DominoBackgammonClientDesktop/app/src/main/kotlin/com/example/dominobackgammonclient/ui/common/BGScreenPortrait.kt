package com.example.dominobackgammonclient.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.BGViewModel
import com.example.dominobackgammonclient.ui.UIState
import com.example.dominobackgammonclient.ui.board.Board
import com.example.dominobackgammonclient.ui.dominoes.DominoListHorizontal
import game.common.Game
import game.common.Player

@Composable
fun BGScreenPortrait(
    bgViewModel: BGViewModel,
    gameState: Game,
    uiState: UIState,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
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
            modifier = Modifier.weight(.1f).padding(bottom = 10.dp)
        )
    }
}