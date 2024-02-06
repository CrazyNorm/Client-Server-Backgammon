package com.example.dominobackgammonclient.ui.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DarkRed

@Composable
fun ConnectOverlay(
    onClick: () -> Unit,
    connectionFailed: Boolean,
    disableButton: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Gray)
            .alpha(.75f)
    ) {
        if (connectionFailed)
            Text(
                text = "Failed to connect",
                style = TextStyle(Color.Red)
            )

        Button(
            onClick = onClick,
            enabled = !disableButton,
            colors = ButtonDefaults.buttonColors(DarkRed, Color.White),
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Connect to Localhost")
        }
    }
}