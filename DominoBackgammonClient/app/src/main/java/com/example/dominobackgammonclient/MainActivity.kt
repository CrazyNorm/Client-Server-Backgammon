package com.example.dominobackgammonclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.board.Board
import com.example.dominobackgammonclient.ui.board.BorneOff
import com.example.dominobackgammonclient.ui.board.PipCount
import com.example.dominobackgammonclient.ui.board.PointData
import com.example.dominobackgammonclient.ui.common.BGColour
import com.example.dominobackgammonclient.ui.dominoes.DominoData
import com.example.dominobackgammonclient.ui.dominoes.DominoList
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val board = listOf(
                PointData(2, BGColour.BLACK),
                null,
                null,
                null,
                null,
                PointData(3, BGColour.WHITE),
                null,
                PointData(3, BGColour.WHITE),
                null,
                null,
                null,
                PointData(5, BGColour.BLACK),
                PointData(4, BGColour.WHITE),
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

            val wDoubles = listOf(
                DominoData(6, 6, available = false),
                DominoData(3, 3, available = false),
                DominoData(1, 1)
            )
            val wHand = listOf(
                DominoData(6, 4),
                DominoData(6, 2),
                DominoData(5, 4),
                DominoData(5, 3),
                DominoData(5, 1),
                DominoData(4, 2),
                DominoData(3, 1),
                DominoData(2, 1)
            )

            val bDoubles = listOf(
                DominoData(5, 5, available = false),
                DominoData(4, 4, available = false),
                DominoData(2, 2)
            )
            val bHand = listOf(
                DominoData(6, 5),
                DominoData(6, 3),
                DominoData(6, 1),
                DominoData(5, 2),
                DominoData(4, 3),
                DominoData(4, 1),
                DominoData(3, 2)
            )

            DominoBackgammonClientTheme {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .background(Color(0xFF00BCD4))
                        .padding(vertical = 10.dp)
                ) {
                    DominoList(
                        colour = BGColour.BLACK,
                        doubles = bDoubles,
                        hand = bHand,
                        modifier = Modifier.weight(.2f)
                    )

                    Row(
                        Modifier
                            .weight(0.05f)
                            .padding(horizontal = 8.dp)
                    ){
                        PipCount(
                            colour = BGColour.BLACK,
                            count = 167,
                            modifier = Modifier.weight(0.5f)
                        )
                        BorneOff(
                            colour = BGColour.BLACK,
                            count = 0,
                            modifier = Modifier.weight(0.5f)
                        )
                    }

                    Board(
                        data = board,
                        bar = arrayOf(1, 0),
                        modifier = Modifier
                            .background(Color(0xFF4CAF50))
                            .weight(.5f)
                    )

                    Row(
                        Modifier
                            .weight(0.05f)
                            .padding(horizontal = 8.dp)
                    ){
                        PipCount(
                            colour = BGColour.WHITE,
                            count = 167,
                            modifier = Modifier.weight(0.5f)
                        )
                        BorneOff(
                            colour = BGColour.WHITE,
                            count = 2,
                            modifier = Modifier.weight(0.5f)
                        )
                    }

                    DominoList(
                        colour = BGColour.WHITE,
                        doubles = wDoubles,
                        hand = wHand,
                        modifier = Modifier.weight(.2f)
                    )
                }
            }
        }
    }
}