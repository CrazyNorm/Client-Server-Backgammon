package com.example.dominobackgammonclient.ui.dominoes

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme
import game.dominoes.Domino
import game.dominoes.Hand
import ui.BGColour

@Composable
fun DominoListHorizontal(
    colour: BGColour,
    hand: Hand,
    onClick: (Int, Int) -> Unit,
    enable: Boolean,
    modifier: Modifier = Modifier
) {
    DominoListHorizontal(
        colour = colour,
        doubles = hand.doubles.asList().asReversed(),
        hand = hand.dominoes.asList().asReversed(),
        onClick = if (enable) onClick else { _, _ -> /* do nothing */},
        modifier = modifier
    )
}

@Composable
fun DominoListHorizontal(
    colour: BGColour,
    doubles: List<Domino>,
    hand: List<Domino?>,
    onClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dominoes = doubles + listOf(Domino(0,0)) + hand
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(9.dp),
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
                    val mod = if (item.isAvailable) Modifier
                    else if (item.isSelected) Modifier.border(5.dp, MaterialTheme.colorScheme.tertiary)
                    else if (item.isBlocked) Modifier.alpha(0.8f)
                    else Modifier.alpha(0.5f)

                    Domino(
                        colour = colour,
                        data = item,
                        onClick = { onClick(item.side1, item.side2) },
                        modifier = mod
                    )
                }
            }
        }
    }
}


@Preview
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
        DominoListHorizontal(
            BGColour.WHITE,
            doubles,
            hand,
            { _,_ -> },
            Modifier
                .background(Color(0xff118811))
        )
    }
}

@Preview
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
        DominoListHorizontal(
            BGColour.BLACK,
            doubles,
            hand,
            { _,_ -> },
            Modifier
                .background(Color(0xff118811))
        )
    }
}