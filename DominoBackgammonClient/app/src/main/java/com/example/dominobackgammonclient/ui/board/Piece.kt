package com.example.dominobackgammonclient.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

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
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
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