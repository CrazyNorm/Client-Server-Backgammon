package com.example.dominobackgammonclient.ui.dominoes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun DominoList(
    colour: BGColour,
    doubles: List<DominoData>,
    hand: List<DominoData>,
    modifier: Modifier = Modifier
) {
    val dominoes = doubles + hand
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(5.dp),
        modifier = modifier
    ) {
        items(dominoes) { item ->
            if (item.available) Domino(colour, item)
            else Domino(colour, item, Modifier.alpha(0.5f))
        }
    }
}


@Preview(widthDp = 300, heightDp = 100)
@Composable
fun PreviewWhiteHand() {
    val doubles = listOf(
        DominoData(6, 6, available = false),
        DominoData(3, 3, available = false),
        DominoData(1, 1)
    )
    val hand = listOf(
        DominoData(6, 4),
        DominoData(6, 2),
        DominoData(5, 4),
        DominoData(5, 3),
        DominoData(5, 1),
        DominoData(4, 2),
        DominoData(3, 1),
        DominoData(2, 1)
    )

    DominoBackgammonClientTheme {
        DominoList(
            BGColour.WHITE,
            doubles,
            hand,
            Modifier
                .background(Color(0xff118811))
        )
    }
}

@Preview(widthDp = 300, heightDp = 100)
@Composable
fun PreviewBlackHand() {
    val doubles = listOf(
        DominoData(5, 5, available = false),
        DominoData(4, 4, available = false),
        DominoData(2, 2)
    )
    val hand = listOf(
        DominoData(6, 5),
        DominoData(6, 3),
        DominoData(6, 1),
        DominoData(5, 2),
        DominoData(4, 3),
        DominoData(4, 1),
        DominoData(3, 2)
    )

    DominoBackgammonClientTheme {
        DominoList(
            BGColour.BLACK,
            doubles,
            hand,
            Modifier
                .background(Color(0xff118811))
        )
    }
}