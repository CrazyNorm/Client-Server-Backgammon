package com.example.dominobackgammonclient.ui.dominoes

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun Domino(
    data: DominoData,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        DominoSide(
            colour = data.colour,
            value = data.side1
        )
        DominoSide(
            colour = data.colour,
            value = data.side2
        )
    }
}


@Preview(widthDp = 50, heightDp = 150)
@Composable
fun PreviewWhiteDomino() {
    DominoBackgammonClientTheme {
        Domino(
            DominoData(BGColour.WHITE, 2, 1)
        )
    }
}

@Preview(widthDp = 50, heightDp = 150)
@Composable
fun PreviewBlackDomino() {
    DominoBackgammonClientTheme {
        Domino(
            DominoData(BGColour.BLACK, 6, 4)
        )
    }
}