package com.example.dominobackgammonclient.ui.dominoes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.game.dominoes.Domino
import com.example.dominobackgammonclient.game.dominoes.Hand
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun DominoList(
    colour: BGColour,
    hand: Hand,
    modifier: Modifier = Modifier
) {
    DominoList(
        colour = colour,
        doubles = hand.doubles.asList().asReversed(),
        hand = hand.dominoes.asList().asReversed(),
        modifier = modifier
    )
}

@Composable
fun DominoList(
    colour: BGColour,
    doubles: List<Domino>,
    hand: List<Domino?>,
    modifier: Modifier = Modifier
) {
    val dominoes = doubles + listOf(Domino(0,0)) + hand
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(5.dp),
        modifier = modifier
    ) {
        items(dominoes) { item ->
            if (item != null) {
                if (item == Domino(0, 0)) {
                    Spacer(
                        Modifier
                            .width(5.dp)
                            .fillMaxHeight()
                            .background(Color.Gray)
                    )
                } else {
                    if (item.isAvailable) Domino(colour, item)
                    else Domino(colour, item, Modifier.alpha(0.5f))
                }
            }
        }
    }
}


@Preview(widthDp = 300, heightDp = 100)
@Composable
fun PreviewWhiteHand() {
    val doubles = listOf(
        Domino(6, 6),
        Domino(3, 3),
        Domino(1, 1)
    )
    doubles[0].use()
    doubles[1].use()

    val hand = listOf(
        Domino(6, 4),
        Domino(6, 2),
        Domino(5, 4),
        Domino(5, 3),
        Domino(5, 1),
        Domino(4, 2),
        Domino(3, 1),
        Domino(2, 1)
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
        Domino(5, 5),
        Domino(4, 4),
        Domino(2, 2)
    )
    doubles[0].use()
    doubles[1].use()

    val hand = listOf(
        Domino(6, 5),
        Domino(6, 3),
        Domino(6, 1),
        Domino(5, 2),
        Domino(4, 3),
        Domino(4, 1),
        Domino(3, 2)
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