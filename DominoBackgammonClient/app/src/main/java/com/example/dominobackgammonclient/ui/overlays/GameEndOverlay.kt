package com.example.dominobackgammonclient.ui.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DarkRed

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
            .fillMaxSize()
            .background(Color.Gray)
            .alpha(.75f)
    ) {
        Text("Game Over!")

        Text(
            if (clientWin) "You win!"
            else if (clientLoss) "You lose!"
            else if (clientDisconnect) "You disconnected :("
            else if (opponentDisconnect) "Your opponent disconnected :("
            else ""
        )

        if (winType != 0)
            Text(
                if (winType == 2) "Gammon!"
                else if (winType == 3) "Backgammon!"
                else ""
            )

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(DarkRed, Color.White),
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Play again")
        }
    }
}