package com.example.dominobackgammonclient.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun BoardHalf(
    topData: List<PointData>,
    topInd: List<Int>,
    bottomData: List<PointData>,
    bottomInd: List<Int>,
    onClickPoint: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // top data works left to right
    // bottom data works right to left

    Box(modifier) {
        Row(
            Modifier
                .align(Alignment.TopCenter)
        ) {
            for (i in 0..5) {
                val colour = if (i % 2 == 0) BGColour.WHITE else BGColour.BLACK
                Point(
                    colour,
                    topData[i],
                    { onClickPoint(topInd[i]) },
                    Modifier
                        .weight(1f),
                    rotate = true
                )
            }
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
        ) {
            for (i in 0..5) {
                val colour = if (i % 2 == 0) BGColour.BLACK else BGColour.WHITE
                Point(
                    colour,
                    bottomData[5 - i],
                    { onClickPoint(bottomInd[i]) },
                    Modifier
                        .weight(1f)
                )
            }
        }
    }
}


@Preview (widthDp = 300, heightDp = 700)
@Composable
fun PreviewHalfBoard() {
    val bottomData = listOf(
        PointData(2, BGColour.BLACK),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(5, BGColour.WHITE)
    )
    val topData = listOf(
        PointData(5, BGColour.BLACK),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(2, BGColour.WHITE)
    )

    DominoBackgammonClientTheme {
        BoardHalf(
            topData,
            emptyList(),
            bottomData,
            emptyList(),
            { },
            Modifier
                .fillMaxSize()
                .background(Color(0xff118811))
        )
    }
}