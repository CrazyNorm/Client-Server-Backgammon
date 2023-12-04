package com.example.dominobackgammonclient.ui.dominoes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dominobackgammonclient.game.dominoes.Domino
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun Domino(
    colour: BGColour,
    data: Domino,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier
        .aspectRatio(0.5f)
        .clickable { onClick() }
    ) {
        DominoSide(
            colour = colour,
            value = data.side1,
            isUsed = data.isUsed,
            modifier = Modifier.weight(1f)
        )
        DominoSide(
            colour = colour,
            value = data.side2,
            isUsed = data.isUsed,
            modifier = Modifier.weight(1f)
        )
    }
}


@Preview(widthDp = 50, heightDp = 150)
@Composable
fun PreviewWhiteDomino() {
    DominoBackgammonClientTheme {
        Domino(
            BGColour.WHITE,
            Domino(2, 1),
            { }
        )
    }
}

@Preview(widthDp = 50, heightDp = 150)
@Composable
fun PreviewBlackDomino() {
    DominoBackgammonClientTheme {
        Domino(
            BGColour.BLACK,
            Domino(6, 4),
            { }
        )
    }
}