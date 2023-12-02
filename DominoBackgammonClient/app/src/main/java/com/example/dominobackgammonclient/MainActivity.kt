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
import com.example.dominobackgammonclient.ui.BGScreen
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
            DominoBackgammonClientTheme {
                BGScreen()
            }
        }
    }
}