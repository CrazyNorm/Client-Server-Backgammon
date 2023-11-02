package com.example.dominobackgammonclient.ui.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun Bar(
    white: Int,
    black: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Box(Modifier.weight(1f)) {
            if (white == 1) {
                Piece(
                    BGColour.WHITE,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else if (white > 1) {
                Piece(
                    BGColour.WHITE,
                    white,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }

        Box(Modifier.weight(1f)) {
            if (black == 1) {
                Piece(
                    BGColour.BLACK,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            } else if (black > 1) {
                Piece(
                    BGColour.BLACK,
                    black,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}


@Preview (widthDp = 50, heightDp = 500)
@Composable
fun PreviewBarEmpty() {
    DominoBackgammonClientTheme {
        Bar(white = 0, black = 0)
    }
}

@Preview (widthDp = 50, heightDp = 500)
@Composable
fun PreviewBarOne() {
    DominoBackgammonClientTheme {
        Bar(white = 1, black = 0)
    }
}

@Preview (widthDp = 50, heightDp = 500)
@Composable
fun PreviewBarFull() {
    DominoBackgammonClientTheme {
        Bar(white = 4, black = 5)
    }
}