package com.example.dominobackgammonclient.ui.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun ConnectOverlay(
    onClick: () -> Unit,
    connectionFailed: Boolean,
    disableButton: Boolean,
    playerName: String,
    onNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .alpha(.75f)
    ) {
        // name text box
        Column {
            Text(
                text = "Enter your display name:",
                color = MaterialTheme.colorScheme.onSurface
            )
            TextField(playerName, onNameChanged)
        }

        // connect button
        Column {
            if (connectionFailed)
                Text(
                    text = "Failed to connect",
                    style = TextStyle(Color.Red)
                )

            Button(
                onClick = onClick,
                enabled = !disableButton,
                modifier = Modifier.padding(10.dp)
            ) {
                Text("Connect")
            }
        }
    }
}



@Preview(widthDp = 500, heightDp = 500)
@Composable
fun PreviewBoardWithBar() {
    DominoBackgammonClientTheme {
        ConnectOverlay(
            onClick = { },
            connectionFailed = false,
            disableButton = false,
            playerName = "Player",
            onNameChanged = { _ -> })
    }
}