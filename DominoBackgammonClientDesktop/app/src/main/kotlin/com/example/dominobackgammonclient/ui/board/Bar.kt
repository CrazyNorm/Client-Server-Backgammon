package com.example.dominobackgammonclient.ui.board

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme
import ui.BGColour

@Composable
fun Bar(
    client: PointData,
    opponent: PointData,
    onClickClient: () -> Unit,
    modifier: Modifier = Modifier,
    highlightClient: Boolean = false
) {
    val cMod = if (highlightClient) Modifier.border(5.dp, MaterialTheme.colorScheme.tertiary) else Modifier
    Column(modifier) {
        Box(Modifier.weight(1f)) {
            if (client.count == 1) {
                Piece(
                    client.colour,
                    modifier = cMod
                        .align(Alignment.Center)
                        .clickable { onClickClient() }
                )
            } else if (client.count > 1) {
                Piece(
                    client.colour,
                    client.count,
                    modifier = cMod
                        .align(Alignment.Center)
                        .clickable { onClickClient() }
                )
            }
        }

        Box(Modifier.weight(1f)) {
            if (opponent.count == 1) {
                Piece(
                    opponent.colour,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else if (opponent.count > 1) {
                Piece(
                    opponent.colour,
                    opponent.count,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewBarEmpty() {
    DominoBackgammonClientTheme {
        Bar(PointData(0), PointData(0), { })
    }
}

@Preview
@Composable
fun PreviewBarOne() {
    DominoBackgammonClientTheme {
        Bar(PointData(1, BGColour.WHITE), PointData(0), { })
    }
}

@Preview
@Composable
fun PreviewBarFull() {
    DominoBackgammonClientTheme {
        Bar(PointData(4, BGColour.WHITE), PointData(5, BGColour.BLACK), { })
    }
}