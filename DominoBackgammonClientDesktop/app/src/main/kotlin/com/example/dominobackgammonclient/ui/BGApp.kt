package com.example.dominobackgammonclient.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.dominobackgammonclient.ui.common.BGScreenLandscape
import com.example.dominobackgammonclient.ui.common.BGScreenPortrait
import com.example.dominobackgammonclient.ui.common.PlayersTopBar
import com.example.dominobackgammonclient.ui.overlays.ConnectOverlay
import com.example.dominobackgammonclient.ui.overlays.GameEndOverlay
import com.example.dominobackgammonclient.ui.overlays.WaitingOverlay
import game.common.Player

@Composable
fun BGApp(
    landscape: Boolean,
    bgViewModel: BGViewModel
) {
    val gameState by bgViewModel.gameState.collectAsState()
    val uiState by bgViewModel.uiState.collectAsState()


    // overlays
    if (!uiState.connected)
        ConnectOverlay(
            onConnect = { bgViewModel.sendConnect() },
            connectionFailed = uiState.connectionFailed,
            disableButton = uiState.connecting,
            playerName = uiState.playerName,
            placeholderName = uiState.nameDefault,
            onNameChanged = { name -> bgViewModel.updatePlayerName(name) },
            aiOpponent = uiState.aiOpponent,
            onOpponentTypeChanged = { ai -> bgViewModel.updateAIOpponent(ai) },
            opponentName = uiState.opponentName,
            placeholderOpponent = uiState.opponentDefault,
            onOpponentChanged = { name -> bgViewModel.updateOpponentName(name) },
            aiType = uiState.aiType,
            aiTypeList = uiState.aiTypes,
            onAITypeChanged = { type -> bgViewModel.updateAIType(type) },
            rotate = false
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


    else Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.background(MaterialTheme.colorScheme.primary)
    ) {
        PlayersTopBar(
            clientName = uiState.playerName,
            clientColour = gameState.getColour(Player.Client),
            opponentName = uiState.opponentName,
            opponentColour = gameState.getColour(Player.Opponent),
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        )

        if (landscape)
            BGScreenLandscape(
                bgViewModel,
                gameState,
                uiState,
                Modifier
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxWidth()
            )
        else
            BGScreenPortrait(
                bgViewModel,
                gameState,
                uiState,
                Modifier
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxHeight()
            )
    }
}