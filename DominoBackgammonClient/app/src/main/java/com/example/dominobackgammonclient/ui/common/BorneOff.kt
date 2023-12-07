package com.example.dominobackgammonclient.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun BorneOff(
    colour: BGColour,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
            .border(3.dp, Color.Gray)
    ) {
        Box(
            Modifier
                .weight(1f - count / 15f)
                .fillMaxHeight()
        )
        for (i in 1..count) {
            BorneOffPiece(
                colour,
                Modifier
                    .weight(1f / 15f)
                    .fillMaxHeight()
                    .border(1.dp, Color.Gray)
            )
        }
    }
}

@Composable
fun BorneOffPiece(
    colour: BGColour,
    modifier: Modifier = Modifier
) {
    val pieceColour = if (colour == BGColour.WHITE) Color.White else Color.Black
    Box(modifier.background(pieceColour))
}


@Preview(widthDp = 300, heightDp = 50)
@Composable
fun PreviewWhiteBorneOff() {
    DominoBackgammonClientTheme {
        BorneOff(BGColour.WHITE , 5)
    }
}

@Preview(widthDp = 300, heightDp = 50)
@Composable
fun PreviewBlackBorneOff() {
    DominoBackgammonClientTheme {
        BorneOff(BGColour.BLACK , 10)
    }
}

@Preview(widthDp = 300, heightDp = 50)
@Composable
fun PreviewEmptyBorneOff() {
    DominoBackgammonClientTheme {
        BorneOff(BGColour.WHITE, 0)
    }
}