package com.example.dominobackgammonclient.ui.dominoes

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

// TODO grey out used / unavailable dominoes?

@Composable
fun Domino(
    colour: BGColour,
    data: DominoData,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        DominoSide(
            colour = colour,
            value = data.side1,
            Modifier.weight(1f)
        )
        DominoSide(
            colour = colour,
            value = data.side2,
            Modifier.weight(1f)
        )
    }
}


@Preview(widthDp = 50, heightDp = 150)
@Composable
fun PreviewWhiteDomino() {
    DominoBackgammonClientTheme {
        Domino(
            BGColour.WHITE,
            DominoData(2, 1)
        )
    }
}

@Preview(widthDp = 50, heightDp = 150)
@Composable
fun PreviewBlackDomino() {
    DominoBackgammonClientTheme {
        Domino(
            BGColour.BLACK,
            DominoData(6, 4)
        )
    }
}