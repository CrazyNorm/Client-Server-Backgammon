package com.example.dominobackgammonclient.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PlayersTopBar(
    clientName: String,
    clientColour: BGColour,
    opponentName: String,
    opponentColour: BGColour,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 10.dp)
    ){
        Text(
            opponentName,
            color = if (opponentColour == BGColour.WHITE) Color.Black else Color.White,
            modifier = Modifier
                .weight(0.4f)
                .padding(bottom = 3.dp)
                .background(
                    color = if (opponentColour == BGColour.WHITE) Color.White else Color.Black,
                    shape = RoundedCornerShape(5.dp)
                ),
            textAlign = TextAlign.Center
        )

        Text(
            "vs",
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.weight(0.2f),
            textAlign = TextAlign.Center
        )

        Text(
            clientName,
            color = if (clientColour == BGColour.WHITE) Color.Black else Color.White,
            modifier = Modifier
                .weight(0.4f)
                .padding(bottom = 3.dp)
                .background(
                    color = if (clientColour == BGColour.WHITE) Color.White else Color.Black,
                    shape = RoundedCornerShape(5.dp)
                ),
            textAlign = TextAlign.Center
        )
    }
}