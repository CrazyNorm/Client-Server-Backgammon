package com.example.dominobackgammonclient.ui.common

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme
import ui.BGColour

@Composable
fun BorneOff(
    colour: BGColour,
    count: Int,
    modifier: Modifier = Modifier,
    rotate: Boolean = false
) {
    if (rotate)
        BorneOffColumn(
            colour,
            count,
            modifier
        )
    else
        BorneOffRow(
            colour,
            count,
            modifier
        )
}

@Composable
fun BorneOffRow(
    colour: BGColour,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
            .border(3.dp, Color.Gray)
    ) {
        if (count < 15)
            // fills the empty space next to the borne off pieces
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
fun BorneOffColumn(
    colour: BGColour,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .border(3.dp, Color.Gray)
    ) {
        if (count < 15)
        // fills the empty space next to the borne off pieces
            Box(
                Modifier
                    .weight(1f - count / 15f)
                    .fillMaxWidth()
            )
        for (i in 1..count) {
            BorneOffPiece(
                colour,
                Modifier
                    .weight(1f / 15f)
                    .fillMaxWidth()
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


@Preview
@Composable
fun PreviewWhiteBorneOff() {
    DominoBackgammonClientTheme {
        BorneOff(BGColour.WHITE , 5)
    }
}

@Preview
@Composable
fun PreviewBlackBorneOff() {
    DominoBackgammonClientTheme {
        BorneOff(BGColour.BLACK , 10)
    }
}

@Preview
@Composable
fun PreviewEmptyBorneOff() {
    DominoBackgammonClientTheme {
        BorneOff(BGColour.WHITE, 0)
    }
}