package com.example.dominobackgammonclient.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.game.common.Game
import com.example.dominobackgammonclient.game.common.Player
import com.example.dominobackgammonclient.ui.BGViewModel
import com.example.dominobackgammonclient.ui.UIState
import com.example.dominobackgammonclient.ui.board.Board
import com.example.dominobackgammonclient.ui.dominoes.DominoListVertical

@Composable
fun BGScreenLandscape(
    bgViewModel: BGViewModel,
    gameState: Game,
    uiState: UIState,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        DominoListVertical(
            colour = gameState.getColour(Player.Opponent),
            pipCount = gameState.board.getPipCount(Player.Opponent),
            hand = gameState.getHand(Player.Opponent),
            onClick = { _, _ -> /* Do nothing for opponent hand */ },
            enable = false,
            modifier = Modifier.weight(.15f)
        )

        Board(
            board = gameState.board,
            client = gameState.getColour(Player.Client),
            opponent = gameState.getColour(Player.Opponent),
            highlightedPoints = gameState.highlightedMoves,
            onClickPoint = { p -> bgViewModel.selectPiece(p) },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .weight(.55f)
        )

        // borne off sections
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(0.05f)
                .padding(vertical = 8.dp)
        ) {
            BorneOff(
                colour = gameState.getColour(Player.Opponent),
                count = gameState.board.getOffCount(Player.Opponent),
                modifier = Modifier.weight(0.5f).rotate(180f),
                rotate = true
            )
            var offMod = Modifier.weight(0.5f).clickable { bgViewModel.selectPiece(0) }
            if (gameState.highlightedMoves.contains(0)) offMod = offMod.border(5.dp, Color.Green)
            BorneOff(
                colour = gameState.getColour(Player.Client),
                count = gameState.board.getOffCount(Player.Client),
                modifier = offMod,
                rotate = true
            )
        }

        DominoListVertical(
            colour = gameState.getColour(Player.Client),
            hand = gameState.getHand(Player.Client),
            onClick = { s1, s2 -> bgViewModel.selectDomino(s1, s2) },
            pipCount = gameState.board.getPipCount(Player.Client),
            enable = !uiState.waiting,
            modifier = Modifier.weight(.15f)
        )

        ControlButtons(
            onUndo = { bgViewModel.undoMove() },
            onSubmit = { bgViewModel.sendTurn() },
            enableUndo = gameState.isUndoable && !uiState.waiting,
            enableSubmit = gameState.isTurnValid && !uiState.waiting,
            modifier = Modifier.weight(.1f),
            rotate = true
        )
    }
}