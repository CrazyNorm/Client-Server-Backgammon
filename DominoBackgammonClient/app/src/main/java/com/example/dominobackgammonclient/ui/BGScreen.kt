package com.example.dominobackgammonclient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dominobackgammonclient.game.common.Player
import com.example.dominobackgammonclient.ui.board.Board
import com.example.dominobackgammonclient.ui.common.PlayerStats
import com.example.dominobackgammonclient.ui.dominoes.DominoList

@Composable
fun BGScreen(
    bgViewModel: BGViewModel = viewModel()
) {
    val gameState by bgViewModel.gameState.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(Color(0xFF00BCD4))
            .padding(vertical = 10.dp)
    ) {

        DominoList(
            colour = gameState.getColour(Player.Opponent),
            hand = gameState.getHand(Player.Opponent),
            onClick = { _,_ -> /* Do nothing for opponent hand */ },
            modifier = Modifier.weight(.2f)
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
            modifier = Modifier
                .background(Color(0xFF4CAF50))
                .weight(.5f)
        )

        PlayerStats(
            colour = gameState.getColour(Player.Client),
            pipCount = gameState.board.getPipCount(Player.Client),
            offCount = gameState.board.getOffCount(Player.Client),
            modifier = Modifier
                .weight(0.05f)
                .padding(horizontal = 8.dp)
        )

        DominoList(
            colour = gameState.getColour(Player.Client),
            hand = gameState.getHand(Player.Client),
            onClick = { s1, s2 -> bgViewModel.selectDomino(s1, s2) },
            modifier = Modifier.weight(.2f)
        )
    }
}