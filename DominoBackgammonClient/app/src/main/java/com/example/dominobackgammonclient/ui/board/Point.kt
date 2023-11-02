package com.example.dominobackgammonclient.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme
import com.example.dominobackgammonclient.ui.theme.TriangleShape

@Composable
fun Point(
    pointColour: BGColour,
    data: PointData,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Point(pointColour)
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(.18f)
        ) {
            var i = 0

            if (data.count > 5) {
                Piece(data.colour, data.count, Modifier)
                i++
            }

            while (i < data.count && i < 5) {
                Piece(data.colour)
                i++
            }
        }
    }
}

@Composable
fun Point(
    colour: BGColour,
    modifier: Modifier = Modifier
) {
    if (colour == BGColour.WHITE) Point(Color.White, modifier)
    else Point(Color.Black, modifier)
}

@Composable
fun Point(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.18f)
            .clip(TriangleShape())
            .background(color)
    )
}


@Preview (widthDp = 50, heightDp = 300)
@Composable
fun PreviewWhitePoint() {
    DominoBackgammonClientTheme {
        Point(
            BGColour.WHITE,
            PointData(3, BGColour.WHITE)
        )
    }
}

@Preview (widthDp = 50, heightDp = 300)
@Composable
fun PreviewBlackPoint() {
    DominoBackgammonClientTheme {
        Point(
            BGColour.BLACK,
            PointData(6, BGColour.WHITE))
    }
}