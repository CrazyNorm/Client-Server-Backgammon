package com.example.dominobackgammonclient.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.dominobackgammonclient.game.board.Board
import com.example.dominobackgammonclient.game.common.Player
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun Board(
    board: Board,
    client: BGColour,
    opponent: BGColour,
    modifier: Modifier = Modifier
) {
    val pointsData = mutableListOf<PointData?>()
    for (p in 1..24) {
        val point = board.getPoint(p)

        val data = when (point.player) {
            null -> null
            Player.Client -> PointData(point.count, client)
            Player.Opponent -> PointData(point.count, opponent)
        }

        pointsData.add(data)
    }

    val barData = arrayOf(board.getBarCount(Player.Client), board.getBarCount(Player.Opponent))

    Board(pointsData, barData, modifier)
}

@Composable
fun Board(
    data: List<PointData?>,
    bar: Array<Int>,
    modifier: Modifier = Modifier
) {
    val chunkedData = data.chunked(6)
    val pointWeight = 1f / 14f
    Row(modifier) {
        BoardHalf(
            chunkedData[2],
            chunkedData[1],
            modifier = Modifier
                .weight(6 * pointWeight)
                .fillMaxHeight()
        )

        Spacer(
            modifier = Modifier
                .weight(pointWeight / 2)
                .fillMaxHeight()
                .background(Color.Gray)
        )

        Bar(
            bar[0],
            bar[1],
            Modifier
                .weight(pointWeight)
                .background(Color.Gray)
        )

        Spacer(
            modifier = Modifier
                .weight(pointWeight / 2)
                .fillMaxHeight()
                .background(Color.Gray)
        )

        BoardHalf(
            chunkedData[3],
            chunkedData[0],
            modifier = Modifier
                .weight(6 * pointWeight)
                .fillMaxHeight()
        )
    }
}


@Preview(widthDp = 500, heightDp = 500)
@Composable
fun PreviewBoard() {
    val data = listOf(
        PointData(2, BGColour.BLACK),
        null,
        null,
        null,
        null,
        PointData(5, BGColour.WHITE),
        null,
        PointData(3, BGColour.WHITE),
        null,
        null,
        null,
        PointData(5, BGColour.BLACK),
        PointData(5, BGColour.WHITE),
        null,
        null,
        null,
        PointData(3, BGColour.BLACK),
        null,
        PointData(5, BGColour.BLACK),
        null,
        null,
        null,
        null,
        PointData(2, BGColour.WHITE)
    )

    DominoBackgammonClientTheme {
        Board(
            data,
            arrayOf(0, 0),
            Modifier
                .fillMaxSize()
                .background(Color(0xff118811))
        )
    }
}

@Preview(widthDp = 500, heightDp = 500)
@Composable
fun PreviewBoardWithBar() {
    val data = listOf(
        PointData(2, BGColour.BLACK),
        null,
        null,
        null,
        null,
        PointData(4, BGColour.WHITE),
        null,
        PointData(3, BGColour.WHITE),
        null,
        null,
        null,
        PointData(3, BGColour.BLACK),
        PointData(6, BGColour.WHITE),
        null,
        null,
        null,
        PointData(3, BGColour.BLACK),
        null,
        PointData(4, BGColour.BLACK),
        null,
        null,
        null,
        null,
        PointData(1, BGColour.WHITE)
    )

    DominoBackgammonClientTheme {
        Board(
            data,
            arrayOf(1, 3),
            Modifier
                .fillMaxSize()
                .background(Color(0xff118811))
        )
    }
}