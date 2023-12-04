package com.example.dominobackgammonclient.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme
import com.example.dominobackgammonclient.ui.theme.TriangleShape

@Composable
fun Point(
    pointColour: BGColour,
    data: PointData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    rotate: Boolean = false
) {
    Box(modifier) {
        var mod: Modifier = Modifier.clickable { onClick() };
        if (rotate) mod = mod.rotate(180f)
        if (data.pointSelected) mod = mod.border(5.dp, Color.Green)

        Point(pointColour, mod)

        Column(
            verticalArrangement = if (rotate) Arrangement.Top else Arrangement.Bottom,
            modifier = Modifier
                .aspectRatio(.18f)
        ) {
            var i = 0
            // modifier highlights top / bottom piece if selected
            val pieceMod = if (data.pieceSelected) Modifier.border(5.dp, Color.Green) else Modifier

            if (data.count > 5) {
                if (!rotate) Piece(data.colour, data.count, pieceMod)
                i++
            }

            while (i < data.count && i < 5) {
                // highlight top / bottom piece
                if (!rotate && i == 0) Piece(data.colour, pieceMod)
                else if (rotate && i == data.count - 1) Piece(data.colour, pieceMod)

                else Piece(data.colour)
                i++
            }

            if (rotate && data.count > 5)  Piece(data.colour, data.count, pieceMod)
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
            PointData(3, BGColour.WHITE),
            { }
        )
    }
}

@Preview (widthDp = 50, heightDp = 300)
@Composable
fun PreviewBlackPoint() {
    DominoBackgammonClientTheme {
        Point(
            BGColour.BLACK,
            PointData(6, BGColour.WHITE),
            { }
        )
    }
}