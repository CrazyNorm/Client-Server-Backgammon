package com.example.dominobackgammonclient.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.BGColour

@Composable
fun PlayerStats(
    colour: BGColour,
    pipCount: Int,
    offCount: Int,
    modifier: Modifier = Modifier,
    offHighlight: Boolean = false,
    onClickOff: () -> Unit = { }
) {
    Row(modifier){
        PipCount(
            colour = colour,
            count = pipCount,
            modifier = Modifier.weight(0.5f)
        )

        var offMod = Modifier.weight(0.5f).clickable { onClickOff() }
        if (offHighlight) offMod = offMod.border(5.dp, Color.Green)
        BorneOff(
            colour = colour,
            count = offCount,
            modifier = offMod
        )
    }
}