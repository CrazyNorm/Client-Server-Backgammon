package com.example.dominobackgammonclient.ui.overlays

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
fun ConnectOverlay(
    onConnect: () -> Unit,
    connectionFailed: Boolean,
    disableButton: Boolean,
    playerName: String,
    placeholderName: String,
    onNameChanged: (String) -> Unit,
    aiOpponent: Boolean,
    onOpponentTypeChanged: (Boolean) -> Unit,
    opponentName: String,
    placeholderOpponent: String,
    onOpponentChanged: (String) -> Unit,
    aiType: String,
    aiTypeList: List<String>,
    onAITypeChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    rotate: Boolean = false
) {
    if (!rotate)
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {

            OpponentDetails(
                aiOpponent = aiOpponent,
                onOpponentTypeChanged = onOpponentTypeChanged,
                opponentName = opponentName,
                placeholderOpponent = placeholderOpponent,
                onOpponentChanged = onOpponentChanged,
                aiType = aiType,
                aiTypeList = aiTypeList,
                onAITypeChanged = onAITypeChanged
            )

            ConnectDetails(
                onConnect = onConnect,
                connectionFailed = connectionFailed,
                disableButton = disableButton,
                playerName = playerName,
                placeholderName = placeholderName,
                onNameChanged = onNameChanged
            )
        }
    else
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {

            OpponentDetails(
                aiOpponent = aiOpponent,
                onOpponentTypeChanged = onOpponentTypeChanged,
                opponentName = opponentName,
                placeholderOpponent = placeholderOpponent,
                onOpponentChanged = onOpponentChanged,
                aiType = aiType,
                aiTypeList = aiTypeList,
                onAITypeChanged = onAITypeChanged,
                modifier = Modifier.weight(.5f)
            )

            ConnectDetails(
                onConnect = onConnect,
                connectionFailed = connectionFailed,
                disableButton = disableButton,
                playerName = playerName,
                placeholderName = placeholderName,
                onNameChanged = onNameChanged,
                modifier = Modifier.weight(.5f)
            )
        }
}

@Composable
fun OpponentDetails(
    aiOpponent: Boolean,
    onOpponentTypeChanged: (Boolean) -> Unit,
    opponentName: String,
    placeholderOpponent: String,
    onOpponentChanged: (String) -> Unit,
    aiType: String,
    aiTypeList: List<String>,
    onAITypeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedAIType by remember { mutableStateOf(false) }
    var dropDownWidth by remember { mutableIntStateOf(0) }

    // opponent type switch
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Human",
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(.4f)
            )
            Switch(
                checked = aiOpponent,
                onCheckedChange = onOpponentTypeChanged,
                modifier = Modifier.weight(.2f)
            )
            Text(
                text = "AI",
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(.4f)
            )
        }

        if (aiOpponent) {
            Box {
                TextField(
                    readOnly = true,
                    value = aiType,
                    onValueChange = { },
                    label = { Text("AI type:") },
                    trailingIcon = {
                        Icon(
                            if (expandedAIType) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            null,
                            Modifier.clickable { expandedAIType = !expandedAIType }
                        )
                    },
                    modifier = Modifier
                        .onFocusChanged { expandedAIType = it.isFocused }
                        .onSizeChanged { dropDownWidth = it.width }
                )
                DropdownMenu(
                    expanded = expandedAIType,
                    onDismissRequest = { expandedAIType = false },
                    modifier = Modifier.width(with(LocalDensity.current) { dropDownWidth.toDp() })
                ) {
                    aiTypeList.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                onAITypeChanged(type)
                                expandedAIType = false
                            }
                        )
                    }
                }
            }
        } else {
            var text by remember { mutableStateOf(opponentName) }
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    onOpponentChanged(it)
                },
                placeholder = { Text(placeholderOpponent) },
                label = { Text("Opponent:") },
                singleLine = true
            )
        }
    }
}

@Composable
fun ConnectDetails(
    onConnect: () -> Unit,
    connectionFailed: Boolean,
    disableButton: Boolean,
    playerName: String,
    placeholderName: String,
    onNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {

        // player name
        var text by remember { mutableStateOf(playerName) }
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(placeholderName) },
            label = { Text("Your name:") },
            singleLine = true
        )
        // connect button
        Column {
            if (connectionFailed)
                Text(
                    text = "Failed to connect. Try again",
                    style = TextStyle(Color.Red)
                )

            Button(
                onClick = {
                    onNameChanged(text)
                    onConnect()
                },
                enabled = !disableButton,
                modifier = Modifier.padding(10.dp)
            ) {
                Text("Connect")
            }
        }
    }
}


@Preview
@Composable
fun PreviewOverlay() {
    DominoBackgammonClientTheme {
        ConnectOverlay(
            onConnect = { },
            connectionFailed = false,
            disableButton = false,
            playerName = "",
            placeholderName = "Player",
            onNameChanged = { },
            aiOpponent = false,
            onOpponentTypeChanged = { },
            opponentName = "",
            placeholderOpponent = "any",
            onOpponentChanged = { },
            aiType = "",
            aiTypeList = listOf("1", "2"),
            onAITypeChanged = { }
        )
    }
}