package com.example.dominobackgammonclient.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DarkRed
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun ControlButtons(
    onUndo: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    enableUndo: Boolean = true,
    enableSubmit: Boolean = true
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.padding(vertical = 10.dp)
    ) {
        Button(
            onClick = onUndo,
            enabled = enableUndo,
            colors = ButtonDefaults.buttonColors(DarkRed, Color.White),
            modifier = Modifier
                .weight(0.5f)
        ) {
            Text("Undo")
        }

        Button(
            onClick = onSubmit,
            enabled = enableSubmit,
            colors = ButtonDefaults.buttonColors(DarkRed, Color.White),
            modifier = Modifier.weight(0.5f)
        ) {
            Text("Submit")
        }
    }
}


@Preview (widthDp = 500, heightDp = 100)
@Composable
fun PreviewControlButtons() {
    DominoBackgammonClientTheme {
        ControlButtons(
            onUndo = { },
            onSubmit = { }
        )
    }
}