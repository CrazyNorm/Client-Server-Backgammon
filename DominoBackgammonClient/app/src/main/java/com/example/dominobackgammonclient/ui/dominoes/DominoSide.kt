package com.example.dominobackgammonclient.ui.dominoes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun DominoSide(
    colour: BGColour,
    value: Int,
    modifier: Modifier = Modifier
) {
    if (colour == BGColour.WHITE) {
        DominoSide(
            color = Color.White,
            textColor = Color.Black,
            value = value,
            modifier = modifier
        )
    } else {
        DominoSide(
            color = Color.Black,
            textColor = Color.White,
            value = value,
            modifier = modifier
        )
    }
}

@Composable
fun DominoSide(
    color: Color,
    textColor: Color,
    value: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(color)
            .border(3.dp, Color.Gray)
    ) {
        Text(
            text = value.toString(),
            textAlign = TextAlign.Center,
            color = textColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Preview(widthDp = 50, heightDp = 60)
@Composable
fun PreviewWhiteSide() {
    DominoBackgammonClientTheme {
        DominoSide(
            colour = BGColour.WHITE,
            value = 3
        )
    }
}

@Preview(widthDp = 50, heightDp = 60)
@Composable
fun PreviewBlackSide() {
    DominoBackgammonClientTheme {
        DominoSide(
            colour = BGColour.BLACK,
            value = 6
        )
    }
}