package com.example.dominobackgammonclient.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.board.BorneOff
import com.example.dominobackgammonclient.ui.board.PipCount

@Composable
fun PlayerStats(
    colour: BGColour,
    pipCount: Int,
    offCount: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier){
        PipCount(
            colour = colour,
            count = pipCount,
            modifier = Modifier.weight(0.5f)
        )
        BorneOff(
            colour = colour,
            count = offCount,
            modifier = Modifier.weight(0.5f)
        )
    }
}