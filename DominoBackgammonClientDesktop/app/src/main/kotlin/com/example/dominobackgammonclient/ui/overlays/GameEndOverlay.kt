package com.example.dominobackgammonclient.ui.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun GameEndOverlay(
    onClick: () -> Unit,
    clientWin: Boolean,
    clientLoss: Boolean,
    winType: Int,
    clientDisconnect: Boolean,
    opponentDisconnect: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .alpha(.75f)
    ) {
        Text(
            text = "Game Over!",
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text =
                if (clientWin) "You win!"
                else if (clientLoss) "You lose!"
                else if (clientDisconnect) "You disconnected :("
                else if (opponentDisconnect) "Your opponent disconnected :("
                else "",
            color = MaterialTheme.colorScheme.onSurface
        )

        if (winType != 0)
            Text(
                text =
                    if (winType == 2) "Gammon!"
                    else if (winType == 3) "Backgammon!"
                    else "",
                color = MaterialTheme.colorScheme.onSurface
            )

        Button(
            onClick = onClick,
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Play again")
        }
    }
}