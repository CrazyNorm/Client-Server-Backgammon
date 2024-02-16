package com.example.dominobackgammonclient.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
    enableSubmit: Boolean = true,
    rotate: Boolean = false
) {
    if (rotate)
        ControlButtonsColumn(
            onUndo,
            onSubmit,
            modifier,
            enableUndo,
            enableSubmit
        )
    else
        ControlButtonsRow(
            onUndo,
            onSubmit,
            modifier,
            enableUndo,
            enableSubmit
        )
}

@Composable
fun ControlButtonsRow(
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
            modifier = Modifier.weight(0.5f)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
        }

        Button(
            onClick = onSubmit,
            enabled = enableSubmit,
            colors = ButtonDefaults.buttonColors(DarkRed, Color.White),
            modifier = Modifier.weight(0.5f)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Submit")
        }
    }
}

@Composable
fun ControlButtonsColumn(
    onUndo: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    enableUndo: Boolean = true,
    enableSubmit: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.padding(horizontal = 10.dp)
    ) {
        Button(
            onClick = onSubmit,
            enabled = enableSubmit,
            colors = ButtonDefaults.buttonColors(DarkRed, Color.White),
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier.weight(0.5f)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Submit")
        }

        Button(
            onClick = onUndo,
            enabled = enableUndo,
            colors = ButtonDefaults.buttonColors(DarkRed, Color.White),
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier.weight(0.5f)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
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