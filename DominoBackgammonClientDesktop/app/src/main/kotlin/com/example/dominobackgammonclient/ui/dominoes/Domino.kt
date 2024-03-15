package com.example.dominobackgammonclient.ui.dominoes

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme
import game.dominoes.Domino
import ui.BGColour

@Composable
fun Domino(
    colour: BGColour,
    data: Domino,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    rotate: Boolean = false
) {
    if (rotate)
        DominoRow(
            colour,
            data,
            onClick,
            modifier
        )
    else
        DominoColumn(
            colour,
            data,
            onClick,
            modifier
        )
}

@Composable
fun DominoColumn(
    colour: BGColour,
    data: Domino,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
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

@Composable
fun DominoRow(
    colour: BGColour,
    data: Domino,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .aspectRatio(2f)
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


@Preview
@Composable
fun PreviewWhiteDomino() {
    DominoBackgammonClientTheme {
        Box(Modifier.fillMaxSize()) {
            Domino(
                BGColour.WHITE,
                Domino(2, 1),
                { },
                rotate = true
            )
        }
    }
}

@Preview
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