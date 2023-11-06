package com.example.dominobackgammonclient.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun PipCount(
    colour: BGColour,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Piece(colour)
        Text("Moves left:")
        Text(
            count.toString(),
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview (widthDp = 300, heightDp = 50)
@Composable
fun PreviewWhitePipCount() {
    DominoBackgammonClientTheme {
        PipCount(
            BGColour.WHITE,
            count = 167,
            Modifier.background(Color(0xff118811))
        )
    }
}

@Preview (widthDp = 300, heightDp = 50)
@Composable
fun PreviewBlackPipCount() {
    DominoBackgammonClientTheme {
        PipCount(
            BGColour.BLACK,
            count = 156,
            Modifier.background(Color(0xff118811))
        )
    }
}