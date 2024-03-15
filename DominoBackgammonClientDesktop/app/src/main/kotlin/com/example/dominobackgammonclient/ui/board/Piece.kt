package com.example.dominobackgammonclient.ui.board

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme
import ui.BGColour


@Composable
fun Piece(
    colour: BGColour,
    modifier: Modifier = Modifier
) {
    if (colour == BGColour.WHITE) Piece(Color.White, modifier)
    else Piece(Color.Black, modifier)
}

@Composable
fun Piece(
    colour: BGColour,
    count: Int,
    modifier: Modifier = Modifier
) {
    if (colour == BGColour.WHITE) {
        Piece(
            color = Color.White,
            textColor = Color.Black,
            count = count,
            modifier = modifier
        )
    } else {
        Piece(
            color = Color.Black,
            textColor = Color.White,
            count = count,
            modifier = modifier
        )
    }
}


@Composable
fun Piece(
    color: Color,
    textColor: Color,
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(color)
            .border(3.dp, Color.Gray, CircleShape)
    ) {
        Text(
            text = count.toString(),
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun Piece(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(color)
            .border(3.dp, Color.Gray, CircleShape)
    )
}


@Preview
@Composable
fun PreviewWhitePiece() {
    DominoBackgammonClientTheme {
        Piece(colour = BGColour.WHITE)
    }
}

@Preview
@Composable
fun PreviewBlackPiece() {
    DominoBackgammonClientTheme {
        Piece(colour = BGColour.BLACK)
    }
}

@Preview
@Composable
fun PreviewWhiteLabelPiece() {
    DominoBackgammonClientTheme {
        Piece(
            colour = BGColour.WHITE,
            count = 7
        )
    }
}

@Preview
@Composable
fun PreviewBlackLabelPiece() {
    DominoBackgammonClientTheme {
        Piece(
            colour = BGColour.BLACK,
            count = 7
        )
    }
}