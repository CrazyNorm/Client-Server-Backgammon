package com.example.dominobackgammonclient.ui.board

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme
import game.board.Board
import game.common.Player
import ui.BGColour

@Composable
fun Board(
    board: Board,
    client: BGColour,
    opponent: BGColour,
    onClickPoint: (Int) -> Unit,
    modifier: Modifier = Modifier,
    highlightedPoints: List<Int> = emptyList(),
    highlightedPieces: List<Int> = emptyList()
) {
    val pointsData = mutableListOf<PointData>()
    for (p in 1..24) {
        val point = board.getPoint(p)

        val data = when (point.player) {
            null -> PointData(0)
            Player.Client -> PointData(point.count, client)
            Player.Opponent -> PointData(point.count, opponent)
        }

        if (highlightedPoints.contains(p)) data.pointSelected = true
        if (highlightedPieces.contains(p)) data.pieceSelected = true

        pointsData.add(data)
    }

    val barData = arrayOf(
        PointData(board.getBarCount(Player.Client), client),
        PointData(board.getBarCount(Player.Opponent), opponent)
    )
    val highlightBar =
        if (highlightedPoints.contains(25)) true
        else if (highlightedPieces.contains(25)) true
        else false

    Board(
        pointsData,
        barData,
        highlightBar,
        onClickPoint,
        modifier
    )
}

@Composable
fun Board(
    data: List<PointData>,
    bar: Array<PointData>,
    highlightBar: Boolean,
    onClickPoint: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val chunkedData = data.chunked(6)
    val chunkedIndices = listOf((6 downTo 1), (12 downTo 7), (13..18), (19..24))
    val pointWeight = 1f / 14f
    Row(modifier) {
        BoardHalf(
            chunkedData[2],
            chunkedIndices[2].toList(),
            chunkedData[1],
            chunkedIndices[1].toList(),
            onClickPoint,
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
            { onClickPoint(25) },
            highlightClient = highlightBar,
            modifier = Modifier
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
            chunkedIndices[3].toList(),
            chunkedData[0],
            chunkedIndices[0].toList(),
            onClickPoint,
            modifier = Modifier
                .weight(6 * pointWeight)
                .fillMaxHeight()
        )
    }
}


@Preview
@Composable
fun PreviewBoard() {
    val data = listOf(
        PointData(2, BGColour.BLACK),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(5, BGColour.WHITE),
        PointData(0),
        PointData(3, BGColour.WHITE),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(5, BGColour.BLACK),
        PointData(5, BGColour.WHITE),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(3, BGColour.BLACK),
        PointData(0),
        PointData(5, BGColour.BLACK),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(2, BGColour.WHITE)
    )

    DominoBackgammonClientTheme {
        Board(
            data,
            arrayOf(PointData(0), PointData(0)),
            true,
            { },
            Modifier
                .fillMaxSize()
                .background(Color(0xff118811))
        )
    }
}

@Preview
@Composable
fun PreviewBoardWithBar() {
    val data = listOf(
        PointData(2, BGColour.BLACK),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(4, BGColour.WHITE),
        PointData(0),
        PointData(3, BGColour.WHITE),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(3, BGColour.BLACK),
        PointData(6, BGColour.WHITE),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(3, BGColour.BLACK),
        PointData(0),
        PointData(4, BGColour.BLACK),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(0),
        PointData(1, BGColour.WHITE)
    )

    DominoBackgammonClientTheme {
        Board(
            data,
            arrayOf(PointData(1, BGColour.WHITE), PointData(3, BGColour.BLACK)),
            true,
            { },
            Modifier
                .fillMaxSize()
                .background(Color(0xff118811))
        )
    }
}